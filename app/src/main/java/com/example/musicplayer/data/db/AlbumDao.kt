package com.example.musicplayer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.musicplayer.data.Album
import com.example.musicplayer.data.AlbumWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Transaction
    @Query("SELECT * FROM album")
    suspend fun getAllAlbums(): List<Album>
    @Transaction
    @Query("SELECT * FROM album")
    fun getAllAlbumsFlow(): Flow<List<Album>>

    @Transaction
    @Query("SELECT * FROM album WHERE id = :id")
    fun getAlbumTracks(id: Int): Flow<AlbumWithTracks>

    @Transaction
    @Insert
    suspend fun insert(a: Album)

    @Transaction
    @Delete
    suspend fun delete(a: Album)

}