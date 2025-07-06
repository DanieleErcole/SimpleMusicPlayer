package com.example.musicplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Playing.name
    )

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = { navController.navigate(AppScreen.Playing.name) }
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Current playing track"
            )
        }
        Button(
            onClick = { navController.navigate(AppScreen.Tracks.name) }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "Tracks"
            )
        }
        Button(
            onClick = { navController.navigate(AppScreen.Albums.name) }
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle, //TODO: Test icon, change it
                contentDescription = "Albums"
            )
        }
        Button(
            onClick = { navController.navigate(AppScreen.Playlists.name) }
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Playlists"
            )
        }
        Button(
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