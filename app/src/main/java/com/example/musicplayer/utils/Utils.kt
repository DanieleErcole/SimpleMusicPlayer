package com.example.musicplayer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.data.TrackWithAlbum
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds
import androidx.core.net.toUri
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.FlowCollector

fun app(ctx: Context): MusicPlayerApplication {
    return ctx.applicationContext as MusicPlayerApplication
}

suspend fun FlowCollector<Preferences>.catchError(ex: Throwable, name: String) {
    if (ex is IOException) {
        Log.e("Datastore", "Error reading $name", ex)
        emit(emptyPreferences())
    } else {
        throw ex
    }
}

@SuppressLint("DefaultLocale")
fun formatTimestamp(ms: Long): String = ms.milliseconds.toComponents { minutes, seconds, _ ->
    "$minutes:${seconds.toString().padStart(2, '0')}"
}

fun formatInstantToHuman(i: Instant): String = ZonedDateTime
    .ofInstant(i, ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm"))

fun TrackWithAlbum.toMediaItem(): MediaItem {
    val t = this
    val extras = Bundle().apply {
        putLong("trackId", t.internal.trackId)
        putLong("albumId", t.album.id)
        putLong("addedToLibrary", t.internal.addedToLibrary.toEpochMilli())
        putInt("playedCount", t.internal.playedCount)
        putString("location", t.internal.location)
        t.internal.lastPlayed?.let {
            putLong("lastPlayed", it.toEpochMilli())
        }
    }
    return MediaItem.Builder()
        .setUri(this.internal.location)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setExtras(extras)
                .setTitle(this.internal.title)
                .setArtist(this.internal.artist)
                .setArtworkUri(this.album.thumbnail?.toUri())
                .setAlbumTitle(this.album.name)
                .setAlbumArtist(this.album.artist)
                .setComposer(this.internal.composer)
                .setTrackNumber(this.internal.trackNumber)
                .setDiscNumber(this.internal.discNumber)
                .setDurationMs(this.internal.durationMs)
                .setReleaseYear(this.internal.year)
                .setGenre(this.internal.genre)
                .build()
        )
        .build()
}

fun Cursor.nullableIntColumn(index: Int): Int? = if (index == -1) null else this.getInt(index)
fun Cursor.nullableStringColumn(index: Int): String? = if (index == -1) null else this.getString(index)
fun Cursor.nullableLongColumn(index: Int): Long? = if (index == -1) null else this.getLong(index)

fun floatPosition(pos: Long, duration: Long): Float = ((pos * 100) / duration).toFloat()

fun LazyGridScope.offSetCells(count: Int) {
    item(span = { GridItemSpan(count) }) {}
}

fun toPosition(percentage: Float, duration: Long): Long = (percentage / 100 * duration).toLong()