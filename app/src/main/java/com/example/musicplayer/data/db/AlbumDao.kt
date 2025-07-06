package com.example.musicplayer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.musicplayer.data.Album
import com.example.musicplayer.data.AlbumWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Query("SELECT * FROM album")
    suspend fun getAllAlbums(): List<Album>
    @Query("SELECT * FROM album")
    fun getAllAlbumsFlow(): Flow<List<Album>>

    @Query("SELECT * FROM album WHERE id = :id")
    fun getAlbumTracks(id: Int): Flow<AlbumWithTracks>

    @Insert
    suspend fun insert(a: Album)

    @Delete
    suspend fun delete(a: Album)

}