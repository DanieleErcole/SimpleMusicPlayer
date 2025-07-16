package com.example.musicplayer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.CustomContextMenuBtn
import com.example.musicplayer.ui.components.CustomContextMenuCheckboxBtn
import com.example.musicplayer.ui.components.TrackList
import com.example.musicplayer.ui.components.TransparentBtnWithContextMenu
import com.example.musicplayer.ui.state.PlaylistsVM
import com.example.musicplayer.ui.state.TrackListVM
import com.example.musicplayer.ui.state.TracksVM

@Composable
fun TracksScreen(
    modifier: Modifier = Modifier,
    tracksVM: TracksVM = viewModel(factory = TracksVM.Factory),
    plVm: PlaylistsVM = viewModel(factory = PlaylistsVM.Factory),
) {
    val filters = tracksVM.artistFilters.collectAsState()
    val selectedFilters = tracksVM.selectedFilters.collectAsState()

    TrackList(
        listVm = viewModel { TrackListVM(tracksVM.tracks) },
        plVm = plVm,
        listTitle = "All songs",
        filters = {
            TransparentBtnWithContextMenu(
                painter = painterResource(R.drawable.filters),
                contentDescription = "Filters",
                enabled = filters.value.isNotEmpty(),
                tint = MaterialTheme.colorScheme.outline
            ) {
                Column {
                    Text(
                        text = "Artists",
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