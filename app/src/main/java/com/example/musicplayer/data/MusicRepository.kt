package com.example.musicplayer.data

import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    suspend fun getAllTracks(): List<TrackWithAlbum>
    fun getAllTracksFlow(): Flow<List<TrackWithAlbum>>
    fun getAllArtists(): Flow<List<String>>
    suspend fun newTrack(t: Track)
    suspend fun deleteTrack(t: Track)
    suspend fun deleteTrackBlk(trackList: List<Track>)

    fun getAllPlaylists(): Flow<List<PlaylistWithTracks>>
    fun getPlaylistTracks(id: Int): Flow<PlaylistWithTracks>
    fun getTrackPlaylists(id: Int): Flow<List<Playlist>>
    suspend fun newPlaylist(pl: Playlist)
    suspend fun deletePlaylist(pl: Playlist)

    suspend fun getAllAlbums(): List<Album>
    fun getAllAlbumsFlow(): Flow<List<Album>>
    fun getAlbumTracks(id: Int): Flow<AlbumWithTracks>
    suspend fun newAlbum(a: Album)
    suspend fun deleteAlbum(a: Album)

    suspend fun getQueueTracks(): List<QueuedTrack>
    fun getQueueTracksFlow(): Flow<List<QueuedTrack>>
    suspend fun currentPlaying(): QueuedTrack?
    fun currentPlayingFlow(): Flow<QueuedTrack?>
    suspend fun queue(item: QueueItem)
    suspend fun remove(item: QueueItem)

}