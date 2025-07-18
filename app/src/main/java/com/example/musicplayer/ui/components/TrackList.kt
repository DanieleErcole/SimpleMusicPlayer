package com.example.musicplayer.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicplayer.R
import com.example.musicplayer.data.TrackWithAlbum
import com.example.musicplayer.ui.AppScreen
import com.example.musicplayer.ui.components.dialogs.AddToPlaylistDialog
import com.example.musicplayer.ui.components.dialogs.SongInfoDialog
import com.example.musicplayer.ui.state.PlaylistsVM
import com.example.musicplayer.ui.state.TrackListVM
import com.example.musicplayer.utils.formatTimestamp
import kotlinx.coroutines.launch

@Composable
fun TrackList(
    listVm: TrackListVM,
    plVm: PlaylistsVM,
    pagerState: PagerState,
    listTitle: String,
    onBackClick: (() -> Unit)? = null,
    filters: (@Composable () -> Unit)? = null,
    objectToolsBtn: (@Composable () -> Unit)? = null,
    queueAll: Boolean = false,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val tracks = listVm.tracks.collectAsStateWithLifecycle()
    val searchStr = listVm.searchString.collectAsStateWithLifecycle()
    val selectedTracks = listVm.selectedTracks.collectAsStateWithLifecycle()

    var openInfoDialog by remember { mutableStateOf(false) }
    var dialogTrack by remember { mutableStateOf<TrackWithAlbum?>(null) } // Track used for info and add to playlist dialog
    dialogTrack?.let {
        AddToPlaylistDialog(
            track = it,
            plVm = plVm
        )
        if (openInfoDialog)
            SongInfoDialog(
                track = it,
                onDismiss = { openInfoDialog = false },
            )
    }

    val selectionMode by remember { derivedStateOf { selectedTracks.value.isNotEmpty() } }
    Column(
        modifier = modifier.padding(top = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(.04f)
                .padding(start = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                onBackClick?.let {
                    TransparentButton(
                        onClick = it,
                        painter = painterResource(R.drawable.back),
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
                Text(
                    text = listTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                filters?.invoke()
                objectToolsBtn?.invoke()
                //TODO: use this only in the album/playlist context
                /*TransparentBtnWithContextMenu(
                    painter = painterResource(R.drawable.edit),
                    contentDescription = "Playlist options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    fullSizeIcon = true,
                    modifier = Modifier.height(24.dp).width(24.dp)
                ) {
                    CustomContextMenuBtn(
                        onClick = { /*TODO: implement editing playlist*/ },
                        painter = painterResource(R.drawable.edit),
                        text = "Rename playlist",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CustomContextMenuBtn(
                        onClick = { /*TODO: implement playlist deletion*/ },
                        painter = painterResource(R.drawable.remove),
                        text = "Delete playlist",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // Place a play all button (queue all tracks) only if this list is viewed in the context of an album or playlist
                    CustomContextMenuBtn(
                        onClick = { /*TODO: queue all list's tracks*/ },
                        painter = painterResource(R.drawable.play),
                        text = "Play",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }*/
            }
        }
        if (selectionMode)
            SelectionToolbar(
                listVm = listVm,
                tracks = tracks.value,
                selection = selectedTracks.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.06f)
                    .padding(start = 10.dp)
            )
        else SearchInputField(
            text = searchStr.value,
            onChange = { listVm.updateSearchString(it) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(.06f)
                .padding(horizontal = 8.dp)
        )
        Text(
            text = "${tracks.value.size} songs",
            fontSize = 13.sp,
            lineHeight = 13.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .weight(.04f)
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 8.dp)
        )
        Divider()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(top = 16.dp)
                .weight(.8f)
        ) {
            items(tracks.value) {
                TrackItem(
                    track = it,
                    onItemClick = {
                        if (selectionMode)
                            listVm.selectTrack(it.internal.trackId)
                        else {
                            listVm.play(it.internal)
                            if (queueAll) {
                                listVm.clearQueue()
                                listVm.queue(tracks.value
                                    .map { it.internal }
                                    .filter { t -> t.trackId != it.internal.trackId }
                                    .toList()
                                )
                            }
                            scope.launch {
                                pagerState.animateScrollToPage(AppScreen.Playing.index)
                            }
                        }
                    },
                    onLongPress = { listVm.selectTrack(it.internal.trackId) },
                    isSelected = selectedTracks.value.contains(it.internal.trackId),
                    selectionMode = selectionMode,
                    onAddClick = {
                        dialogTrack = it
                        plVm.toggleAddDialog()
                    },
                    onInfoClick = {
                        dialogTrack = it
                        openInfoDialog = true
                    }
                )
            }
        }
    }
}

@Composable
fun SelectionToolbar(
    listVm: TrackListVM,
    tracks: List<TrackWithAlbum>,
    selection: List<Long>,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TransparentButton(
                onClick = {
                    if (selection.size == tracks.size)
                        listVm.clearSelection()
                    else listVm.selectList(tracks.map { it.internal.trackId })
                },
                painter = painterResource(if (selection.size == tracks.size) R.drawable.selected else R.drawable.not_selected),
                contentDescription = "Select/Deselect all",
                tint = MaterialTheme.colorScheme.primary,
                fullSizeIcon = true,
                modifier = Modifier
                    .width(26.dp)
                    .height(26.dp)
            )
            Text(
                text = "${selection.size} selected",
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
            TransparentButton(
                onClick = { /*TODO: implement options*/ },
                painter = painterResource(R.drawable.more_horiz),
                contentDescription = "Selection options",
                tint = MaterialTheme.colorScheme.primary
            )
            TransparentButton(
                onClick = { listVm.clearSelection() },
                painter = painterResource(R.drawable.close),
                contentDescription = "Close selection",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackItem(
    track: TrackWithAlbum,
    onItemClick: () -> Unit,
    onLongPress: () -> Unit,
    onAddClick: () -> Unit,
    onInfoClick: () -> Unit,
    isSelected: Boolean,
    selectionMode: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = onLongPress
            )
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(.15f)
                .aspectRatio(1f)
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(track.album.thumbnail)
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
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(if (isSelected) R.drawable.selected else R.drawable.not_selected),
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(.75f)
                .height(60.dp)
                .padding(start = 16.dp)
        ) {
            Text(
                text = track.internal.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 14.sp,
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
                )
                Text(
                    text = formatTimestamp(track.internal.durationMs),
                    maxLines = 1,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                )
            }
        }
        TransparentBtnWithContextMenu(
            painter = painterResource(R.drawable.more_horiz),
            contentDescription = "Track options",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(.1f)
        ) {
            CustomContextMenuBtn(
                onClick = onInfoClick,
                painter = painterResource(R.drawable.info),
                text = "Song info",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CustomContextMenuBtn(
                onClick = onAddClick,
                painter = painterResource(R.drawable.playlist_add),
                text = "Add to a playlist",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}