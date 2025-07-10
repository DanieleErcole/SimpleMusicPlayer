package com.example.musicplayer.utils

import android.annotation.SuppressLint
import android.content.Context
import com.example.musicplayer.MusicPlayerApplication
import java.time.Duration

fun app(ctx: Context): MusicPlayerApplication {
    return ctx.applicationContext as MusicPlayerApplication
}

@SuppressLint("DefaultLocale")
fun formatTimestamp(ms: Long): String {
    val duration = Duration.ofMillis(ms)
    val hours = duration.toHours()
    val minutes = duration.toMinutes()
    val seconds = duration.seconds
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}