package com.example.musicplayer.ui

import android.Manifest
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.ui.components.dialogs.AddToPlaylistDialog
import com.example.musicplayer.ui.components.dialogs.SongInfoDialog
import com.example.musicplayer.ui.components.slideInConditional
import com.example.musicplayer.ui.components.slideOutConditional
import com.example.musicplayer.ui.screens.CurrentPlayingScreen
import com.example.musicplayer.ui.screens.QueueScreen
import com.example.musicplayer.ui.screens.TracksScreen
import com.example.musicplayer.ui.state.CurrentPlayingVM
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.MusicPlayerVM
import com.example.musicplayer.ui.state.PlaylistsVM
import com.example.musicplayer.ui.state.QueueVM
import com.example.musicplayer.ui.state.TracksVM
import com.example.musicplayer.utils.app
import com.example.musicplayer.utils.hasPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class AppScreen(val index: Int) {
    Queue(0),
    Playing(1), // Current playing screen with player controls
    Tracks(2), // All track list with search and favorites filter etc.
    Albums(3), // Albums grid, with tracks inside
    Playlists(4), // Playlist grid with tracks
    Settings(5), // Settings page. e.g. scanned directories etc...
}

@Composable
fun MusicPlayerApp(
    appVm: MusicPlayerVM,
    navController: NavHostController = rememberNavController()
) {
    val ctx = LocalContext.current
    val app = app(ctx)

    val scannedDirs = appVm.scannedDirectories.collectAsStateWithLifecycle()
    val firstLaunch = appVm.firstLaunch.collectAsStateWithLifecycle()

    val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO
    else Manifest.permission.READ_EXTERNAL_STORAGE

    var relaunchPermission by remember { mutableStateOf(false) }
    var hasPermission by remember {
        mutableStateOf(ctx.applicationContext.hasPermission(permission))
    }
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasPermission = it }
    )

    if (!hasPermission)
        LaunchedEffect(Unit, relaunchPermission) { permissionsLauncher.launch(permission) }

    LaunchedEffect(scannedDirs.value, hasPermission) {
        if (hasPermission) {
            Log.i(null, "Scanning dirs")
            withContext(Dispatchers.IO) {
                app.scanner.scanDirectories(ctx, scannedDirs.value)
            }
        }
    }

    if (firstLaunch.value) {
        Log.i(null, "Updating dirs")
        appVm.updateScannedDirs(
            // Default Music directory
            listOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath)
        )
        appVm.firstLaunched()
    }

    LaunchedEffect(Unit) {
        appVm.errors.collect { errorMsg ->
            appVm.snackBarState.showSnackbar(message = errorMsg)
        }
    }

    val dialogsVm = viewModel<DialogsVM>(factory = DialogsVM.Factory)
    val tracksVm = viewModel<TracksVM>(factory = TracksVM.Factory)
    val playingVm = viewModel<CurrentPlayingVM>(factory = CurrentPlayingVM.Factory)
    val playlistVm = viewModel<PlaylistsVM>(factory = PlaylistsVM.Factory)
    val queueVm = viewModel<QueueVM>(factory = QueueVM.Factory)

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = appVm.snackBarState,
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        },
        bottomBar = {
            AppBar(vm = appVm, navController = navController)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        AddToPlaylistDialog(
            plVm = playlistVm,
            dialogsVm = dialogsVm
        )
        SongInfoDialog(dialogsVm = dialogsVm)

        NavHost(
            navController = navController,
            startDestination = AppScreen.Playing.name,
            enterTransition = { slideInConditional(animSpec = tween(500)) },
            exitTransition = { slideOutConditional(animSpec = tween(500)) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = AppScreen.Queue.name) {
                QueueScreen(vm = queueVm)
            }
            composable(route = AppScreen.Playing.name) {
                CurrentPlayingScreen(
                    vm = playingVm,
                    dialogsVm = dialogsVm
                )
            }
            composable(route = AppScreen.Tracks.name) {
                TracksScreen(
                    navController = navController,
                    tracksVM = tracksVm,
                    dialogsVm = dialogsVm
                )
            }
            composable(route = AppScreen.Albums.name) {

            }
            composable(route = AppScreen.Playlists.name) {

            }
            composable(route = AppScreen.Settings.name) {

            }
        }
    }
}