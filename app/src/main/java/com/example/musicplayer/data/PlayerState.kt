package com.example.musicplayer.data

import com.example.musicplayer.utils.PlayerStateDefaults

enum class Loop {
    None,
    Queue,
    Track
}

data class PlayerState(
    val volume: Float,
    val paused: Boolean,
    val loopMode: Loop
) {
    companion object {
        fun default(): PlayerState =
            PlayerState(
                volume = PlayerStateDefaults.VOLUME,
                paused = PlayerStateDefaults.PAUSED,
                loopMode = PlayerStateDefaults.LOOP
            )
    }
}