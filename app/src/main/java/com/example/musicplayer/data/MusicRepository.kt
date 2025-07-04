package com.example.musicplayer.data

import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    suspend fun getAllTracks(): List<TrackWithAlbum>
    fun getAllTracksFlow(): Flow<List<TrackWithAlbum>>
    fun getAllArtists(): Flow<List<String>>
    fun getArtistTracks(name: String): Flow<List<TrackWithAlbum>>
    suspend fun newTrack(t: Track)
    suspend fun deleteTrack(t: Track)

    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistTracks(id: Int): Flow<PlaylistWithTracks>
    suspend fun newPlaylist(pl: Playlist)
    suspend fun deletePlaylist(pl: Playlist)

    suspend fun getAllAlbums(): List<Album>
    fun getAllAlbumsFlow(): Flow<List<Album>>
    fun getAlbumTracks(id: Int): Flow<AlbumWithTracks>
    suspend fun newAlbum(a: Album)
    suspend fun deleteAlbum(a: Album)

}