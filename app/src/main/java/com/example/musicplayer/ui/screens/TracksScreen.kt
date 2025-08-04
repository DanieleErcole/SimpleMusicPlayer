package com.example.musicplayer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.musicplayer.R
import com.example.musicplayer.data.ListContext
import com.example.musicplayer.data.ListMode
import com.example.musicplayer.data.TrackFilter
import com.example.musicplayer.ui.components.CustomContextMenuCheckboxBtn
import com.example.musicplayer.ui.components.TrackList
import com.example.musicplayer.ui.components.TransparentBtnWithContextMenu
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.TrackListVM
import com.example.musicplayer.ui.state.TracksVM
import com.example.musicplayer.utils.app

@Composable
fun TracksScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    tracksVM: TracksVM,
    dialogsVm: DialogsVM
) {
    val filters = tracksVM.artistFilters.collectAsState()
    val selectedFilters = tracksVM.selectedFilters.collectAsState()

    val app = app(LocalContext.current)
    val filter = TrackFilter(
        musicRepo = app.container.musicRepository,
        filters = selectedFilters.value.ifEmpty { null },
        ctx = ListContext(mode = ListMode.Tracks)
    )

    TrackList(
        listVm = viewModel {
            TrackListVM(
                trackSrc = filter,
                playerController = app.playerController
            )
        },
        dialogsVm = dialogsVm,
        navController = navController,
        listTitle = stringResource(R.string.tracks_page),
        filters = {
            TransparentBtnWithContextMenu(
                painter = painterResource(R.drawable.filters),
                contentDescription = "Filters",
                enabled = filters.value.isNotEmpty(),
                tint = MaterialTheme.colorScheme.outline
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.artists_filters),
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    filters.value.forEach {
                        CustomContextMenuCheckboxBtn(
                            onClick = { tracksVM.selectFilter(it) },
                            isChecked = selectedFilters.value.contains(it),
                            text = it,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    )
}