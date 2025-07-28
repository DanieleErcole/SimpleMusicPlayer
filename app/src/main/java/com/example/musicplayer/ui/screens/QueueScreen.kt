package com.example.musicplayer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.SelectionToolbar
import com.example.musicplayer.ui.components.TrackItem
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.QueueVM
import com.example.musicplayer.ui.state.ReorderableQueueItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun QueueScreen(
    modifier: Modifier = Modifier,
    vm: QueueVM,
    dialogsVm: DialogsVM
) {
    val update = vm.update.collectAsStateWithLifecycle()
    var tracks by remember { mutableStateOf(emptyList<ReorderableQueueItem>()) }

    val selectedTracks = vm.selectedTracks.collectAsStateWithLifecycle()
    val selectionMode by remember { derivedStateOf { selectedTracks.value.isNotEmpty() } }

    LaunchedEffect(update.value) {
        withContext(Dispatchers.IO) {
            tracks = vm.queue().map { ReorderableQueueItem(it) }
        }
    }

    var fromPos by remember { mutableIntStateOf(-1) }
    var toPos by remember { mutableIntStateOf(-1) }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        tracks = tracks.toMutableList().apply {
            get(to.index).move(from.index)
            val removed = removeAt(from.index)
            removed.move(to.index)
            add(to.index, removed)
        }
        toPos = to.index
    }

    Column(
        modifier = modifier.padding(top = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Queue",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
            val cur = tracks.find { it.track.queuedItem.isCurrent }
            Text(
                text = "${(cur?.track?.queuedItem?.position)?.plus(1) ?: 0} / ${tracks.size}",
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
            TransparentButton(
                onClick = { dialogsVm.setConfirmDialog(title = "Clear the queue?") { vm.clearQueue() } },
                painter = painterResource(R.drawable.delete),
                contentDescription = "Clear queue",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        AnimatedVisibility(selectionMode) {
            SelectionToolbar(
                whileSelectedClick = {
                    if (tracks.size == selectedTracks.value.size)
                        vm.clearSelection()
                    else vm.selectList(tracks.map { it.position }.toSet())
                },
                onCloseClick = { vm.clearSelection() },
                onQueueClick = {
                    vm.queueAll(
                        tracks
                            .filter { it.position in selectedTracks.value }
                            .map { it.track.track }
                    )
                    vm.clearSelection()
                },
                onAddClick = {
                    dialogsVm.setAddDialog(
                        tracks = tracks
                            .filter { it.position in selectedTracks.value }
                            .map { it.track.track },
                    ) { vm.clearSelection() }
                },
                onDeleteClick = {
                    vm.dequeueAll(
                        tracks
                            .filter { it.position in selectedTracks.value }
                            .map { it.track }
                    )
                    vm.clearSelection()
                },
                selectionSize = selectedTracks.value.size,
                allSelected = tracks.size == selectedTracks.value.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
        }
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .weight(.8f)
        ) {
            items(tracks, key = { it.hashCode() }) { item ->
                val mod = if (item.track.queuedItem.isCurrent)
                    Modifier.border(1.dp, MaterialTheme.colorScheme.primary)
                else Modifier

                ReorderableItem(reorderableLazyListState, key = item.hashCode()) { isDragging ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = mod.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.draggableHandle(
                                enabled = !selectionMode,
                                onDragStarted = { fromPos = item.position },
                                onDragStopped = { vm.moveTrack(fromPos, toPos) }
                            ),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.drag_handle),
                                contentDescription = "dragHandle",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        TrackItem(
                            track = item.track.track,
                            onItemClick = {
                                if (selectionMode)
                                    vm.selectTrack(item)
                                else vm.playTrack(item.position)
                            },
                            onLongPress = { vm.selectTrack(item) },
                            isSelected = item.position in selectedTracks.value,
                            selectionMode = selectionMode,
                            onAddClick = { dialogsVm.setAddDialog(listOf(item.track.track)) },
                            onInfoClick = { dialogsVm.setInfoDialog(item.track.track) },
                            onQueueClick = { vm.queueAll(listOf(item.track.track)) }
                        )
                    }
                }
            }
        }
    }
}