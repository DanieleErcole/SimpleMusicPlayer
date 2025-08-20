package com.example.musicplayer.data

import com.example.musicplayer.data.db.AlbumDao
import com.example.musicplayer.data.db.PlaylistDao
import com.example.musicplayer.data.db.QueueDao
import com.example.musicplayer.data.db.TrackDao
import kotlinx.coroutines.flow.Flow
import java.time.Instant

class LocalMusicRepository(
    private val trackDao: TrackDao,
    private val plDao: PlaylistDao,
    private val albumDao: AlbumDao,
    private val queueDao: QueueDao
) : MusicRepository {

    override suspend fun getAllTracks(): List<TrackWithAlbum> = trackDao.getAllTracks()
    override fun getAllTracksFlow(genres: List<String>, empty: Boolean, searchString: String?): Flow<List<TrackWithAlbum>> =
        trackDao.getAllTracksFlow(genres, empty, searchString)
    override fun getArtistTracks(artist: String, searchString: String?): Flow<List<TrackWithAlbum>> = trackDao.getArtistTracks(artist, searchString)
    override fun getAllArtists(searchString: String?): Flow<List<String>> = trackDao.getAllArtists(searchString)
    override fun getAllGenres(): Flow<List<String>> = trackDao.getAllGenres()
    override suspend fun newTrack(t: Track) = trackDao.insert(t)
    override suspend fun deleteTrack(t: Track) = trackDao.delete(t)
    override suspend fun deleteTrackBlk(trackList: List<Track>) = trackDao.deleteBlk(trackList.map { it.trackId })

    override fun getAllPlaylists(): Flow<List<Playlist>> = plDao.getAllPlaylists()
    override fun getPlaylistsWithThumbnails(searchString: String?): Flow<List<PlaylistWithThumbnails>> = plDao.getPlaylistsWithThumbnails(searchString)
    override fun getPlaylistTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>> = plDao.getPlaylistTracks(id, searchString)
    override suspend fun addToPlaylist(tracks: List<Long>, playlist: Long) = plDao.addToPlaylist(tracks.map {
        TrackAddedToPlaylist(playlist, it, Instant.now())
    })
    override suspend fun removeFromPlaylist(tracks: List<Long>, playlist: Long) = plDao.removeFromPlaylist(tracks, playlist)
    override suspend fun newPlaylist(pl: Playlist) = plDao.insert(pl)
    override suspend fun deletePlaylist(pl: Playlist) = plDao.delete(pl)
    override suspend fun renamePlaylist(id: Long, newName: String) = plDao.rename(id, newName)

    override suspend fun getAllAlbums(): List<Album> = albumDao.getAllAlbums()
    override fun getAllAlbumsFlow(searchString: String?): Flow<List<Album>> = albumDao.getAllAlbumsFlow(searchString)
    override fun getAlbumTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>> = albumDao.getAlbumTracks(id, searchString)
    override suspend fun getAlbumTracksCount(id: Long): Int = albumDao.getAlbumTracksCount(id)
    override suspend fun newAlbum(a: Album) = albumDao.insert(a)
    override suspend fun deleteAlbum(a: Album) = albumDao.delete(a)

    override suspend fun queueSize(): Int = queueDao.size()
    override suspend fun getQueueTracks(): List<QueuedTrack> = queueDao.getQueueTracks()
    override suspend fun currentPlaying(): QueuedTrack? = queueDao.currentPlaying()
    override suspend fun storeCurrentPos(pos: Long) = queueDao.storeCurrentPos(pos)
    override suspend fun clearQueue() = queueDao.clear()
    override fun currentPlayingFlow(): Flow<QueuedTrack?> = queueDao.currentPlayingFlow()
    override suspend fun queueAll(items: List<QueueItem>) = queueDao.insertBlk(items)
    override suspend fun dequeueAll(items: List<QueueItem>) = queueDao.deleteAllAndRefresh(items)
    override suspend fun queueAndPlay(item: QueueItem) = queueDao.queueAndPlay(item)
    override suspend fun finishAndPlayNextPos(nextPos: Int, doNothingToCurrent: Boolean) = queueDao.finishAndPlayNextPos(nextPos, doNothingToCurrent)
    override suspend fun deleteAndPlayNextPos(nextPos: Int) = queueDao.deleteAndPlayNextPos(nextPos)
    override suspend fun replaceQueue(new: List<QueueItem>) = queueDao.replaceQueue(new)
    override suspend fun moveTrack(from: Int, to: Int) = queueDao.moveTrack(from, to)
}