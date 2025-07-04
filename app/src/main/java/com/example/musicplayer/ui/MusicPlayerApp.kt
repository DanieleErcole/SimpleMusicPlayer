package com.example.musicplayer.ui

import android.os.Environment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.MusicPlayerApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class AppScreen() {
    Playing, // Current playing screen with player controls
    Tracks, // All track list with search and favorites filter etc.
    Albums, // Albums grid, with tracks inside
    Artists, // Artist list and search //TODO: maybe this can be included in the Tracks page as a filter
    Genres, // Genres list and search //TODO: maybe this can be included in the Tracks page as a filter
    Playlists, // Playlist grid with tracks
    Settings, // Settings page. e.g. scanned directories etc...
    TrackList // Track list, which can be a playlist, an album, an artist or the queue
}

@Composable
fun MusicPlayerApp(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val app = context.applicationContext as MusicPlayerApplication

    //TODO: now scan manually and every time the directories are updated
    LaunchedEffect(Unit) {
        // If it's the first time the user launches the app we scan the directories
        app.userPreferencesRepository.firstLaunch.collect {
            if (it) {
                app.userPreferencesRepository.updateScannedDirs(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath // Default Music directory
                )
                withContext(Dispatchers.IO) {
                    app.scanner.scanDirectories()
                }
                app.userPreferencesRepository.firstLaunched()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Playing,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = AppScreen.Playing.name) {

            }
            composable(route = AppScreen.Tracks.name) {

            }
            composable(route = AppScreen.Albums.name) {

            }
            composable(route = AppScreen.Artists.name) {

            }
            composable(route = AppScreen.Genres.name) {

            }
            composable(route = AppScreen.Playlists.name) {

            }
            composable(route = AppScreen.Settings.name) {

            }
            composable(route = AppScreen.TrackList.name) {

            }
        }
    }
}

@Composable
fun AppBar(

) {

}