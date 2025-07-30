package com.example.musicplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.CustomContextMenuBtn
import com.example.musicplayer.ui.components.TransparentBtnWithContextMenu
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.state.MusicPlayerVM

@Composable
fun AppBar(
    vm: MusicPlayerVM,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Playing.name
    )
    val isInPage: (AppScreen) -> Boolean = { screen ->
        currentScreen.name == screen.name
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
                    onClick = { navController.navigate(AppScreen.Queue.name) },
                    painter = painterResource(R.drawable.queue_icon),
                    contentDescription = "Current queue",
                    enabled = !isInPage(AppScreen.Queue),
                    tint = if (isInPage(AppScreen.Queue)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = { navController.navigate(AppScreen.Playing.name) },
                    painter = painterResource(R.drawable.play_tab_icon),
                    contentDescription = "Current playing track",
                    enabled = !isInPage(AppScreen.Playing),
                    tint = if (isInPage(AppScreen.Playing)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = { navController.navigate(AppScreen.Tracks.name) },
                    painter = painterResource(R.drawable.tracks_file),
                    contentDescription = "Tracks",
                    enabled = !isInPage(AppScreen.Tracks),
                    tint = if (isInPage(AppScreen.Tracks)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = { navController.navigate(AppScreen.Albums.name) },
                    painter = painterResource(R.drawable.albums_icon),
                    contentDescription = "Albums",
                    enabled = !isInPage(AppScreen.Albums),
                    tint = if (isInPage(AppScreen.Albums)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = { navController.navigate(AppScreen.Playlists.name) },
                    painter = painterResource(R.drawable.playlists_icon),
                    contentDescription = "Playlists",
                    enabled = !isInPage(AppScreen.Playlists),
                    tint = if (isInPage(AppScreen.Playlists)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentBtnWithContextMenu(
                    painter = painterResource(R.drawable.more),
                    contentDescription = "More options",
                    tint = if (currentScreen.name == AppScreen.Settings.name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    CustomContextMenuBtn(
                        onClick = { vm.rescan(ctx = ctx) },
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