package com.example.musicplayer.data

import com.example.musicplayer.data.db.AlbumDao
import com.example.musicplayer.data.db.PlaylistDao
import com.example.musicplayer.data.db.TrackDao
import kotlinx.coroutines.flow.Flow

class LocalMusicRepository(
    private val trackDao: TrackDao,
    private val plDao: PlaylistDao,
    private val albumDao: AlbumDao
) : MusicRepository {

    override suspend fun getAllTracks(): List<TrackWithAlbum> = trackDao.getAllTracks()
    override fun getAllTracksFlow(): Flow<List<TrackWithAlbum>> = trackDao.getAllTracksFlow()
    override fun getAllArtists(): Flow<List<String>> = trackDao.getAllArtists()
    override fun getArtistTracks(name: String): Flow<List<TrackWithAlbum>> = trackDao.getArtistTracks(name)
    override suspend fun newTrack(t: Track) = trackDao.newTrack(t)
    override suspend fun deleteTrack(t: Track) = trackDao.deleteTrack(t)

    override fun getAllPlaylists(): Flow<List<Playlist>> = plDao.getAllPlaylists()
    override fun getPlaylistTracks(id: Int): Flow<PlaylistWithTracks> = plDao.getPlaylistTracks(id)
    override suspend fun newPlaylist(pl: Playlist) = plDao.newPlaylist(pl)
    override suspend fun deletePlaylist(pl: Playlist) = plDao.deletePlaylist(pl)

    override suspend fun getAllAlbums(): List<Album> = albumDao.getAllAlbums()
    override fun getAllAlbumsFlow(): Flow<List<Album>> = albumDao.getAllAlbumsFlow()
    override fun getAlbumTracks(id: Int): Flow<AlbumWithTracks> = albumDao.getAlbumTracks(id)
    override suspend fun newAlbum(a: Album) = albumDao.newAlbum(a)
    override suspend fun deleteAlbum(a: Album) = albumDao.deleteAlbum(a)

}