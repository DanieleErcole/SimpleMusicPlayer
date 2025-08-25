package com.example.musicplayer.dbTests

import com.example.musicplayer.data.Album
import com.example.musicplayer.data.Playlist
import com.example.musicplayer.data.Track
import com.example.musicplayer.utils.DefaultAlbum
import java.time.Instant

fun testPlaylist(id: Long): Playlist = Playlist(
    name = "p$id",
    created = Instant.now()
)

fun testAlbum(): Album = Album(
    id = DefaultAlbum.UNKNOWN_ID,
    name = DefaultAlbum.UNKNOWN_ALBUM_NAME,
    thumbnail = null,
    artist = DefaultAlbum.UNKNOWN_ARTIST
)

fun testTrack(id: Long, album: Album, artist: String = "Unknown"): Track = Track(
    trackId = id,
    location = "/",
    title = "t$id",
    album = album.id,
    artist = artist,
    year = 2025,
    durationMs = 100000,
    addedToLibrary = Instant.now(),
)