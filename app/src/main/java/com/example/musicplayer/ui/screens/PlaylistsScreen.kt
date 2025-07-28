package com.example.musicplayer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.musicplayer.R
import com.example.musicplayer.data.ListContext
import com.example.musicplayer.data.ListMode
import com.example.musicplayer.data.Playlist
import com.example.musicplayer.data.TrackFilter
import com.example.musicplayer.ui.components.CustomContextMenuBtn
import com.example.musicplayer.ui.components.Divider
import com.example.musicplayer.ui.components.SearchInputField
import com.example.musicplayer.ui.components.TrackList
import com.example.musicplayer.ui.components.TransparentBtnWithContextMenu
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.PlaylistsVM
import com.example.musicplayer.ui.state.TrackListVM
import com.example.musicplayer.utils.app

@Composable
fun PlaylistsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    plVm: PlaylistsVM,
    dialogsVm: DialogsVM
) {
    var selectedPl by remember { mutableStateOf<Playlist?>(null) }

    val app = app(LocalContext.current)
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
        TrackList(
            listVm = listVm,
            dialogsVm = dialogsVm,
            navController = navController,
            listTitle = it.name,
            onBackClick = { selectedPl = null },
            objectToolsBtn = { tracks ->
                TransparentBtnWithContextMenu(
                    painter = painterResource(R.drawable.edit),
                    contentDescription = "Playlist options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    fullSizeIcon = true,
                    modifier = Modifier.height(24.dp).width(24.dp)
                ) {
                    CustomContextMenuBtn(
                        onClick = { dialogsVm.setRenameDialog(it.playlistId) },
                        painter = painterResource(R.drawable.edit),
                        text = "Rename playlist",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CustomContextMenuBtn(
                        onClick = { plVm.deletePlaylist(it) },
                        painter = painterResource(R.drawable.remove),
                        text = "Delete playlist",
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
    } ?: PlaylistList(
        plVm = plVm,
        onSelectPlaylist = { selectedPl = it },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun PlaylistList(
    plVm: PlaylistsVM,
    onSelectPlaylist: (Playlist) -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchStr = plVm.searchString.collectAsStateWithLifecycle()
    val playlists = plVm.filteredPlaylist.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .padding(top = 8.dp)
    ) {
        SearchInputField(
            text = searchStr.value,
            placeholder = "Search a playlist",
            onChange = { plVm.updateSearchString(it) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(.06f)
                .padding(horizontal = 8.dp)
        )
        Divider()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .weight(.9f)
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
        ) {
            items(playlists.value) {
                PlaylistItem(
                    playlist = it,
                    onClick = { onSelectPlaylist(it) }
                )
            }
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

}