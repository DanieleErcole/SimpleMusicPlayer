package com.example.musicplayer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.musicplayer.data.QueueItem
import com.example.musicplayer.data.QueuedTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface QueueDao {

    @Transaction
    @Query("SELECT * FROM queue ORDER BY added ASC")
    suspend fun getQueueTracks(): List<QueuedTrack>
    @Transaction
    @Query("SELECT * FROM queue ORDER BY added ASC")
    fun getQueueTracksFlow(): Flow<List<QueuedTrack>>

    @Transaction
    @Query("SELECT * FROM queue WHERE position IS NOT NULL")
    suspend fun currentPlaying(): QueuedTrack?
    @Transaction
    @Query("SELECT * FROM queue WHERE position IS NOT NULL")
    fun currentPlayingFlow(): Flow<QueuedTrack?>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: QueueItem)

    @Transaction
    @Delete
    suspend fun delete(item: QueueItem)

}