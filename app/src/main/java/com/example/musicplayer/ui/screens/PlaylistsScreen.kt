package com.example.musicplayer.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
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
import com.example.musicplayer.utils.THUMBNAILS_GRID_COLUMNS
import com.example.musicplayer.utils.THUMBNAILS_GRID_COUNT
import com.example.musicplayer.utils.app
import kotlinx.coroutines.Dispatchers
import kotlin.collections.first
import kotlin.collections.ifEmpty

@Composable
fun PlaylistsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    plVm: PlaylistsVM,
    dialogsVm: DialogsVM,
    horizontalLayout: Boolean,
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
                        if (tracks.size > 1) ctx.getString(R.string.multiple_tracks) else ctx.getString(R.string.single_track)
                    )
                ) {
                    plVm.removeFromPlaylist(tracks, it.playlistId)
                }
            },
            horizontalLayout = horizontalLayout,
            objectToolsBtn = { tracks ->
                TransparentBtnWithContextMenu(
                    painter = painterResource(R.drawable.more_horiz),
                    contentDescription = "Playlist options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
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

@SuppressLint("UnusedBoxWithConstraintsScope")
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
            .padding(top = dimensionResource(R.dimen.padding_very_small))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_medium),
                    bottom = dimensionResource(R.dimen.padding_small)
                )
        ) {
            SearchInputField(
                text = searchStr.value,
                placeholder = stringResource(R.string.plholder_search_playlists),
                onChange = { plVm.updateSearchString(it) },
                modifier = Modifier
                    .weight(.9f)
                    .padding(start = dimensionResource(R.dimen.padding_very_small))
            )
            TransparentButton(
                onClick = onAddPlaylist,
                painter = painterResource(R.drawable.add),
                contentDescription = "New playlist",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
            )
        }
        Divider()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(dimensionResource(R.dimen.grid_item_min_size)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
            modifier = Modifier
                .fillMaxSize()
                .weight(.9f)
                .testTag("PlaylistList")
        ) {
            items(playlists.value) {
                PlaylistItem(
                    playlist = it,
                    onClick = { onSelectPlaylist(it.playlist) }
                )
            }
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: PlaylistWithThumbnails,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints {
        Column(
            modifier = modifier
                .clickable(onClick = onClick)
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp))
                .clip(shape = RoundedCornerShape(10.dp))
        ) {
            val thumbnails = playlist.toThumbsList().take(THUMBNAILS_GRID_COUNT)
            val minWidth = this@BoxWithConstraints.minWidth

            if (thumbnails.size > 1) {
                val imgSize = (minWidth / THUMBNAILS_GRID_COLUMNS)
                LazyVerticalGrid(
                    modifier = Modifier.size(minWidth),
                    columns = GridCells.Fixed(THUMBNAILS_GRID_COLUMNS)
                ) {
                    items(thumbnails) {
                        AsyncImage(
                            modifier = Modifier.size(imgSize),
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
                            modifier = Modifier.size(imgSize),
                            painter = painterResource(R.drawable.no_img),
                            contentDescription = "No image",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else AsyncImage(
                modifier = Modifier
                    .aspectRatio(1f)
                    .size(minWidth),
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
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }
}