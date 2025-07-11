package com.example.musicplayer.ui.components.dialogs

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.CustomContextMenuRadioBtn
import com.example.musicplayer.ui.state.CurrentPlayingVM

@Composable
fun LoopDialog(
    vm: CurrentPlayingVM,
) {
    //TODO: insert the player vm
    CustomContextMenuRadioBtn(
        onClick = {  },
        painter = painterResource(R.drawable.next_song),
        text = "Play next song",
        isSelected = true,
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
    CustomContextMenuRadioBtn(
        onClick = {  },
        painter = painterResource(R.drawable.repeat_queue),
        text = "Repeat the same queue",
        isSelected = false,
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
    CustomContextMenuRadioBtn(
        onClick = {  },
        painter = painterResource(R.drawable.repeat_one),
        text = "Repeat the same song",
        isSelected = false,
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}