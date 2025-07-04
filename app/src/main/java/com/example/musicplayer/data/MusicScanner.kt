package com.example.musicplayer.data

import android.util.Log
import androidx.media3.common.MediaItem
import java.io.File
import java.time.LocalDateTime
import kotlin.String

class MusicScanner(private val musicRepo: MusicRepository, private val userPrefsRepo: UserPreferencesRepository) {

    suspend fun scanDirectories() {
        val albums = musicRepo.getAllAlbums()
        val tracks = musicRepo.getAllTracks()

        userPrefsRepo.scannedDirectories.collect { dirString ->
            dirString.split("$").forEach {
                scanDir(it).forEach { (path, audioFile) ->
                    audioFile.mediaMetadata.let {
                        var album = albums.find { a -> it.albumTitle.toString() == a.name }
                        var track = tracks.find { it.track.location == path }

                        var albumId = if (album == null) {
                            var a = Album(
                                name = it.albumTitle.toString(),
                                thumbnail = it.artworkUri.toString(),
                                artist = it.albumArtist.toString()
                            )
                            musicRepo.newAlbum(a)
                            a.id
                        } else album.id //TODO: check and rewrite this, I think this is kinda stupid, it will be always 0 right?

                        Log.d(MusicScanner::class.simpleName, "Adding album: $albumId")

                        if (track == null) // If the track scanned does not exist in the db add it
                            musicRepo.newTrack(Track(
                                location = path,
                                title = it.title.toString(),
                                album = albumId,
                                artist = it.artist.toString(),
                                composer = it.composer.toString(),
                                genre = it.genre.toString(),
                                trackNumber = it.trackNumber ?: 1,
                                discNumber = it.discNumber ?: 1,
                                year = it.releaseYear ?: 0,
                                addedToLibrary = LocalDateTime.now(),
                                lastPlayed = null,
                                durationMs = it.durationMs ?: 0,
                                playedCount = 0
                            ))
                        else if (!File(path).exists()) // If it already exists in the db but not in the path anymore delete it
                            musicRepo.deleteTrack(track.track)
                    }
                }
            }
        }
    }

    private fun scanDir(dirPath: String): List<Pair<String, MediaItem>> {
        var dir = File(dirPath)
        var fileList = mutableListOf<Pair<String, MediaItem>>()

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