package com.example.musicplayer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.Divider
import com.example.musicplayer.ui.components.SelectionToolbar
import com.example.musicplayer.ui.components.TrackItem
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.QueueVM
import com.example.musicplayer.ui.state.ReorderableQueueItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

data class ReorderableDragHandle(
    val scope: ReorderableCollectionItemScope,
    val dragEnabled: Boolean
)

@Composable
fun QueueScreen(
    modifier: Modifier = Modifier,
    vm: QueueVM,
    dialogsVm: DialogsVM,
    horizontalLayout: Boolean,
) {
    val ctx = LocalContext.current
    val update = vm.update.collectAsStateWithLifecycle()
    var tracks by remember { mutableStateOf(emptyList<ReorderableQueueItem>()) }

    val selectedTracks = vm.selectedTracks.collectAsStateWithLifecycle()
    val selectionMode by remember { derivedStateOf { selectedTracks.value.isNotEmpty() } }

    LaunchedEffect(update.value) {
        withContext(Dispatchers.IO) {
            tracks = vm.queue().map { ReorderableQueueItem(it) }
        }
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        tracks = tracks.toMutableList().apply {
            get(to.index).move(from.index)
            val removed = removeAt(from.index)
            removed.move(to.index)
            add(to.index, removed)
        }
        vm.moveTrack(from.index, to.index)
    }

    var borderVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .collect { index -> borderVisible = index > 0 }
    }

    Column(
        modifier = modifier.padding(top = dimensionResource(R.dimen.padding_small))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                .padding(bottom = if (!selectionMode) 8.dp else 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.queue_page),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                )
                TransparentButton(
                    onClick = { dialogsVm.setConfirmDialog(title = ctx.getString(R.string.clear_queue_title)) { vm.clearQueue() } },
                    painter = painterResource(R.drawable.delete),
                    contentDescription = "Clear queue",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
                )
            }

            val cur = tracks.find { it.track.queuedItem.isCurrent }
            Text(
                text = "${(cur?.position)?.plus(1) ?: 0} / ${tracks.size}",
                fontSize = 12.sp,
                lineHeight = 14.sp,
                modifier = Modifier.align(Alignment.Center)
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
                onDequeueClick = {
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
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium))
            )
        }
        AnimatedVisibility(borderVisible) {
            Divider()
        }
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(.8f)
                .testTag("QueueList")
        ) {
            items(tracks, key = { it.hashCode() }) { item ->
                ReorderableItem(reorderableLazyListState, key = item.hashCode()) { isDragging ->
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
                        onQueueClick = { vm.queueAll(listOf(item.track.track)) },
                        onDequeueClick = { vm.dequeueAll(listOf(item.track)) },
                        dragHandle = ReorderableDragHandle(
                            scope = this,
                            dragEnabled = !selectionMode
                        ),
                        horizontalLayout = horizontalLayout,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                1.dp,
                                if (item.track.queuedItem.isCurrent) MaterialTheme.colorScheme.primary
                                else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .testTag(if (item.track.queuedItem.isCurrent) "Current" else "QueueItem")
                    )
                }
            }
        }
    }
}