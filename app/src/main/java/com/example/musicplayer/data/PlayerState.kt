package com.example.musicplayer.data

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
)