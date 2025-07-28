package com.example.musicplayer.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    dialogsVm: DialogsVM
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
                    fullSizeIcon = true,
                    modifier = Modifier.height(24.dp).width(24.dp)
                ) {
                    CustomContextMenuBtn(
                        onClick = { dialogsVm.setAddDialog(tracks = tracks) },
                        painter = painterResource(R.drawable.playlist_add),
                        text = "Add to a playlist",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CustomContextMenuBtn(
                        onClick = { listVm.queueAll(tracks) },
                        painter = painterResource(R.drawable.queue_icon),
                        text = "Add to queue",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CustomContextMenuBtn(
                        onClick = { listVm.queueAll(tracks, mustPlay = true) },
                        painter = painterResource(R.drawable.play),
                        text = "Play",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            mustReplaceQueue = true,
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
            .padding(top = 8.dp)
    ) {
        SearchInputField(
            text = searchStr.value,
            placeholder = "Search an album",
            onChange = { albumsVM.updateSearchString(it) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(.06f)
                .padding(horizontal = 8.dp)
        )
        Divider()
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(.9f)
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
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
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant/*surfaceContainerHigh*/, shape = RoundedCornerShape(10.dp))
            .clip(shape = RoundedCornerShape(10.dp))
    ) {
        AsyncImage(
            modifier = Modifier.size(128.dp),
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
            modifier = Modifier.padding(8.dp)
        )
    }
}