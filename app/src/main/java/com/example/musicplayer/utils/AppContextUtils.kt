package com.example.musicplayer.utils

import android.content.Context
import com.example.musicplayer.MusicPlayerApplication

fun app(ctx: Context): MusicPlayerApplication {
    return ctx.applicationContext as MusicPlayerApplication
}