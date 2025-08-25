package com.example.musicplayer.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicplayer.R
import com.example.musicplayer.data.TrackWithAlbum
import com.example.musicplayer.ui.AppScreen
import com.example.musicplayer.ui.screens.ReorderableDragHandle
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.TrackListVM
import com.example.musicplayer.utils.formatTimestamp
import kotlinx.coroutines.Dispatchers
import kotlin.collections.map

@Composable
fun TrackList(
    listVm: TrackListVM,
    dialogsVm: DialogsVM,
    navController: NavController,
    listTitle: String,
    onBackClick: (() -> Unit)? = null,
    onRemoveClick: ((List<TrackWithAlbum>) -> Unit)? = null,
    filters: (@Composable () -> Unit)? = null,
    objectToolsBtn: (@Composable (List<TrackWithAlbum>) -> Unit)? = null,
    mustReplaceQueue: Boolean = false,
    horizontalLayout: Boolean,
    modifier: Modifier
) {
    val tracks = listVm.tracks.collectAsStateWithLifecycle()
    val searchStr = listVm.searchString.collectAsStateWithLifecycle()
    val selectedTracks = listVm.selectedTracks.collectAsStateWithLifecycle()

    val selectionMode by remember { derivedStateOf { selectedTracks.value.isNotEmpty() } }
    Column(
        modifier = modifier
            .padding(top = dimensionResource(R.dimen.padding_very_small))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                onBackClick?.let {
                    BackHandler(
                        enabled = true,
                        onBack = it
                    )

                    TransparentButton(
                        onClick = it,
                        painter = painterResource(R.drawable.back),
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
                    )
                }
                Text(
                    text = listTitle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier
                        .testTag("PageTitle")
                        .padding(start = onBackClick?.let { dimensionResource(R.dimen.padding_small) } ?: 0.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                filters?.invoke()
                objectToolsBtn?.invoke(tracks.value)
            }
        }
        AnimatedVisibility(selectionMode) {
            SelectionToolbar(
                whileSelectedClick = {
                    if (tracks.value.size == selectedTracks.value.size)
                        listVm.clearSelection()
                    else listVm.selectList(tracks.value.map { it.internal.trackId }.toSet())
                },
                onCloseClick = { listVm.clearSelection() },
                onQueueClick = {
                    listVm.queueAll(tracks.value.filter { it.internal.trackId in selectedTracks.value })
                    listVm.clearSelection()
                },
                onAddClick = {
                    dialogsVm.setAddDialog(tracks.value.filter { it.internal.trackId in selectedTracks.value }) {
                        listVm.clearSelection()
                    }
                },
                onRemoveClick = onRemoveClick?.let { {
                    it.invoke(tracks.value.filter { it.internal.trackId in selectedTracks.value })
                    listVm.clearSelection()
                } },
                onPlayClick = {
                    listVm.queueAll(
                        tracks.value.filter { it.internal.trackId in selectedTracks.value },
                        mustPlay = true
                    )
                    listVm.clearSelection()
                    navController.navigate(AppScreen.Playing.name)
                },
                selectionSize = selectedTracks.value.size,
                allSelected = tracks.value.size == selectedTracks.value.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium))
            )
        }
        AnimatedVisibility(!selectionMode) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SearchInputField(
                    text = searchStr.value,
                    placeholder = stringResource(R.string.plholder_search_tracks),
                    onChange = { listVm.updateSearchString(it) },
                    modifier = Modifier
                        .weight(.7f)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Text(
                    text = "${tracks.value.size} ${stringResource(R.string.song_counter)}",
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .weight(.3f)
                        .padding(end = dimensionResource(R.dimen.padding_medium))
                )
            }
        }
        Divider()
        LazyColumn(
            modifier = Modifier
                .testTag("TrackList")
                .weight(.85f)
        ) {
            items(tracks.value) {
                TrackItem(
                    track = it,
                    onItemClick = {
                        if (selectionMode)
                            listVm.selectTrack(it.internal.trackId)
                        else {
                            listVm.replaceQueue(
                                tracks = if (mustReplaceQueue) tracks.value else listOf(it),
                                currentId = it.internal.trackId,
                            )
                            navController.navigate(AppScreen.Playing.name)
                        }
                    },
                    onLongPress = { listVm.selectTrack(it.internal.trackId) },
                    horizontalLayout = horizontalLayout,
                    isSelected = it.internal.trackId in selectedTracks.value,
                    selectionMode = selectionMode,
                    onAddClick = { dialogsVm.setAddDialog(listOf(it)) },
                    onInfoClick = { dialogsVm.setInfoDialog(it) },
                    onQueueClick = { listVm.queueAll(listOf(it)) },
                    onRemoveClick = onRemoveClick?.let { f -> {
                        f.invoke(listOf(it))
                        listVm.clearSelection()
                    } }
                )
            }
        }
    }
}

@Composable
fun SelectionToolbar(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
    onQueueClick: () -> Unit,
    whileSelectedClick: () -> Unit,
    onCloseClick: () -> Unit,
    onPlayClick: (() -> Unit)? = null,
    onDequeueClick: (() -> Unit)? = null,
    onRemoveClick: (() -> Unit)? = null,
    selectionSize: Int,
    allSelected: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.testTag("SelectionToolbar")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TransparentButton(
                onClick = whileSelectedClick,
                painter = painterResource(if (allSelected) R.drawable.selected else R.drawable.not_selected),
                contentDescription = "Select/Deselect all",
                tint = MaterialTheme.colorScheme.primary,
                fullSizeIcon = true,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = "$selectionSize ${stringResource(R.string.selection_counter)}",
                fontSize = 14.sp,
                lineHeight = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TransparentBtnWithContextMenu(
                painter = painterResource(R.drawable.more_horiz),
                contentDescription = "Selection options",
                tint = MaterialTheme.colorScheme.primary,
            ) { closeMenu ->
                CustomContextMenuBtn(
                    onClick = {
                        onAddClick()
                        closeMenu()
                    },
                    painter = painterResource(R.drawable.playlist_add),
                    text = stringResource(R.string.playlist_add_label),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                onRemoveClick?.let {
                    CustomContextMenuBtn(
                        onClick = {
                            it()
                            closeMenu()
                        },
                        painter = painterResource(R.drawable.remove),
                        text = stringResource(R.string.playlist_remove_label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                CustomContextMenuBtn(
                    onClick = {
                        onQueueClick()
                        closeMenu()
                    },
                    painter = painterResource(R.drawable.queue_icon),
                    text = stringResource(R.string.queue_add_label),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                onPlayClick?.let {
                    CustomContextMenuBtn(
                        onClick = {
                            it()
                            closeMenu()
                        },
                        painter = painterResource(R.drawable.play),
                        text = stringResource(R.string.play_label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                onDequeueClick?.let {
                    CustomContextMenuBtn(
                        onClick = {
                            it()
                            closeMenu()
                        },
                        painter = painterResource(R.drawable.remove),
                        text = stringResource(R.string.dequeue_label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            TransparentButton(
                onClick = onCloseClick,
                painter = painterResource(R.drawable.close),
                contentDescription = "Close selection",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackItem(
    modifier: Modifier = Modifier,
    track: TrackWithAlbum,
    onItemClick: () -> Unit,
    onLongPress: () -> Unit,
    onAddClick: () -> Unit,
    onInfoClick: () -> Unit,
    onQueueClick: () -> Unit,
    onRemoveClick: (() -> Unit)? = null,
    onDequeueClick: (() -> Unit)? = null,
    dragHandle: ReorderableDragHandle? = null,
    horizontalLayout: Boolean,
    isSelected: Boolean,
    selectionMode: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = onLongPress
            )
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = .15f) else Color.Transparent)
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        dragHandle?.let { handle ->
            IconButton(
                onClick = {},
                modifier = with(handle.scope) {
                    Modifier.draggableHandle(enabled = handle.dragEnabled)
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.drag_handle),
                    contentDescription = "dragHandle",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        val density = LocalDensity.current
        var height by remember { mutableStateOf(0.dp) }
        Box(
            modifier = Modifier
                .weight(if (horizontalLayout) .05f else .15f)
                .onGloballyPositioned { height = with(density) { it.size.height.toDp() } }
                .aspectRatio(1f)
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(track.album.thumbnail)
                    .dispatcher(Dispatchers.IO)
                    .crossfade(true)
                    .build(),
                contentDescription = track.internal.title,
                error = painterResource(R.drawable.unknown_thumb),
                placeholder = painterResource(R.drawable.unknown_thumb),
                contentScale = ContentScale.Crop
            )

            if (selectionMode) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = .5f))
                        .padding(dimensionResource(R.dimen.padding_small)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(if (isSelected) R.drawable.selected else R.drawable.not_selected),
                        contentDescription = "Selected",
                        tint = if (isSystemInDarkTheme())
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        else if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(.75f)
                .height(height)
                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = track.internal.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 16.sp,
            )
            Text(
                text = track.internal.artist,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 12.sp,
                lineHeight = 12.sp,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = track.album.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.weight(.80f)
                )
                Text(
                    text = formatTimestamp(track.internal.durationMs),
                    textAlign = TextAlign.Right,
                    maxLines = 1,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.weight(.20f)
                )
            }
        }
        TransparentBtnWithContextMenu(
            painter = painterResource(R.drawable.more_horiz),
            contentDescription = "Track options",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
        ) { closeMenu ->
            CustomContextMenuBtn(
                onClick = {
                    onInfoClick()
                    closeMenu()
                },
                painter = painterResource(R.drawable.info),
                text = stringResource(R.string.song_info),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CustomContextMenuBtn(
                onClick = {
                    onAddClick()
                    closeMenu()
                },
                painter = painterResource(R.drawable.playlist_add),
                text = stringResource(R.string.playlist_add_label),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            onRemoveClick?.let {
                CustomContextMenuBtn(
                    onClick = {
                        it()
                        closeMenu()
                    },
                    painter = painterResource(R.drawable.remove),
                    text = stringResource(R.string.playlist_remove_label),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            onDequeueClick?.let {
                CustomContextMenuBtn(
                    onClick = {
                        it()
                        closeMenu()
                    },
                    painter = painterResource(R.drawable.remove),
                    text = stringResource(R.string.dequeue_label),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            CustomContextMenuBtn(
                onClick = {
                    onQueueClick()
                    closeMenu()
                },
                painter = painterResource(R.drawable.queue_icon),
                text = stringResource(R.string.queue_add_label),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}