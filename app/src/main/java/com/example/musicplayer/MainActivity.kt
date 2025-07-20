package com.example.musicplayer

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayer.ui.MusicPlayerApp
import com.example.musicplayer.ui.state.MusicPlayerVM
import com.example.musicplayer.ui.theme.MusicPlayerTheme

class MainActivity : ComponentActivity() {

    lateinit var vm: MusicPlayerVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            vm = viewModel(factory = MusicPlayerVM.Factory)
            MusicPlayerTheme {
                MusicPlayerApp(appVm = vm)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(MainActivity::class.simpleName, "Saving current track position")
        vm.storeCurrentTrackInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.releaseRes()
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicPlayerTheme {
    }
}