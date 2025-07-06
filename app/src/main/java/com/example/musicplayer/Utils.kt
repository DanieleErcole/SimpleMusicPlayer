package com.example.musicplayer

import android.content.Context

fun app(ctx: Context): MusicPlayerApplication {
    return ctx.applicationContext as MusicPlayerApplication
}