package com.example.musicplayer.ui

import android.Manifest
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.ui.screens.CurrentPlayingScreen
import com.example.musicplayer.ui.screens.TracksScreen
import com.example.musicplayer.ui.state.MusicPlayerVM
import com.example.musicplayer.ui.state.QueueVM
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
    Main(6),
}

@Composable
fun MusicPlayerApp(
    appVm: MusicPlayerVM,
    navController: NavHostController = rememberNavController()
) {
    val ctx = LocalContext.current
    val app = app(ctx)
    LaunchedEffect(Unit) { // Only on first composition
        app.player.init(ctx)
    }

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

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Main.name
    )
    val pagerState = rememberPagerState(initialPage = AppScreen.Playing.index, pageCount = { 5 })

    Scaffold(
        bottomBar = {
            AppBar(
                vm = appVm,
                navController = navController,
                pagerState = pagerState,
                currentScreen = currentScreen
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Main.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(
                route = AppScreen.Main.name,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                }
            ) {
                HorizontalPager(
                    state = pagerState,
                    beyondViewportPageCount = 2
                ) { page ->
                    when (page) {
                        AppScreen.Queue.index -> {
                            val vm = viewModel<QueueVM>(factory = QueueVM.Factory)
                            val items = vm.queue.collectAsStateWithLifecycle()
                            LazyColumn {
                                items(items.value) {
                                    Text(it.track.internal.title)
                                }
                            }
                        }
                        AppScreen.Playing.index -> CurrentPlayingScreen()
                        AppScreen.Tracks.index -> TracksScreen(pagerState = pagerState)
                        AppScreen.Albums.index -> {}
                        AppScreen.Playlists.index -> {}
                    }
                }
            }
            composable(
                route = AppScreen.Settings.name,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                }
            ) {

            }
        }
    }
}