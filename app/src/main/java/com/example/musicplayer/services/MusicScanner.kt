package com.example.musicplayer.services

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.musicplayer.data.Album
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.Track
import com.example.musicplayer.utils.DefaultAlbum
import com.example.musicplayer.utils.albumUriBase
import com.example.musicplayer.utils.nullableIntColumn
import com.example.musicplayer.utils.nullableLongColumn
import com.example.musicplayer.utils.nullableStringColumn
import java.time.Instant

class MusicScanner(private val musicRepo: MusicRepository) {

    suspend fun scanDirectories(ctx: Context) {
        val albums = musicRepo.getAllAlbums().toMutableList()
        val tracks = musicRepo.getAllTracks().map { it.internal }.toMutableList()

        val scannedTracks = scanMusic(ctx)

        // Delete from the db all the tracks that are present but are not stored in the media storage anymore
        // If a track has been moved to another scanned location its id will change (MediaStore behaviour), I treat it like it's another track entirely
        // by deleting its previous version and adding the new one at the new location
        musicRepo.deleteTrackBlk(tracks.filter {
            scannedTracks.find { (track) -> track.trackId == it.trackId } == null
        })

        scannedTracks.forEach { (track, album) ->
            // Check whether the track already exists
            if (tracks.find { it.trackId == track.trackId } != null)
                return@forEach

            // Check if the album it's not already stored in the db
            if (album.id == DefaultAlbum.UNKNOWN_ID)
                addUnknownAlbum(albums)
            else albums.find { a -> album.id == a.id } ?: run {
                Log.i(MusicScanner::class.simpleName, "Adding album ${album.name} with id ${album.id}")
                musicRepo.newAlbum(album)
                albums.add(album)
            }

            Log.i(MusicScanner::class.simpleName, "Adding track ${track.location} with id ${track.trackId}")
            musicRepo.newTrack(track)
            tracks.add(track)
        }

        albums.forEach {
            if (musicRepo.getAlbumTracksCount(it.id) == 0)
                musicRepo.deleteAlbum(it)
        }

        Log.i(MusicScanner::class.simpleName, "Finished scanning tracks")
    }

    private suspend fun addUnknownAlbum(albums: List<Album>) {
        albums.find { it.name == DefaultAlbum.UNKNOWN_ALBUM_NAME } ?: run {
            Log.i(MusicScanner::class.simpleName, "Adding unknown album")
            val newAlbum = Album(
                name = DefaultAlbum.UNKNOWN_ALBUM_NAME,
                thumbnail = null,
                artist = DefaultAlbum.UNKNOWN_ARTIST
            )
            musicRepo.newAlbum(newAlbum)
        }
    }

    private fun scanMusic(ctx: Context): List<Pair<Track, Album>> {
        val fileList = mutableListOf<Pair<Track, Album>>()
        val projection = mutableListOf(
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DISC_NUMBER,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            projection.add(MediaStore.Audio.Media.GENRE)

        val cursor = ctx.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection.toTypedArray(),
            "${MediaStore.Audio.Media.IS_MUSIC} > 0",
            null,
            null
        )

        Log.i(MusicScanner::class.simpleName, "${cursor?.count}")
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val trackNumberColumn = it.getColumnIndex(MediaStore.Audio.Media.TRACK)
            val discNumberColumn = it.getColumnIndex(MediaStore.Audio.Media.DISC_NUMBER)
            val composerColumn = it.getColumnIndex(MediaStore.Audio.Media.COMPOSER)
            val genreColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) it.getColumnIndex(MediaStore.Audio.Media.GENRE) else -1
            val yearColumn = it.getColumnIndex(MediaStore.Audio.Media.YEAR)
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val durationColumn = it.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val albumArtistColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST)
            val albumIdColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)

                val data = it.getString(dataColumn)
                val name = it.getString(nameColumn)
                val trackNumber = it.nullableIntColumn(trackNumberColumn)
                val discNumber = it.nullableIntColumn(discNumberColumn)
                val composer = it.nullableStringColumn(composerColumn)
                val genre = it.nullableStringColumn(genreColumn)
                val year = it.nullableIntColumn(yearColumn)
                val title = it.nullableStringColumn(titleColumn)
                val duration = it.nullableLongColumn(durationColumn)
                val artist = it.nullableStringColumn(artistColumn)
                val albumTitle = it.nullableStringColumn(albumColumn)
                val albumArtist = it.nullableStringColumn(albumArtistColumn)

                val albumId = it.nullableLongColumn(albumIdColumn)
                val albumUri = albumId?.let { ContentUris.withAppendedId(albumUriBase, it).toString() }

                Log.i(MusicScanner::class.simpleName, "Found audio file: $name at $data with id: $id")

                val album = albumId?.let {
                    Album(
                        id = it,
                        name = albumTitle?.ifEmpty { DefaultAlbum.UNKNOWN_ALBUM_NAME } ?: DefaultAlbum.UNKNOWN_ALBUM_NAME,
                        thumbnail = albumUri,
                        artist = albumArtist ?: DefaultAlbum.UNKNOWN_ARTIST
                    )
                } ?: Album(
                    id = DefaultAlbum.UNKNOWN_ID,
                    name = DefaultAlbum.UNKNOWN_ALBUM_NAME,
                    thumbnail = null,
                    artist = DefaultAlbum.UNKNOWN_ARTIST
                )
                fileList.add(Pair(
                    second = album,
                    first = Track(
                        trackId = id,
                        location = data,
                        title = title ?: DefaultAlbum.UNKNOWN,
                        album = album.id,
                        artist = artist ?: DefaultAlbum.UNKNOWN,
                        composer = composer ?: DefaultAlbum.UNKNOWN,
                        genre = genre ?: DefaultAlbum.UNKNOWN,
                        trackNumber = trackNumber ?: 1,
                        discNumber = discNumber ?: 1,
                        year = year ?: 0,
                        addedToLibrary = Instant.now(),
                        lastPlayed = null,
                        durationMs = duration ?: 0,
                        playedCount = 0
                    )
                ))
            }
        }
        return fileList
    }

}