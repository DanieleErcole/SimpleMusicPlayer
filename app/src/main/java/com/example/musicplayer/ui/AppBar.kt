package com.example.musicplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicplayer.ui.components.TransparentButton

private val screens = listOf(
    AppScreen.Queue,
    AppScreen.Playing,
    AppScreen.Tracks,
    AppScreen.Albums,
    AppScreen.Artists,
    AppScreen.Playlists,
    AppScreen.Settings
)

@Composable
fun AppBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Playing.name
    )

    LazyRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.outlineVariant)
            .padding(top = 1.dp)
            .background(MaterialTheme.colorScheme.background)
            .height(100.dp)
    ) {
        items(screens) {
            val inPage = currentScreen.name == it.name
            TransparentButton(
                onClick = { navController.navigate(it.name) },
                painter = painterResource(it.icon),
                contentDescription = "${it.name} page",
                enabled = !inPage,
                tint = if (inPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}