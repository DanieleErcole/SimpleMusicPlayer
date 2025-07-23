package com.example.musicplayer.data

import com.example.musicplayer.data.db.AlbumDao
import com.example.musicplayer.data.db.PlaylistDao
import com.example.musicplayer.data.db.QueueDao
import com.example.musicplayer.data.db.TrackDao
import kotlinx.coroutines.flow.Flow

class LocalMusicRepository(
    private val trackDao: TrackDao,
    private val plDao: PlaylistDao,
    private val albumDao: AlbumDao,
    private val queueDao: QueueDao
) : MusicRepository {

    override suspend fun getAllTracks(): List<TrackWithAlbum> = trackDao.getAllTracks()
    override fun getAllTracksFlow(artists: List<String>?, searchString: String?): Flow<List<TrackWithAlbum>> = trackDao.getAllTracksFlow(artists, searchString)
    override fun getAllArtists(): Flow<List<String>> = trackDao.getAllArtists()
    override suspend fun newTrack(t: Track) = trackDao.insert(t)
    override suspend fun deleteTrack(t: Track) = trackDao.delete(t)
    override suspend fun deleteTrackBlk(trackList: List<Track>) = trackDao.deleteBlk(trackList.map { it.trackId })

    override fun getAllPlaylists(): Flow<List<Playlist>> = plDao.getAllPlaylists()
    override fun getPlaylistTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>> = plDao.getPlaylistTracks(id, searchString)
    override fun getTrackPlaylists(id: Long): Flow<List<Playlist>> = plDao.getTrackPlaylists(id)
    override suspend fun newPlaylist(pl: Playlist) = plDao.insert(pl)
    override suspend fun deletePlaylist(pl: Playlist) = plDao.delete(pl)

    override suspend fun getAllAlbums(): List<Album> = albumDao.getAllAlbums()
    override fun getAllAlbumsFlow(): Flow<List<Album>> = albumDao.getAllAlbumsFlow()
    override fun getAlbumTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>> = albumDao.getAlbumTracks(id, searchString)
    override suspend fun newAlbum(a: Album) = albumDao.insert(a)
    override suspend fun deleteAlbum(a: Album) = albumDao.delete(a)

    override suspend fun queueSize(): Int = queueDao.size()
    override suspend fun getQueueTracks(): List<QueuedTrack> = queueDao.getQueueTracks()
    override fun getQueueTracksFlow(): Flow<List<QueuedTrack>> = queueDao.getQueueTracksFlow()
    override suspend fun currentPlaying(): QueuedTrack? = queueDao.currentPlaying()
    override suspend fun storeCurrentPos(pos: Long) = queueDao.storeCurrentPos(pos)
    override suspend fun clearQueue() = queueDao.clear()
    override fun currentPlayingFlow(): Flow<QueuedTrack?> = queueDao.currentPlayingFlow()
    override suspend fun queue(item: QueueItem) = queueDao.insert(item)
    override suspend fun queueAll(items: List<QueueItem>) = queueDao.insertBlk(items)
    override suspend fun remove(item: QueueItem) = queueDao.deleteAndRefresh(item)
    override suspend fun queueAndPlay(item: QueueItem) = queueDao.queueAndPlay(item)
    override suspend fun finishAndPlayNextPos(nextPos: Int, doNothingToCurrent: Boolean) {
        queueDao.finishAndPlayNextPos(nextPos, doNothingToCurrent)
    }
    override suspend fun replaceQueue(new: List<QueueItem>) = queueDao.replaceQueue(new)
}