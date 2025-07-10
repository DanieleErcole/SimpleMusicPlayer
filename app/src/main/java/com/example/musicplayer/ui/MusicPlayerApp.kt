package com.example.musicplayer.ui

import android.os.Environment
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.ui.screens.CurrentPlayingScreen
import com.example.musicplayer.ui.state.CurrentPlayingVM
import com.example.musicplayer.ui.state.MusicPlayerVM
import com.example.musicplayer.utils.app
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class AppScreen() {
    Playing, // Current playing screen with player controls
    Tracks, // All track list with search and favorites filter etc.
    Albums, // Albums grid, with tracks inside
    //Artists, // Artist list and search //TODO: maybe this can be included in the Tracks page as a filter
    //Genres, // Genres list and search //TODO: maybe this can be included in the Tracks page as a filter
    Playlists, // Playlist grid with tracks
    Settings, // Settings page. e.g. scanned directories etc...
    TrackList // Track list, which can be a playlist, an album, an artist or the queue
}

@Composable
fun MusicPlayerApp(
    appVm: MusicPlayerVM = viewModel(factory = MusicPlayerVM.Factory),
    navController: NavHostController = rememberNavController()
) {
    val app = app(LocalContext.current)
    val scannedDirs = appVm.scannedDirectories.collectAsState()
    val firstLaunch = appVm.firstLaunch.collectAsState()

    LaunchedEffect(scannedDirs.value) {
        Log.i(null, "Scanning dirs")
        app.scanner.scanDirectories(scannedDirs.value)
    }

    if (firstLaunch.value) {
        Log.i(null, "Updating dirs")
        appVm.updateScannedDirs(
            listOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath) // Default Music directory
        )
        appVm.firstLaunched()
    }

    Scaffold(
        bottomBar = {
            AppBar(navController = navController)
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Playing.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = AppScreen.Playing.name) {
                CurrentPlayingScreen(
                    vm = viewModel(factory = CurrentPlayingVM.Factory),
                    modifier = Modifier
                )
            }
            composable(route = AppScreen.Tracks.name) {

            }
            composable(route = AppScreen.Albums.name) {

            }
            composable(route = AppScreen.Playlists.name) {

            }
            composable(route = AppScreen.Settings.name) {

            }
            composable(route = AppScreen.TrackList.name) {
                //TODO: use Gson JSON serialization to pass the album/playlist/queue here as a string, or create an object with a title or the track list
            }
        }
    }
}