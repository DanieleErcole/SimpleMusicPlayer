package com.example.musicplayer

import com.example.musicplayer.data.QueueItem
import com.example.musicplayer.data.db.AlbumDao
import com.example.musicplayer.data.db.QueueDao
import com.example.musicplayer.data.db.TrackDao
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DbQueueTests : DbTests() {

    private lateinit var dao: QueueDao
    private lateinit var tDao: TrackDao
    private lateinit var aDao: AlbumDao

    private val album = testAlbum()
    private val track = testTrack(0, album)
    private val items = listOf(
        QueueItem(
            track = track.trackId,
            position = 0,
            isCurrent = false,
            lastPosition = null,
        ),
        QueueItem(
            track = track.trackId,
            position = 1,
            isCurrent = false,
            lastPosition = null,
        ),
        QueueItem(
            track = track.trackId,
            position = 2,
            isCurrent = false,
            lastPosition = null,
        )
    )

    override fun initDao() {
        dao = db.queueDao()
        tDao = db.trackDao()
        aDao = db.albumDao()
    }

    @Before
    fun insert() = runTest {
        aDao.insert(album)
        tDao.insert(track)
        dao.insertBlk(items)
    }

    @Test
    fun getQueueTracksTest() = runTest {
        val queue = dao.getQueueTracks()
        assertEquals(0, queue[0].queuedItem.position)
        assertEquals(1, queue[1].queuedItem.position)
        assertEquals(2, queue[2].queuedItem.position)
    }

    @Test
    fun currentPlayingTest() = runTest {
        dao.play(items[0])
        val cur = dao.currentPlaying()
        assertNotEquals(null, cur)
    }

    @Test
    fun deleteTest() = runTest {
        dao.play(items[1])
        dao.deleteAndRefresh(items[0])

        val item = dao.getQueueTracks()[0]
        assertEquals(0, item.queuedItem.position)
        assertTrue(item.queuedItem.isCurrent)
    }

    @Test
    fun deleteMultipleTest() = runTest {
        dao.play(items[2])
        dao.deleteAllAndRefresh(items.take(2))

        val item = dao.getQueueTracks()[0]
        assertEquals(0, item.queuedItem.position)
        assertTrue(item.queuedItem.isCurrent)
    }

    @Test
    fun clearTest() = runTest {
        dao.clear()
        assertEquals(dao.size(), 0)
    }

    @Test
    fun moveTrackTest() = runTest {
        dao.play(items[0])
        dao.moveTrack(0, 1)
        val queue = dao.getQueueTracks()
        assertTrue(queue[1].queuedItem.isCurrent)
        assertEquals(1, queue[1].queuedItem.position)
    }

    @Test
    fun playNextTest() = runTest {
        dao.play(items[0])
        dao.finishAndPlayNextPos(1, false)
        val cur = dao.currentPlaying()
        assertEquals(1, cur?.queuedItem?.position)
    }

}