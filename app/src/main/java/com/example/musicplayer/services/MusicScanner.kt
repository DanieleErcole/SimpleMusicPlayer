package com.example.musicplayer.services

import android.content.ContentUris
import android.content.Context
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
import java.time.ZonedDateTime

class MusicScanner(private val musicRepo: MusicRepository) {

    suspend fun scanDirectories(ctx: Context, scannedDirs: List<String>) {
        val albums = musicRepo.getAllAlbums().toMutableList()
        val tracks = musicRepo.getAllTracks().map { it.internal }.toMutableList()
        addUnknownAlbum()

        //TODO: move this and do this only on track play for single tracks together with the existence check
        //TODO: another option is to do this only on scanned directory removal (maybe best option)
        // Delete tracks whose location is not present in the scanned dirs (previously deleted)
        /*val toDelete = mutableListOf<Track>()
        tracks.forEach {
            Log.i(MusicScanner::class.simpleName, "${!fileAncestorIsScannedDir(scannedDirs, it.location)}")
            if (!fileExists(ctx, it.location) || !fileAncestorIsScannedDir(scannedDirs, it.location))
                toDelete.add(it)
        }
        musicRepo.deleteTrackBlk(toDelete)*/

        scannedDirs.forEach { dir ->
            Log.i(MusicScanner::class.simpleName, "Scanning directory $dir")
            scanDir(ctx, dir).forEach { (track, album) ->
                // Check whether the track and its album already exists
                if (tracks.find { it.trackId == track.trackId } != null)
                    return

                albums.find { a -> album.id == a.id } ?: run {
                    Log.i(MusicScanner::class.simpleName, "Adding album ${album.name} with id ${album.id}")
                    musicRepo.newAlbum(album)
                    albums.add(album)
                }

                //TODO: move this somewhere else because I took track from the fs, so it exists, this cleanup must be done somewhere else (MusicScannerConnection?)
                /*if (!File(track.location).exists()) // If it already exists in the db but not in the path anymore delete it
                    musicRepo.deleteTrack(track)*/
                Log.i(MusicScanner::class.simpleName, "Adding track ${track.location} with id ${track.trackId}")
                musicRepo.newTrack(track)
                tracks.add(track)
            }
        }
        Log.i(MusicScanner::class.simpleName, "Finished scanning dirs")
    }

    private suspend fun addUnknownAlbum() {
        musicRepo.getAllAlbums().find { it.name == DefaultAlbum.UNKNOWN_ALBUM_NAME } ?: run {
            Log.i(MusicScanner::class.simpleName, "Adding unknown album")
            val newAlbum = Album(
                name = DefaultAlbum.UNKNOWN_ALBUM_NAME,
                thumbnail = null,
                artist = DefaultAlbum.UNKNOWN_ARTIST
            )
            musicRepo.newAlbum(newAlbum)
        }
    }

    fun fileExists(ctx: Context, filePath: String): Boolean {
        val cursor = ctx.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Files.FileColumns._ID),
            "${MediaStore.Files.FileColumns.DATA} = ?",
            arrayOf(filePath),
            null
        )
        return cursor?.use {
            it.count > 0
        } ?: false
    }

    private fun fileAncestorIsScannedDir(scannedDirs: List<String>, filePath: String): Boolean {
        Log.i(MusicScanner::class.simpleName, filePath)
        scannedDirs.forEach {
            Log.i(MusicScanner::class.simpleName, "$it contains: ${filePath.contains(it)}")
            if (filePath.contains(it))
                return true
        }
        return false
    }

    private fun scanDir(ctx: Context, dirPath: String): List<Pair<Track, Album>> {
        val fileList = mutableListOf<Pair<Track, Album>>()
        val cursor = ctx.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
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
            ),
            "${MediaStore.Audio.Media.DATA} LIKE ?",
            arrayOf("$dirPath%"),
            null
        )

        Log.i(MusicScanner::class.simpleName, "${cursor?.count}")
        cursor?.use {
            val isMusicColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC)
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val trackNumberColumn = it.getColumnIndex(MediaStore.Audio.Media.TRACK)
            val discNumberColumn = it.getColumnIndex(MediaStore.Audio.Media.DISC_NUMBER)
            val composerColumn = it.getColumnIndex(MediaStore.Audio.Media.COMPOSER)
            val yearColumn = it.getColumnIndex(MediaStore.Audio.Media.YEAR)
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val durationColumn = it.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val albumArtistColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST)
            val albumIdColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            while (it.moveToNext()) {
                if (it.getInt(isMusicColumn) == 0) continue

                val id = it.getLong(idColumn)

                val data = it.getString(dataColumn)
                val name = it.getString(nameColumn)
                val trackNumber = it.nullableIntColumn(trackNumberColumn)
                val discNumber = it.nullableIntColumn(discNumberColumn)
                val composer = it.nullableStringColumn(composerColumn)
                val year = it.nullableIntColumn(yearColumn)
                val title = it.nullableStringColumn(titleColumn)
                val duration = it.nullableLongColumn(durationColumn)
                val artist = it.nullableStringColumn(artistColumn)
                val albumTitle = it.nullableStringColumn(albumColumn)
                val albumArtist = it.nullableStringColumn(albumArtistColumn)

                val albumId = it.nullableLongColumn(albumIdColumn)
                val albumUri = albumId?.let { ContentUris.withAppendedId(albumUriBase, albumId).toString() }

                Log.i(MusicScanner::class.simpleName, "Found audio file: $name at $data")

                val aId = albumId ?: DefaultAlbum.UNKNOWN_ID
                fileList.add(Pair(
                    second = Album(
                        id = aId,
                        name = albumTitle?.ifEmpty { DefaultAlbum.UNKNOWN_ALBUM_NAME } ?: DefaultAlbum.UNKNOWN_ALBUM_NAME,
                        thumbnail = albumUri,
                        artist = albumArtist ?: DefaultAlbum.UNKNOWN_ARTIST
                    ),
                    first = Track(
                        trackId = id,
                        location = data,
                        title = title ?: DefaultAlbum.UNKNOWN,
                        album = aId,
                        artist = artist ?: DefaultAlbum.UNKNOWN,
                        composer = composer ?: DefaultAlbum.UNKNOWN,
                        genre = DefaultAlbum.UNKNOWN, //TODO. decide whether to remove it or set minSdk == 30
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