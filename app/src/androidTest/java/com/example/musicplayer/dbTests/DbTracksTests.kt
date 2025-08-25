package com.example.musicplayer.dbTests

import com.example.musicplayer.data.db.AlbumDao
import com.example.musicplayer.data.db.TrackDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DbTracksTests : DbTests() {

    private lateinit var dao: TrackDao
    private lateinit var aDao: AlbumDao

    private val album = testAlbum()
    private val tracks = listOf(
        testTrack(0, album),
        testTrack(1, album, "Artist1"),
        testTrack(2, album, "Artist1")
    )

    override fun initDao() {
        dao = db.trackDao()
        aDao = db.albumDao()
    }

    @Before
    fun insert() = runTest {
        aDao.insert(album)
        tracks.forEach {
            dao.insert(it)
        }
    }

    @Test
    fun getAllArtistsTest() = runTest {
        val artists = dao.getAllArtists(null).first()
        assertTrue(artists.containsAll(listOf("Unknown", "Artist1")))
    }

    @Test
    fun getArtistTrackTest() = runTest {
        val items = dao.getArtistTracks("Artist1", null).first()
        assertEquals(2, items.size)
    }

    @Test
    fun correctInsertTest() = runTest {
        assertEquals(3, dao.getAllTracks().size)
    }

    @Test
    fun deleteTest() = runTest {
        dao.delete(tracks[1])
        assertEquals(2, dao.getAllTracks().size)
    }

    @Test
    fun deleteBlkTest() = runTest {
        dao.deleteBlk(listOf(0, 2))
        assertEquals(1, dao.getAllTracks().size)
    }

}