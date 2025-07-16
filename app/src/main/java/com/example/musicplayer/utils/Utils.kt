package com.example.musicplayer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import com.example.musicplayer.MusicPlayerApplication
import kotlin.time.Duration.Companion.milliseconds

fun app(ctx: Context): MusicPlayerApplication {
    return ctx.applicationContext as MusicPlayerApplication
}

@SuppressLint("DefaultLocale")
fun formatTimestamp(ms: Long): String = ms.milliseconds.toComponents { minutes, seconds, _ ->
    "$minutes:${seconds.toString().padStart(2, '0')}"
}

fun Cursor.nullableIntColumn(index: Int): Int? = if (index == -1) null else this.getInt(index)
fun Cursor.nullableStringColumn(index: Int): String? = if (index == -1) null else this.getString(index)
fun Cursor.nullableLongColumn(index: Int): Long? = if (index == -1) null else this.getLong(index)
