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
    fun getAllPlaylistsFiltered(searchString: String?): Flow<List<Playlist>>
    fun getPlaylistTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>>
    fun getTrackPlaylists(id: Long): Flow<List<Playlist>>
    suspend fun addToPlaylist(tracks: List<Long>, playlist: Long)
    suspend fun newPlaylist(pl: Playlist)
    suspend fun deletePlaylist(pl: Playlist)
    suspend fun renamePlaylist(id: Long, newName: String)

    suspend fun getAllAlbums(): List<Album>
    fun getAllAlbumsFlow(searchString: String? = null): Flow<List<Album>>
    fun getAlbumTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>>
    suspend fun newAlbum(a: Album)
    suspend fun deleteAlbum(a: Album)

    suspend fun queueSize(): Int
    suspend fun getQueueTracks(): List<QueuedTrack>
    fun getQueueTracksFlow(): Flow<List<QueuedTrack>>
    suspend fun currentPlaying(): QueuedTrack?
    suspend fun storeCurrentPos(pos: Long)
    fun currentPlayingFlow(): Flow<QueuedTrack?>
    suspend fun clearQueue()
    suspend fun queue(item: QueueItem)
    suspend fun queueAll(items: List<QueueItem>)
    suspend fun dequeueAll(items: List<QueueItem>)
    suspend fun queueAndPlay(item: QueueItem)
    suspend fun finishAndPlayNextPos(nextPos: Int, doNothingToCurrent: Boolean = false)
    suspend fun replaceQueue(new: List<QueueItem>)
    suspend fun moveTrack(from: Int, to: Int)
}