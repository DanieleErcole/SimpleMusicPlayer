package com.example.musicplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.CustomContextMenuBtn
import com.example.musicplayer.ui.components.TransparentBtnWithContextMenu
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.state.MusicPlayerVM
import kotlinx.coroutines.launch

@Composable
fun AppBar(
    vm: MusicPlayerVM,
    pagerState: PagerState,
    navController: NavController,
    currentScreen: AppScreen,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val isInPage: (Int) -> Boolean = { page ->
        pagerState.currentPage == page && currentScreen.name != AppScreen.Settings.name
    }

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Column (
            verticalArrangement = Arrangement.Top
        ) {
            HorizontalDivider (
                thickness = 2.dp,
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TransparentButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(AppScreen.Queue.index)
                            if (currentScreen.name == AppScreen.Settings.name)
                                navController.navigate(AppScreen.Main.name)
                        }
                    },
                    painter = painterResource(R.drawable.queue_icon),
                    contentDescription = "Current queue",
                    enabled = !isInPage(AppScreen.Queue.index),
                    tint = if (isInPage(AppScreen.Queue.index)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(AppScreen.Playing.index)
                            if (currentScreen.name == AppScreen.Settings.name)
                                navController.navigate(AppScreen.Main.name)
                        }
                    },
                    painter = painterResource(R.drawable.play_tab_icon),
                    contentDescription = "Current playing track",
                    enabled = !isInPage(AppScreen.Playing.index),
                    tint = if (isInPage(AppScreen.Playing.index)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(AppScreen.Tracks.index)
                            if (currentScreen.name == AppScreen.Settings.name)
                                navController.navigate(AppScreen.Main.name)
                        }
                    },
                    painter = painterResource(R.drawable.tracks_file),
                    contentDescription = "Tracks",
                    enabled = !isInPage(AppScreen.Tracks.index),
                    tint = if (isInPage(AppScreen.Tracks.index)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(AppScreen.Albums.index)
                            if (currentScreen.name == AppScreen.Settings.name)
                                navController.navigate(AppScreen.Main.name)
                        }
                    },
                    painter = painterResource(R.drawable.albums_icon),
                    contentDescription = "Albums",
                    enabled = !isInPage(AppScreen.Albums.index),
                    tint = if (isInPage(AppScreen.Albums.index)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(AppScreen.Playlists.index)
                            if (currentScreen.name == AppScreen.Settings.name)
                                navController.navigate(AppScreen.Main.name)
                        }
                    },
                    painter = painterResource(R.drawable.playlists_icon),
                    contentDescription = "Playlists",
                    enabled = !isInPage(AppScreen.Playlists.index),
                    tint = if (isInPage(AppScreen.Playlists.index)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentBtnWithContextMenu(
                    painter = painterResource(R.drawable.more),
                    contentDescription = "More options",
                    tint = if (currentScreen.name == AppScreen.Settings.name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    CustomContextMenuBtn(
                        onClick = {
                            val list = vm.scannedDirectories.value
                            vm.updateScannedDirs(emptyList())
                            vm.updateScannedDirs(list)
                        },
                        painter = painterResource(R.drawable.scan),
                        text = "scan",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CustomContextMenuBtn(
                        onClick = { navController.navigate(AppScreen.Settings.name) },
                        painter = painterResource(R.drawable.settings),
                        text = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CustomContextMenuBtn(
                        onClick = {  },
                        painter = painterResource(R.drawable.power),
                        text = "Close app",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}