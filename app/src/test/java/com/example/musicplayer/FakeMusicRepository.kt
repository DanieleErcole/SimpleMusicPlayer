package com.example.musicplayer

import com.example.musicplayer.data.Album
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.Playlist
import com.example.musicplayer.data.PlaylistWithThumbnails
import com.example.musicplayer.data.QueueItem
import com.example.musicplayer.data.QueuedTrack
import com.example.musicplayer.data.Track
import com.example.musicplayer.data.TrackWithAlbum
import kotlinx.coroutines.flow.Flow

class FakeMusicRepository : MusicRepository {

    override suspend fun getAllTracks(): List<TrackWithAlbum> {
        TODO("Not yet implemented")
    }

    override fun getAllTracksFlow(
        artists: List<String>?,
        searchString: String?
    ): Flow<List<TrackWithAlbum>> {
        TODO("Not yet implemented")
    }

    override fun getAllArtists(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun newTrack(t: Track) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrack(t: Track) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrackBlk(trackList: List<Track>) {
        TODO("Not yet implemented")
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        TODO("Not yet implemented")
    }

    override fun getPlaylistsWithThumbnails(searchString: String?): Flow<List<PlaylistWithThumbnails>> {
        TODO("Not yet implemented")
    }

    override fun getPlaylistTracks(
        id: Long,
        searchString: String?
    ): Flow<List<TrackWithAlbum>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToPlaylist(
        tracks: List<Long>,
        playlist: Long
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromPlaylist(
        tracks: List<Long>,
        playlist: Long
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun newPlaylist(pl: Playlist) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylist(pl: Playlist) {
        TODO("Not yet implemented")
    }

    override suspend fun renamePlaylist(id: Long, newName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlbums(): List<Album> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsFlow(searchString: String?): Flow<List<Album>> {
        TODO("Not yet implemented")
    }

    override fun getAlbumTracks(
        id: Long,
        searchString: String?
    ): Flow<List<TrackWithAlbum>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumTracksCount(id: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun newAlbum(a: Album) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlbum(a: Album) {
        TODO("Not yet implemented")
    }

    override suspend fun queueSize(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getQueueTracks(): List<QueuedTrack> {
        TODO("Not yet implemented")
    }

    override suspend fun currentPlaying(): QueuedTrack? {
        TODO("Not yet implemented")
    }

    override suspend fun storeCurrentPos(pos: Long) {
        TODO("Not yet implemented")
    }

    override fun currentPlayingFlow(): Flow<QueuedTrack?> {
        TODO("Not yet implemented")
    }

    override suspend fun clearQueue() {
        TODO("Not yet implemented")
    }

    override suspend fun queueAll(items: List<QueueItem>) {
        TODO("Not yet implemented")
    }

    override suspend fun dequeueAll(items: List<QueueItem>) {
        TODO("Not yet implemented")
    }

    override suspend fun queueAndPlay(item: QueueItem) {
        TODO("Not yet implemented")
    }

    override suspend fun finishAndPlayNextPos(
        nextPos: Int,
        doNothingToCurrent: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun replaceQueue(new: List<QueueItem>) {
        TODO("Not yet implemented")
    }

    override suspend fun moveTrack(from: Int, to: Int) {
        TODO("Not yet implemented")
    }

}