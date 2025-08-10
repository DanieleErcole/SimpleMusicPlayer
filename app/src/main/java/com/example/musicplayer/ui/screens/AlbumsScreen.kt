package com.example.musicplayer.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.musicplayer.data.Album
import com.example.musicplayer.data.ListContext
import com.example.musicplayer.data.ListMode
import com.example.musicplayer.data.TrackFilter
import com.example.musicplayer.ui.AppScreen
import com.example.musicplayer.ui.components.CustomContextMenuBtn
import com.example.musicplayer.ui.components.Divider
import com.example.musicplayer.ui.components.SearchInputField
import com.example.musicplayer.ui.components.TrackList
import com.example.musicplayer.ui.components.TransparentBtnWithContextMenu
import com.example.musicplayer.ui.state.AlbumsVM
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.TrackListVM
import com.example.musicplayer.utils.app
import kotlinx.coroutines.Dispatchers

@Composable
fun AlbumsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    albumsVM: AlbumsVM,
    dialogsVm: DialogsVM,
    horizontalLayout: Boolean,
) {
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }

    val app = app(LocalContext.current)
    val filter by remember {
        derivedStateOf {
            TrackFilter(
                musicRepo = app.container.musicRepository,
                ctx = ListContext(mode = ListMode.Album, id = selectedAlbum?.id)
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

    selectedAlbum?.let {
        TrackList(
            listVm = listVm,
            dialogsVm = dialogsVm,
            navController = navController,
            listTitle = it.name,
            onBackClick = { selectedAlbum = null },
            objectToolsBtn = { tracks ->
                TransparentBtnWithContextMenu(
                    painter = painterResource(R.drawable.more_horiz),
                    contentDescription = "Album options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
                ) { closeMenu ->
                    CustomContextMenuBtn(
                        onClick = {
                            dialogsVm.setAddDialog(tracks = tracks) { closeMenu() }
                        },
                        painter = painterResource(R.drawable.playlist_add),
                        text = stringResource(R.string.playlist_add_label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CustomContextMenuBtn(
                        onClick = {
                            listVm.queueAll(tracks)
                            closeMenu()
                        },
                        painter = painterResource(R.drawable.queue_icon),
                        text = stringResource(R.string.queue_add_label),
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
            horizontalLayout = horizontalLayout,
            modifier = modifier.fillMaxSize()
        )
    } ?: AlbumGrid(
        albumsVM = albumsVM,
        onSelectAlbum = { selectedAlbum = it },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun AlbumGrid(
    albumsVM: AlbumsVM,
    onSelectAlbum: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchStr = albumsVM.searchString.collectAsStateWithLifecycle()
    val albums = albumsVM.albums.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .padding(top = dimensionResource(R.dimen.padding_very_small))
    ) {
        SearchInputField(
            text = searchStr.value,
            placeholder = stringResource(R.string.plholder_search_albums),
            onChange = { albumsVM.updateSearchString(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_small),
                    bottom = dimensionResource(R.dimen.padding_very_small)
                )
        )
        Divider()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(dimensionResource(R.dimen.grid_item_min_size)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
            modifier = Modifier
                .fillMaxSize()
                .weight(.9f)
        ) {
            items(albums.value) {
                AlbumItem(
                    album = it,
                    onClick = { onSelectAlbum(it) }
                )
            }
        }
    }
}

@Composable
fun AlbumItem(
    album: Album,
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
            AsyncImage(
                modifier = Modifier
                    .aspectRatio(1f)
                    .size(size = this@BoxWithConstraints.minWidth),
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(album.thumbnail)
                    .dispatcher(Dispatchers.IO)
                    .crossfade(true)
                    .build(),
                contentDescription = album.name,
                error = painterResource(R.drawable.unknown_thumb),
                placeholder = painterResource(R.drawable.unknown_thumb),
                contentScale = ContentScale.Crop
            )
            Text(
                text = album.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }
}