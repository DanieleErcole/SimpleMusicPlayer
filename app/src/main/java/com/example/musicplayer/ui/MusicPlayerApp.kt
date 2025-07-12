package com.example.musicplayer.ui

import android.os.Environment
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.ui.components.slideInConditional
import com.example.musicplayer.ui.components.slideOutConditional
import com.example.musicplayer.ui.screens.CurrentPlayingScreen
import com.example.musicplayer.ui.state.CurrentPlayingVM
import com.example.musicplayer.ui.state.MusicPlayerVM
import com.example.musicplayer.ui.state.PlaylistsVM
import com.example.musicplayer.utils.app

enum class AppScreen(val index: Int) {
    Queue(0),
    Playing(1), // Current playing screen with player controls
    Tracks(2), // All track list with search and favorites filter etc.
    Albums(3), // Albums grid, with tracks inside
    //Artists, // Artist list and search //TODO: maybe this can be included in the Tracks page as a filter
    //Genres, // Genres list and search //TODO: maybe this can be included in the Tracks page as a filter
    Playlists(4), // Playlist grid with tracks
    Settings(5), // Settings page. e.g. scanned directories etc...
    TrackList(6) // Track list, which can be a playlist, an album or an artist
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

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Playing.name
    )

    Scaffold(
        bottomBar = {
            AppBar(
                vm = appVm,
                navController = navController,
                currentScreen = currentScreen
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Playing.name,
            enterTransition = { slideInConditional(from = appVm.prevScreen, to = currentScreen) },
            popEnterTransition = { slideInConditional(from = appVm.prevScreen, to = currentScreen) },
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(
                route = AppScreen.Queue.name,
                exitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Queue) },
                popExitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Queue) }
            ) {
                Text("Ciao")
            }
            composable(
                route = AppScreen.Playing.name,
                exitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Playing) },
                popExitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Playing) }
            ) {
                CurrentPlayingScreen(
                    vm = viewModel(factory = CurrentPlayingVM.Factory),
                    plVm = viewModel(factory = PlaylistsVM.Factory),
                    modifier = Modifier
                )
            }
            composable(
                route = AppScreen.Tracks.name,
                exitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Tracks) },
                popExitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Tracks) }
            ) {

            }
            composable(
                route = AppScreen.Albums.name,
                exitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Albums) },
                popExitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Albums) }
            ) {

            }
            composable(
                route = AppScreen.Playlists.name,
                exitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Playlists) },
                popExitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Playlists) }
            ) {

            }
            composable(
                route = AppScreen.Settings.name,
                exitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Settings) },
                popExitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.Settings) }
            ) {

            }
            composable(
                route = AppScreen.TrackList.name,
                exitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.TrackList) },
                popExitTransition = { slideOutConditional(from = currentScreen, to = AppScreen.TrackList) }
            ) {
                //TODO: use Gson JSON serialization to pass the album/playlist/queue here as a string, or create an object with a title or the track list
            }
        }
    }
}