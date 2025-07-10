package com.example.musicplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.state.MusicPlayerVM

@Composable
fun AppBar(
    vm: MusicPlayerVM,
    navController: NavController,
    currentScreen: AppScreen,
    modifier: Modifier = Modifier
) {
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
                        vm.updatePrevScreen(currentScreen)
                        navController.navigate(AppScreen.Queue.name)
                    },
                    painter = painterResource(R.drawable.queue_icon),
                    contentDescription = "Current queue",
                    enabled = currentScreen.name != AppScreen.Queue.name,
                    tint = if (currentScreen.name == AppScreen.Queue.name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = {
                        vm.updatePrevScreen(currentScreen)
                        navController.navigate(AppScreen.Playing.name)
                    },
                    painter = painterResource(R.drawable.play_tab_icon),
                    contentDescription = "Current playing track",
                    enabled = currentScreen.name != AppScreen.Playing.name,
                    tint = if (currentScreen.name == AppScreen.Playing.name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = {
                        vm.updatePrevScreen(currentScreen)
                        navController.navigate(AppScreen.Tracks.name)
                    },
                    painter = painterResource(R.drawable.tracks_file),
                    contentDescription = "Tracks",
                    enabled = currentScreen.name != AppScreen.Tracks.name,
                    tint = if (currentScreen.name == AppScreen.Tracks.name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = {
                        vm.updatePrevScreen(currentScreen)
                        navController.navigate(AppScreen.Albums.name)
                    },
                    painter = painterResource(R.drawable.albums_icon),
                    contentDescription = "Albums",
                    enabled = currentScreen.name != AppScreen.Albums.name,
                    tint = if (currentScreen.name == AppScreen.Albums.name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = {
                        vm.updatePrevScreen(currentScreen)
                        navController.navigate(AppScreen.Playlists.name)
                    },
                    painter = painterResource(R.drawable.playlists_icon),
                    contentDescription = "Playlists",
                    enabled = currentScreen.name != AppScreen.Playlists.name,
                    tint = if (currentScreen.name == AppScreen.Playlists.name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
                TransparentButton(
                    onClick = {
                        //TODO: open the menu
                    },
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Dropdown menu",
                    tint = if (currentScreen.name == AppScreen.Settings.name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}