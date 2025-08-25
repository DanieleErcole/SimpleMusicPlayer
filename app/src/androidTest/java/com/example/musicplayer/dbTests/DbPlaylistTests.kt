package com.example.musicplayer.dbTests

import com.example.musicplayer.data.TrackAddedToPlaylist
import com.example.musicplayer.data.db.AlbumDao
import com.example.musicplayer.data.db.PlaylistDao
import com.example.musicplayer.data.db.TrackDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

class DbPlaylistTests : DbTests() {

    private lateinit var dao: PlaylistDao
    private lateinit var tDao: TrackDao
    private lateinit var aDao: AlbumDao

    private var playlist = testPlaylist(0)
    private val album = testAlbum()
    private val tracks = listOf(
        testTrack(0, album),
        testTrack(1, album, "Artist1"),
        testTrack(2, album, "Artist1")
    )

    override fun initDao() {
        dao = db.playlistDao()
        tDao = db.trackDao()
        aDao = db.albumDao()
    }

    @Before
    fun insert() = runTest {
        aDao.insert(album)
        tracks.forEach { tDao.insert(it) }

        dao.insert(playlist)
        playlist = playlist.copy(playlistId = dao.getAllPlaylists().first().first().playlistId)

        dao.addToPlaylist(tracks.map {
            TrackAddedToPlaylist(playlist.playlistId, it.trackId, Instant.now())
        })
    }

    @Test
    fun tracksAddedTest() = runTest {
        val items = dao.getPlaylistTracks(playlist.playlistId, null).first()
        assertEquals(3, items.size)
    }

    @Test
    fun removeTracksTest() = runTest {
        dao.removeFromPlaylist(tracks.map { it.trackId }.take(2), playlist.playlistId)
        val items = dao.getPlaylistTracks(playlist.playlistId, null).first()
        assertEquals(1, items.size)
    }

    @Test
    fun renameTest() = runTest {
        dao.rename(playlist.playlistId, "New Name")
        val pl = dao.getAllPlaylists().first().first()
        assertEquals("New Name", pl.name)
    }

    @Test
    fun deleteTest() = runTest {
        dao.delete(playlist)
        assertEquals(0, dao.getAllPlaylists().first().size)
    }

}