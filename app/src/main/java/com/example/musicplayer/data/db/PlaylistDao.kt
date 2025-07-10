package com.example.musicplayer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.musicplayer.data.Playlist
import com.example.musicplayer.data.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Transaction
    @Query("SELECT * FROM playlist ORDER BY created ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM playlist WHERE playlistId = :id")
    fun getPlaylistTracks(id: Int): Flow<PlaylistWithTracks>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pl: Playlist)

    @Transaction
    @Delete
    suspend fun delete(pl: Playlist)

}