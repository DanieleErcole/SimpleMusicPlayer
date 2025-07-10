package com.example.musicplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicplayer.R

@Composable
fun AppBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Playing.name
    )

    Column {
        HorizontalDivider (
            thickness = 2.dp,
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
        )
        BottomAppBar(
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.navigate(AppScreen.TrackList.name) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.queue_icon),
                        contentDescription = "Current queue"
                    )
                }
                IconButton(
                    onClick = { navController.navigate(AppScreen.Playing.name) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.play_tab_icon),
                        contentDescription = "Current playing track"
                    )
                }
                IconButton(
                    onClick = { navController.navigate(AppScreen.Tracks.name) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.tracks_file),
                        contentDescription = "Tracks"
                    )
                }
                IconButton(
                    onClick = { navController.navigate(AppScreen.Albums.name) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.albums_icon),
                        contentDescription = "Albums"
                    )
                }
                IconButton(
                    onClick = { navController.navigate(AppScreen.Playlists.name) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.playlists_icon),
                        contentDescription = "Playlists"
                    )
                }
                IconButton(
                    onClick = {
                        //TODO: open the menu
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Dropdown menu"
                    )
                }
            }
        }
    }
}