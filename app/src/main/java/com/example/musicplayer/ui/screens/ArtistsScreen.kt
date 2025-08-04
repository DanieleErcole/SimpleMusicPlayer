package com.example.musicplayer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.musicplayer.R
import com.example.musicplayer.data.ListContext
import com.example.musicplayer.data.ListMode
import com.example.musicplayer.data.TrackFilter
import com.example.musicplayer.ui.AppScreen
import com.example.musicplayer.ui.components.CustomContextMenuBtn
import com.example.musicplayer.ui.components.Divider
import com.example.musicplayer.ui.components.SearchInputField
import com.example.musicplayer.ui.components.TrackList
import com.example.musicplayer.ui.components.TransparentBtnWithContextMenu
import com.example.musicplayer.ui.state.ArtistsVM
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.TrackListVM
import com.example.musicplayer.utils.app

@Composable
fun ArtistsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    vm: ArtistsVM,
    dialogsVm: DialogsVM
) {
    var selectedArtist by remember { mutableStateOf<String?>(null) }

    val app = app(LocalContext.current)
    val filter by remember {
        derivedStateOf {
            TrackFilter(
                musicRepo = app.container.musicRepository,
                ctx = ListContext(mode = ListMode.Artist, artist = selectedArtist)
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

    selectedArtist?.let {
        TrackList(
            listVm = listVm,
            dialogsVm = dialogsVm,
            navController = navController,
            listTitle = it,
            onBackClick = { selectedArtist = null },
            objectToolsBtn = { tracks ->
                TransparentBtnWithContextMenu(
                    painter = painterResource(R.drawable.more_horiz),
                    contentDescription = "Artist options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    fullSizeIcon = true,
                    modifier = Modifier.height(24.dp).width(24.dp)
                ) {
                    CustomContextMenuBtn(
                        onClick = { dialogsVm.setAddDialog(tracks = tracks) },
                        painter = painterResource(R.drawable.playlist_add),
                        text = stringResource(R.string.playlist_add_label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CustomContextMenuBtn(
                        onClick = { listVm.queueAll(tracks) },
                        painter = painterResource(R.drawable.queue_icon),
                        text = stringResource(R.string.queue_add_label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CustomContextMenuBtn(
                        onClick = {
                            listVm.queueAll(tracks, mustPlay = true)
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
    } ?: ArtistsList(
        vm = vm,
        onSelectArtist = { selectedArtist = it },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun ArtistsList(
    modifier: Modifier = Modifier,
    vm: ArtistsVM,
    onSelectArtist: (String) -> Unit
) {
    val searchStr = vm.searchString.collectAsStateWithLifecycle()
    val artists = vm.artists.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .padding(top = 8.dp)
    ) {
        SearchInputField(
            text = searchStr.value,
            placeholder = stringResource(R.string.plholder_search_artists),
            onChange = { vm.updateSearchString(it) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(.06f)
                .padding(horizontal = 8.dp)
        )
        Divider()
        LazyColumn(
            modifier = Modifier
                .weight(.9f)
        ) {
            items(artists.value) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_small))
                        .clickable(onClick = { onSelectArtist(it) })
                ) {
                    Text(
                        text = it,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}