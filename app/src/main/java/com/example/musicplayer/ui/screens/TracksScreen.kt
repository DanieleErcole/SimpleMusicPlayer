package com.example.musicplayer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
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
    dialogsVm: DialogsVM,
    horizontalLayout: Boolean,
) {
    val filters = tracksVM.genreFilters.collectAsState()
    val selectedFilters = tracksVM.selectedFilters.collectAsState()

    val app = app(LocalContext.current)
    val filter by remember {
        derivedStateOf {
            TrackFilter(
                musicRepo = app.container.musicRepository,
                filters = selectedFilters.value,
                ctx = ListContext(mode = ListMode.Tracks)
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

    TrackList(
        listVm = listVm,
        dialogsVm = dialogsVm,
        navController = navController,
        listTitle = stringResource(R.string.tracks_page),
        filters = {
            TransparentBtnWithContextMenu(
                painter = painterResource(R.drawable.filters),
                contentDescription = "Filters",
                enabled = filters.value.isNotEmpty(),
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
            ) { closeMenu ->
                Column(
                    modifier = Modifier
                        .heightIn(max = 350.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.genres_filters),
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_medium))
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
        horizontalLayout = horizontalLayout,
        modifier = modifier.fillMaxSize()
    )
}