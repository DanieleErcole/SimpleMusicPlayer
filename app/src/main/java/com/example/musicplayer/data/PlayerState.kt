package com.example.musicplayer.data

import com.example.musicplayer.utils.DEFAULT_VOLUME

enum class Loop {
    None,
    Queue,
    Track
}

data class PlayerState(
    val volume: Float,
    val paused: Boolean,
    val loopMode: Loop,
    val shuffle: Boolean
) {
    companion object {
        fun default(): PlayerState =
            PlayerState(
                volume = DEFAULT_VOLUME,
                paused = false,
                loopMode = Loop.Track,
                shuffle = false
            )
    }
}