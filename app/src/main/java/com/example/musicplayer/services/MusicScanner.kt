package com.example.musicplayer.services

import android.util.Log
import androidx.compose.ui.res.painterResource
import androidx.media3.common.MediaItem
import com.example.musicplayer.R
import com.example.musicplayer.data.Album
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.Track
import com.example.musicplayer.utils.DefaultAlbum
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.time.ZonedDateTime

class MusicScanner(private val musicRepo: MusicRepository) {

    suspend fun scanDirectories(scannedDirs: List<String>) {
        val albums = musicRepo.getAllAlbums()
        val tracks = musicRepo.getAllTracks()
        val unknownAlbum = addUnknownAlbum()

        // Delete tracks which location is not present in the scanned dirs (previously deleted)
        val toDelete = mutableListOf<Track>()
        tracks.forEach {
            val f = File(it.internal.location)
            if (f.exists() && !scannedDirs.contains(f.parent!!))
                toDelete.add(it.internal)
        }
        musicRepo.deleteTrackBlk(toDelete)

        scannedDirs.forEach { dir ->
            Log.i(MusicScanner::class.simpleName, "Scanning directory $dir")
            scanDir(dir).forEach { (path, audioFile) ->
                audioFile.mediaMetadata.let {
                    // Check whether the track and its album already exists
                    val album = albums.find { a -> it.albumTitle.toString() == a.name }
                    val track = tracks.find { it.internal.location == path }

                    // Get the album id
                    val albumId = album?.id ?: run {
                        // if the album title is null or empty we associate it to the unknown album
                        if (it.albumTitle == null || it.albumTitle.toString().isEmpty())
                            unknownAlbum.id
                        else { // If we're here the album is valid and must be added to the db
                            val a = Album(
                                name = it.albumTitle.toString(),
                                thumbnail = it.artworkUri?.toString(),
                                artist = it.albumArtist.toString()
                            )
                            musicRepo.newAlbum(a)
                            a.id
                        }
                    }

                    Log.i(MusicScanner::class.simpleName, "Adding album: $albumId")

                    if (track == null) // If the scanned track does not exist in the db add it
                        musicRepo.newTrack(
                            Track(
                                location = path,
                                title = it.title?.toString() ?: DefaultAlbum.UNKNOWN,
                                album = albumId,
                                artist = it.artist?.toString() ?: DefaultAlbum.UNKNOWN,
                                composer = it.composer?.toString() ?: DefaultAlbum.UNKNOWN,
                                genre = it.genre?.toString() ?: DefaultAlbum.UNKNOWN,
                                trackNumber = it.trackNumber ?: 1,
                                discNumber = it.discNumber ?: 1,
                                year = it.releaseYear ?: 0,
                                addedToLibrary = ZonedDateTime.now(),
                                lastPlayed = null,
                                durationMs = it.durationMs ?: 0,
                                playedCount = 0
                            )
                        )
                    else if (!File(path).exists()) // If it already exists in the db but not in the path anymore delete it
                        musicRepo.deleteTrack(track.internal)
                }
            }
        }
        Log.i(MusicScanner::class.simpleName, "Finished scanning dirs")
    }

    private suspend fun addUnknownAlbum(): Album {
        return musicRepo.getAllAlbums().find { it.name == DefaultAlbum.UNKNOWN_ALBUM_NAME } ?: run {
            val newAlbum = Album(
                name = DefaultAlbum.UNKNOWN_ALBUM_NAME,
                thumbnail = null,
                artist = DefaultAlbum.UNKNOWN_ARTIST
            )
            musicRepo.newAlbum(newAlbum)
            newAlbum
        }
    }

    private fun scanDir(dirPath: String): List<Pair<String, MediaItem>> {
        val dir = File(dirPath)
        val fileList = mutableListOf<Pair<String, MediaItem>>()

        if (dir.isDirectory) {
            dir.listFiles()?.forEach { file ->
                if (file.isFile && isAudioFile(file))
                    fileList.add(Pair(file.absolutePath, MediaItem.fromUri(file.absolutePath)))
                else if (file.isDirectory)
                    fileList.addAll(scanDir(file.absolutePath))
            }
        }

        return fileList
    }

    private fun isAudioFile(file: File): Boolean {
        val audioExtensions = listOf("flac", "ogg", "mp3", "wav")
        return audioExtensions.any { file.extension.contentEquals(it, ignoreCase = true) }
    }

}