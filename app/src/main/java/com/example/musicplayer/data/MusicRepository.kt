package com.example.musicplayer.data

import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    suspend fun getAllTracks(): List<TrackWithAlbum>
    fun getAllTracksFlow(genres: List<String>, empty: Boolean, searchString: String?): Flow<List<TrackWithAlbum>>
    fun getArtistTracks(artist: String, searchString: String?): Flow<List<TrackWithAlbum>>
    fun getAllArtists(searchString: String?): Flow<List<String>>
    fun getAllGenres(): Flow<List<String>>
    suspend fun newTrack(t: Track)
    suspend fun deleteTrack(t: Track)
    suspend fun deleteTrackBlk(trackList: List<Track>)

    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistsWithThumbnails(searchString: String?): Flow<List<PlaylistWithThumbnails>>
    fun getPlaylistTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>>
    suspend fun addToPlaylist(tracks: List<Long>, playlist: Long)
    suspend fun removeFromPlaylist(tracks: List<Long>, playlist: Long)
    suspend fun newPlaylist(pl: Playlist)
    suspend fun deletePlaylist(pl: Playlist)
    suspend fun renamePlaylist(id: Long, newName: String)

    suspend fun getAllAlbums(): List<Album>
    fun getAllAlbumsFlow(searchString: String? = null): Flow<List<Album>>
    fun getAlbumTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>>
    suspend fun getAlbumTracksCount(id: Long): Int
    suspend fun newAlbum(a: Album)
    suspend fun deleteAlbum(a: Album)

    suspend fun queueSize(): Int
    suspend fun getQueueTracks(): List<QueuedTrack>
    suspend fun currentPlaying(): QueuedTrack?
    suspend fun setPlay(pos: Int)
    suspend fun storeCurrentPos(pos: Long)
    fun currentPlayingFlow(): Flow<QueuedTrack?>
    suspend fun clearQueue()
    suspend fun queue(item: QueueItem)
    suspend fun queueAll(items: List<QueueItem>)
    suspend fun dequeueAll(items: List<QueueItem>)
    suspend fun finishAndPlayNextPos(nextPos: Int, doNothingToCurrent: Boolean = false)
    suspend fun deleteCurrentAndPlayNextPos()
    suspend fun replaceQueue(new: List<QueueItem>)
    suspend fun moveTrack(from: Int, to: Int)
}