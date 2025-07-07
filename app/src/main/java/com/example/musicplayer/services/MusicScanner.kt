package com.example.musicplayer.services

import android.util.Log
import androidx.media3.common.MediaItem
import com.example.musicplayer.data.Album
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.Track
import com.example.musicplayer.data.UserPreferencesRepository
import java.io.File
import java.time.LocalDateTime
import java.time.ZonedDateTime

class MusicScanner(private val musicRepo: MusicRepository, private val userPrefsRepo: UserPreferencesRepository) {

    suspend fun scanDirectories() {
        // This will automatically run when the scannedDirectories changes
        userPrefsRepo.scannedDirectories.collect { dirs ->
            val albums = musicRepo.getAllAlbums()
            val tracks = musicRepo.getAllTracks()

            dirs.forEach {
                scanDir(it).forEach { (path, audioFile) ->
                    audioFile.mediaMetadata.let {
                        val album = albums.find { a -> it.albumTitle.toString() == a.name }
                        val track = tracks.find { it.track.location == path }

                        val albumId = if (album == null) {
                            val a = Album(
                                name = it.albumTitle.toString(),
                                thumbnail = it.artworkUri.toString(),
                                artist = it.albumArtist.toString()
                            )
                            musicRepo.newAlbum(a)
                            a.id
                        } else album.id

                        Log.d(MusicScanner::class.simpleName, "Adding album: $albumId")

                        if (track == null) // If the track scanned does not exist in the db add it
                            musicRepo.newTrack(
                                Track(
                                    location = path,
                                    title = it.title.toString(),
                                    album = albumId,
                                    artist = it.artist.toString(),
                                    composer = it.composer.toString(),
                                    genre = it.genre.toString(),
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
                            musicRepo.deleteTrack(track.track)
                    }
                }
            }
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