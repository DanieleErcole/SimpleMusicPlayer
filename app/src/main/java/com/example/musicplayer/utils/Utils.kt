package com.example.musicplayer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.icu.util.TimeZone
import com.example.musicplayer.MusicPlayerApplication
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

fun app(ctx: Context): MusicPlayerApplication {
    return ctx.applicationContext as MusicPlayerApplication
}

@SuppressLint("DefaultLocale")
fun formatTimestamp(ms: Long): String = ms.milliseconds.toComponents { minutes, seconds, _ ->
    "$minutes:${seconds.toString().padStart(2, '0')}"
}

fun formatInstantToHuman(i: Instant): String = ZonedDateTime
    .ofInstant(i, ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm"))

fun Cursor.nullableIntColumn(index: Int): Int? = if (index == -1) null else this.getInt(index)
fun Cursor.nullableStringColumn(index: Int): String? = if (index == -1) null else this.getString(index)
fun Cursor.nullableLongColumn(index: Int): Long? = if (index == -1) null else this.getLong(index)

fun floatPosition(pos: Long, duration: Long): Float = ((pos * 100) / duration).toFloat()

fun toPosition(percentage: Float, duration: Long): Long = (percentage / 100 * duration).toLong()