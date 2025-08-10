package com.example.musicplayer.ui

import android.Manifest
import android.os.Build
import android.provider.MediaStore
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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.musicplayer.R
import com.example.musicplayer.services.MusicObserver
import com.example.musicplayer.ui.components.dialogs.AddToPlaylistDialog
import com.example.musicplayer.ui.components.dialogs.ConfirmActionDialog
import com.example.musicplayer.ui.components.dialogs.NewPlaylistDialog
import com.example.musicplayer.ui.components.dialogs.PermissionDeniedDialog
import com.example.musicplayer.ui.components.dialogs.RenamePlaylistDialog
import com.example.musicplayer.ui.components.dialogs.SongInfoDialog
import com.example.musicplayer.ui.components.slideInConditional
import com.example.musicplayer.ui.components.slideOutConditional
import com.example.musicplayer.ui.screens.AlbumsScreen
import com.example.musicplayer.ui.screens.ArtistsScreen
import com.example.musicplayer.ui.screens.CurrentPlayingScreen
import com.example.musicplayer.ui.screens.PlaylistsScreen
import com.example.musicplayer.ui.screens.QueueScreen
import com.example.musicplayer.ui.screens.SettingsScreen
import com.example.musicplayer.ui.screens.TracksScreen
import com.example.musicplayer.ui.state.AlbumsVM
import com.example.musicplayer.ui.state.ArtistsVM
import com.example.musicplayer.ui.state.CurrentPlayingVM
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.MusicPlayerVM
import com.example.musicplayer.ui.state.PlaylistsVM
import com.example.musicplayer.ui.state.QueueVM
import com.example.musicplayer.ui.state.SettingsVM
import com.example.musicplayer.ui.state.TracksVM
import com.example.musicplayer.utils.app
import com.example.musicplayer.utils.hasPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class AppScreen(val index: Int, val icon: Int) {
    Queue(0, R.drawable.queue_icon), // Player queue
    Playing(1, R.drawable.play_tab_icon), // Current playing screen with player controls
    Tracks(2, R.drawable.tracks_file), // Track list with search filters etc.
    Albums(3, R.drawable.albums_icon), // Albums grid, with tracks inside
    Artists(4, R.drawable.person), // Artist search
    Playlists(5, R.drawable.playlists_icon), // Playlist grid with tracks
    Settings(6, R.drawable.settings), // Settings page. e.g. scanned directories etc...
}

@Composable
fun MusicPlayerApp(
    appVm: MusicPlayerVM,
    navController: NavHostController = rememberNavController(),
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
) {
    val ctx = LocalContext.current
    val app = app(ctx)

    val observer = remember {
        MusicObserver(
            scanner = app.scanner,
            ctx = ctx
        )
    }

    val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO
    else Manifest.permission.READ_EXTERNAL_STORAGE

    val dialogsVm = viewModel<DialogsVM>(factory = DialogsVM.Factory)

    var hasPermission by remember {
        mutableStateOf(ctx.applicationContext.hasPermission(permission))
    }
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            hasPermission = it
            if (!it)
                dialogsVm.togglePermDialog()
        }
    )

    LaunchedEffect(hasPermission) {
        if (!hasPermission)
            permissionsLauncher.launch(permission)
    }

    LaunchedEffect(hasPermission) {
        if (appVm.canAutoScan() && hasPermission) {
            Log.i(null, "Scanning tracks")
            withContext(Dispatchers.IO) {
                app.scanner.scanDirectories(ctx)
            }
        }
    }

    DisposableEffect(Unit) {
        ctx.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
        onDispose {
            ctx.contentResolver.unregisterContentObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        appVm.errors.collect { errorMsg ->
            appVm.snackBarState.showSnackbar(message = errorMsg)
        }
    }

    val tracksVm = viewModel<TracksVM>(factory = TracksVM.Factory)
    val playingVm = viewModel<CurrentPlayingVM>(factory = CurrentPlayingVM.Factory)
    val playlistVm = viewModel<PlaylistsVM>(factory = PlaylistsVM.Factory)
    val queueVm = viewModel<QueueVM>(factory = QueueVM.Factory)
    val albumsVm = viewModel<AlbumsVM>(factory = AlbumsVM.Factory)
    val artistsVm = viewModel<ArtistsVM>(factory = ArtistsVM.Factory)
    val settingsVm = viewModel<SettingsVM>(factory = SettingsVM.Factory)

    navController.addOnDestinationChangedListener { _, destination, _ ->
        val to = AppScreen.valueOf(destination.route ?: AppScreen.Playing.name)
        if (to == AppScreen.Queue)
            queueVm.updateUIQueue()
    }

    val isInLandscape = windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
            || windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

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
            AppBar(
                navController = navController,
                thinNavBar = isInLandscape
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        PermissionDeniedDialog(dialogsVm = dialogsVm)
        ConfirmActionDialog(dialogsVm = dialogsVm)
        AddToPlaylistDialog(
            plVm = playlistVm,
            dialogsVm = dialogsVm,
            horizontalLayout = isInLandscape
        )
        NewPlaylistDialog(
            plVm = playlistVm,
            dialogsVM = dialogsVm
        )
        RenamePlaylistDialog(
            plVm = playlistVm,
            dialogsVM = dialogsVm
        )
        SongInfoDialog(
            dialogsVm = dialogsVm,
            horizontalLayout = isInLandscape
        )

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
                QueueScreen(
                    vm = queueVm,
                    dialogsVm = dialogsVm,
                    horizontalLayout = isInLandscape
                )
            }
            composable(route = AppScreen.Playing.name) {
                CurrentPlayingScreen(
                    vm = playingVm,
                    dialogsVm = dialogsVm,
                    horizontalLayout = isInLandscape
                )
            }
            composable(route = AppScreen.Tracks.name) {
                TracksScreen(
                    navController = navController,
                    tracksVM = tracksVm,
                    dialogsVm = dialogsVm,
                    horizontalLayout = isInLandscape
                )
            }
            composable(route = AppScreen.Albums.name) {
                AlbumsScreen(
                    navController = navController,
                    albumsVM = albumsVm,
                    dialogsVm = dialogsVm,
                    horizontalLayout = isInLandscape
                )
            }
            composable(route = AppScreen.Artists.name) {
                ArtistsScreen(
                    navController = navController,
                    vm = artistsVm,
                    dialogsVm = dialogsVm,
                    horizontalLayout = isInLandscape
                )
            }
            composable(route = AppScreen.Playlists.name) {
                PlaylistsScreen(
                    navController = navController,
                    plVm = playlistVm,
                    dialogsVm = dialogsVm,
                    horizontalLayout = isInLandscape
                )
            }
            composable(route = AppScreen.Settings.name) {
                SettingsScreen(
                    navController = navController,
                    vm = settingsVm,
                    dialogsVm = dialogsVm
                )
            }
        }
    }
}