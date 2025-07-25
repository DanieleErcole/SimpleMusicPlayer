package com.example.musicplayer.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.Divider
import com.example.musicplayer.ui.components.TrackItem
import com.example.musicplayer.ui.state.QueueVM
import kotlinx.coroutines.flow.first
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun QueueScreen(
    modifier: Modifier = Modifier,
    vm: QueueVM
) {
    val state = vm.queue.collectAsStateWithLifecycle()
    val tracks by remember { derivedStateOf { state.value } }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        /*tracks = tracks.toMutableList().apply {
            add(from.index, removeAt(to.index))
        }*/
        vm.moveTrack(from.index, to.index)
    }

    Column(
        modifier = modifier.padding(top = 8.dp)
    ) {
        Divider()
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(top = 16.dp)
                .weight(.8f)
        ) {
            itemsIndexed(tracks, key = { index, _ -> index }) { index, item ->
                val mod = if (item.queuedItem.isCurrent)
                    Modifier.border(1.dp, MaterialTheme.colorScheme.primary)
                else Modifier

                ReorderableItem(reorderableLazyListState, key = index) { isDragging ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = mod.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.draggableHandle(),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.drag_handle),
                                contentDescription = "dragHandle",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        TrackItem(
                            track = item.track,
                            onItemClick = {  },
                            onLongPress = {  },
                            isSelected = false,
                            selectionMode = false,
                            onAddClick = {  },
                            onInfoClick = {  },
                            onQueueClick = {  }
                        )
                    }
                }
            }
        }
    }
}