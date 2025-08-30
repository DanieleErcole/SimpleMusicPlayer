package com.example.musicplayer

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.SessionToken
import com.example.musicplayer.services.player.PlayerService
import com.example.musicplayer.ui.MusicPlayerApp
import com.example.musicplayer.ui.state.MusicPlayerVM
import com.example.musicplayer.ui.theme.MusicPlayerTheme

class MainActivity : ComponentActivity() {

    lateinit var vm: MusicPlayerVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MusicPlayerApplication
        val sessionToken = SessionToken(applicationContext, ComponentName(applicationContext, PlayerService::class.java))
        app.playerController.init(applicationContext, sessionToken)

        setContent {
            enableEdgeToEdge(
                navigationBarStyle = if (isSystemInDarkTheme())
                    SystemBarStyle.dark(Color.TRANSPARENT)
                else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            )

            vm = viewModel(factory = MusicPlayerVM.Factory)
            MusicPlayerTheme {
                MusicPlayerApp(appVm = vm)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(MainActivity::class.simpleName, "Saving current track position")
        if (::vm.isInitialized)
            vm.storeCurrentTrackInfo()
    }

    override fun onDestroy() {
        if (::vm.isInitialized)
            vm.releaseRes()
        val serviceIntent = Intent(this, PlayerService::class.java)
        stopService(serviceIntent)
        Log.d(null, "Destroying the app activity...")
        super.onDestroy()
    }

}