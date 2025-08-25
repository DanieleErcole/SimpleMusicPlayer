package com.example.musicplayer.dbTests

import com.example.musicplayer.data.Album
import com.example.musicplayer.data.db.AlbumDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DbAlbumTests : DbTests() {

    private lateinit var dao: AlbumDao
    private val albums = listOf(
        Album(
            id = 0,
            name = "a1",
            thumbnail = null,
        ),
        Album(
            id = 1,
            name = "a2",
            thumbnail = null,
        )
    )

    override fun initDao() {
        dao = db.albumDao()
    }

    @Before
    fun insert() = runTest {
        dao.insert(albums[0])
        dao.insert(albums[1])
    }

    @Test
    fun insertAlbumsTest() = runTest {
        val items = dao.getAllAlbums()
        assertEquals(albums[0], items[0])
        assertEquals(albums[1], items[1])
    }

    @Test
    fun searchAlbumsTest() = runTest {
        val items = dao.getAllAlbumsFlow("1").first()
        assertEquals(albums[0].name, items[0].name)
    }

    @Test
    fun deleteAlbumTest() = runTest {
        dao.delete(dao.getAllAlbums().first())
        assertTrue(dao.getAllAlbums().size == 1)
    }

}