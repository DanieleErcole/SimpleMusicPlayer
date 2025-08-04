package com.example.musicplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
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
            LazyRow(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(screens) {
                    val inPage = isInPage(it)
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
    }
}