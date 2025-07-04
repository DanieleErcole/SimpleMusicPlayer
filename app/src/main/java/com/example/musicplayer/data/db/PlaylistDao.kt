package com.example.musicplayer.data.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.data.Playlist
import com.example.musicplayer.data.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

interface PlaylistDao {

    @Query("SELECT * FROM playlist ORDER BY created ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM playlist WHERE id = :id")
    fun getPlaylistTracks(id: Int): Flow<PlaylistWithTracks>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun newPlaylist(pl: Playlist)

    @Delete
    suspend fun deletePlaylist(pl: Playlist)

}