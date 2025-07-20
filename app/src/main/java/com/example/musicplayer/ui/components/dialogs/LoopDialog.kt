package com.example.musicplayer.ui.components.dialogs

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.musicplayer.R
import com.example.musicplayer.data.Loop
import com.example.musicplayer.ui.components.CustomContextMenuRadioBtn
import com.example.musicplayer.ui.state.CurrentPlayingVM

@Composable
fun LoopDialog(
    vm: CurrentPlayingVM,
    currentMode: Loop
) {
    CustomContextMenuRadioBtn(
        onClick = { vm.setLoopMode(Loop.None) },
        painter = painterResource(R.drawable.next_song),
        text = "Play next song",
        isSelected = currentMode == Loop.None,
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
    CustomContextMenuRadioBtn(
        onClick = { vm.setLoopMode(Loop.Queue) },
        painter = painterResource(R.drawable.repeat_queue),
        text = "Repeat the same queue",
        isSelected = currentMode == Loop.Queue,
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
    CustomContextMenuRadioBtn(
        onClick = { vm.setLoopMode(Loop.Track) },
        painter = painterResource(R.drawable.repeat_one),
        text = "Repeat the same song",
        isSelected = currentMode == Loop.Track,
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}