package com.example.musicplayer.data

import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    suspend fun getAllTracks(): List<TrackWithAlbum>
    fun getAllTracksFlow(artists: List<String>?, searchString: String?): Flow<List<TrackWithAlbum>>
    fun getAllArtists(): Flow<List<String>>
    suspend fun newTrack(t: Track)
    suspend fun deleteTrack(t: Track)
    suspend fun deleteTrackBlk(trackList: List<Track>)

    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>>
    fun getTrackPlaylists(id: Long): Flow<List<Playlist>>
    suspend fun newPlaylist(pl: Playlist)
    suspend fun deletePlaylist(pl: Playlist)

    suspend fun getAllAlbums(): List<Album>
    fun getAllAlbumsFlow(): Flow<List<Album>>
    fun getAlbumTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>>
    suspend fun newAlbum(a: Album)
    suspend fun deleteAlbum(a: Album)

    suspend fun getQueueTracks(): List<QueuedTrack>
    fun getQueueTracksFlow(): Flow<List<QueuedTrack>>
    suspend fun currentPlaying(): QueuedTrack?
    suspend fun nextSong(currentAddedDate: Long): QueuedTrack?
    suspend fun storeCurrentPos(trackId: Long, pos: Long)
    fun currentPlayingFlow(): Flow<QueuedTrack?>
    suspend fun clearQueue()
    suspend fun queue(item: QueueItem)
    suspend fun remove(item: QueueItem)
    suspend fun replaceCurrent(new: QueueItem)
    suspend fun play(trackId: Long)
    suspend fun queueAndPlay(item: QueueItem)
    suspend fun finishAndPlayNext(): QueuedTrack?
}