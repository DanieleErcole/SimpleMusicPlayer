package com.example.musicplayer.ui.components.dialogs

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.musicplayer.R
import com.example.musicplayer.data.Loop
import com.example.musicplayer.ui.components.CustomContextMenuRadioBtn
import com.example.musicplayer.ui.state.CurrentPlayingVM

@Composable
fun LoopDialog(
    vm: CurrentPlayingVM,
    currentMode: Loop,
    endAction: () -> Unit
) {
    CustomContextMenuRadioBtn(
        onClick = {
            vm.setLoopMode(Loop.None)
            endAction()
        },
        painter = painterResource(R.drawable.next_song),
        text = stringResource(R.string.no_loop),
        isSelected = currentMode == Loop.None,
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
    CustomContextMenuRadioBtn(
        onClick = {
            vm.setLoopMode(Loop.Queue)
            endAction()
        },
        painter = painterResource(R.drawable.repeat_queue),
        text = stringResource(R.string.queue_loop),
        isSelected = currentMode == Loop.Queue,
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
    CustomContextMenuRadioBtn(
        onClick = {
            vm.setLoopMode(Loop.Track)
            endAction()
        },
        painter = painterResource(R.drawable.repeat_one),
        text = stringResource(R.string.track_loop),
        isSelected = currentMode == Loop.Track,
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}