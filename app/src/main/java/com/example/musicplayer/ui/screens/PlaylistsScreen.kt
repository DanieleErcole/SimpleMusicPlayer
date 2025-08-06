package com.example.musicplayer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicplayer.R
import com.example.musicplayer.data.ListContext
import com.example.musicplayer.data.ListMode
import com.example.musicplayer.data.Playlist
import com.example.musicplayer.data.PlaylistWithThumbnails
import com.example.musicplayer.data.TrackFilter
import com.example.musicplayer.ui.AppScreen
import com.example.musicplayer.ui.components.CustomContextMenuBtn
import com.example.musicplayer.ui.components.Divider
import com.example.musicplayer.ui.components.SearchInputField
import com.example.musicplayer.ui.components.TrackList
import com.example.musicplayer.ui.components.TransparentBtnWithContextMenu
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.PlaylistsVM
import com.example.musicplayer.ui.state.TrackListVM
import com.example.musicplayer.utils.THUMBNAILS_GRID_COUNT
import com.example.musicplayer.utils.app
import com.example.musicplayer.utils.offSetCells
import kotlinx.coroutines.Dispatchers
import kotlin.collections.first
import kotlin.collections.ifEmpty

@Composable
fun PlaylistsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    plVm: PlaylistsVM,
    dialogsVm: DialogsVM
) {
    var selectedPl by remember { mutableStateOf<Playlist?>(null) }

    val ctx = LocalContext.current
    val app = app(ctx)
    val filter by remember {
        derivedStateOf {
            TrackFilter(
                musicRepo = app.container.musicRepository,
                ctx = ListContext(mode = ListMode.Playlist, id = selectedPl?.playlistId)
            )
        }
    }

    val listVm = viewModel {
        TrackListVM(
            trackSrc = filter,
            playerController = app.playerController
        )
    }
    LaunchedEffect(filter) {
        listVm.setTrackSource(filter)
    }

    selectedPl?.let {
        var plName by remember { mutableStateOf(it.name) }

        TrackList(
            listVm = listVm,
            dialogsVm = dialogsVm,
            navController = navController,
            listTitle = plName,
            onBackClick = { selectedPl = null },
            onRemoveClick = { tracks ->
                dialogsVm.setConfirmDialog(
                    title = ctx.getString(R.string.remove_dialog_title),
                    text = ctx.getString(
                        R.string.remove_dialog_text,
                        if (tracks.size > 1) ctx.getString(R.string.single_track) else ctx.getString(R.string.multiple_tracks)
                    )
                ) {
                    plVm.removeFromPlaylist(tracks, it.playlistId)
                }
            },
            objectToolsBtn = { tracks ->
                TransparentBtnWithContextMenu(
                    painter = painterResource(R.drawable.more_horiz),
                    contentDescription = "Playlist options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    fullSizeIcon = true,
                    modifier = Modifier.height(24.dp).width(24.dp)
                ) { closeMenu ->
                    CustomContextMenuBtn(
                        onClick = {
                            dialogsVm.setRenameDialog(it.playlistId) {
                                plName = it
                                closeMenu()
                            }
                        },
                        painter = painterResource(R.drawable.edit),
                        text = stringResource(R.string.rename_pl_label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CustomContextMenuBtn(
                        onClick = {
                            plVm.deletePlaylist(it)
                            closeMenu()
                            selectedPl = null
                        },
                        painter = painterResource(R.drawable.remove),
                        text = stringResource(R.string.delete_pl_label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CustomContextMenuBtn(
                        onClick = {
                            listVm.queueAll(tracks, mustPlay = true)
                            closeMenu()
                            navController.navigate(AppScreen.Playing.name)
                        },
                        painter = painterResource(R.drawable.play),
                        text = stringResource(R.string.play_label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            mustReplaceQueue = true,
            modifier = modifier.fillMaxSize()
        )
    } ?: PlaylistGrid(
        plVm = plVm,
        onAddPlaylist = { dialogsVm.setNewDialog() },
        onSelectPlaylist = { selectedPl = it },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun PlaylistGrid(
    plVm: PlaylistsVM,
    onAddPlaylist: () -> Unit,
    onSelectPlaylist: (Playlist) -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchStr = plVm.searchString.collectAsStateWithLifecycle()
    val playlists = plVm.filteredPlaylist.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .padding(top = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .weight(.06f)
                .padding(horizontal = 8.dp)
        ) {
            SearchInputField(
                text = searchStr.value,
                placeholder = stringResource(R.string.plholder_search_playlists),
                onChange = { plVm.updateSearchString(it) },
                modifier = Modifier.weight(.9f)
            )
            TransparentButton(
                onClick = onAddPlaylist,
                painter = painterResource(R.drawable.add),
                contentDescription = "New playlist",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Divider()
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(.9f)
                .padding(start = 8.dp, end = 8.dp)
        ) {
            offSetCells(3)
            items(playlists.value) {
                PlaylistItem(
                    playlist = it,
                    onClick = { onSelectPlaylist(it.playlist) }
                )
            }
            offSetCells(3)
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: PlaylistWithThumbnails,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp))
            .clip(shape = RoundedCornerShape(10.dp))
    ) {
        val thumbnails = playlist.toThumbsList()
        if (thumbnails.size > 1) {
            val imgSize = (128 / 2)
            LazyVerticalGrid(
                modifier = Modifier.size(128.dp),
                columns = GridCells.Fixed(2)
            ) {
                items(thumbnails) {
                    AsyncImage(
                        modifier = Modifier.size(imgSize.dp),
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(it)
                            .dispatcher(Dispatchers.IO)
                            .crossfade(true)
                            .build(),
                        contentDescription = playlist.playlist.name,
                        error = painterResource(R.drawable.unknown_thumb),
                        placeholder = painterResource(R.drawable.unknown_thumb),
                        contentScale = ContentScale.Crop
                    )
                }
                items(count = THUMBNAILS_GRID_COUNT - thumbnails.size) {
                    Image(
                        modifier = Modifier.size(imgSize.dp),
                        painter = painterResource(R.drawable.no_img),
                        contentDescription = "No image",
                        contentScale = ContentScale.Crop
                    )
                }
            }
        } else AsyncImage(
            modifier = Modifier.size(128.dp),
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(thumbnails.ifEmpty { null }?.first())
                .dispatcher(Dispatchers.IO)
                .crossfade(true)
                .build(),
            contentDescription = playlist.playlist.name,
            error = painterResource(R.drawable.unknown_thumb),
            placeholder = painterResource(R.drawable.unknown_thumb),
            contentScale = ContentScale.Crop
        )
        Text(
            text = playlist.playlist.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}