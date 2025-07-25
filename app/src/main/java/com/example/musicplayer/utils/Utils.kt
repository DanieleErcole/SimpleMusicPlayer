package com.example.musicplayer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.Bundle
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
import com.example.musicplayer.data.Album
import com.example.musicplayer.data.Track

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

/*fun MediaItem.toTrack(): TrackWithAlbum {
    val metadata = this.mediaMetadata

    val track = Track(
        trackId = metadata.extras!!.getLong("trackId"),
        location = metadata.extras!!.getString("location")!!,
        title = metadata.title.toString(),
        album = metadata.extras!!.getLong("albumId"),
        artist = metadata.artist.toString(),
        composer = metadata.composer.toString(),
        genre = metadata.genre.toString(),
        trackNumber = metadata.trackNumber ?: 1,
        discNumber = metadata.discNumber ?: 1,
        year = metadata.releaseYear ?: 0,
        addedToLibrary = Instant.ofEpochMilli(metadata.extras!!.getLong("addedToLibrary")),
        lastPlayed = metadata.extras?.getLong("lastPlayed")?.let { Instant.ofEpochMilli(it) },
        durationMs = metadata.durationMs ?: 0,
        playedCount = metadata.extras!!.getInt("playedCount")
    )
    val album = Album(
        id = metadata.extras!!.getLong("albumId"),
        name = metadata.albumTitle.toString(),
        thumbnail = metadata.artworkUri?.toString(),
        artist = metadata.albumArtist.toString()
    )

    return TrackWithAlbum(track, album)
}*/

fun Cursor.nullableIntColumn(index: Int): Int? = if (index == -1) null else this.getInt(index)
fun Cursor.nullableStringColumn(index: Int): String? = if (index == -1) null else this.getString(index)
fun Cursor.nullableLongColumn(index: Int): Long? = if (index == -1) null else this.getLong(index)

fun floatPosition(pos: Long, duration: Long): Float = ((pos * 100) / duration).toFloat()

fun toPosition(percentage: Float, duration: Long): Long = (percentage / 100 * duration).toLong()