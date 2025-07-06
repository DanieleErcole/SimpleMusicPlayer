package com.example.musicplayer.data.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.data.QueueItem
import com.example.musicplayer.data.QueuedTrack
import kotlinx.coroutines.flow.Flow

interface QueueDao {

    @Query("SELECT * FROM queue ORDER BY added ASC")
    suspend fun getQueueTracks(): List<QueuedTrack>
    @Query("SELECT * FROM queue ORDER BY added ASC")
    fun getQueueTracksFlow(): Flow<List<QueuedTrack>>

    @Query("SELECT * FROM queue WHERE position IS NOT NULL")
    suspend fun currentPlaying(): QueuedTrack?
    @Query("SELECT * FROM queue WHERE position IS NOT NULL")
    fun currentPlayingFlow(): Flow<QueuedTrack?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: QueueItem)

    @Delete
    suspend fun delete(item: QueueItem)

}