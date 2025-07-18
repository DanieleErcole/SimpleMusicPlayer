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
import java.time.Instant

@Dao
interface QueueDao {

    @Transaction
    @Query("SELECT * FROM queue ORDER BY added ASC")
    suspend fun getQueueTracks(): List<QueuedTrack>
    @Transaction
    @Query("SELECT * FROM queue ORDER BY added ASC")
    fun getQueueTracksFlow(): Flow<List<QueuedTrack>>

    @Transaction
    @Query("SELECT * FROM queue WHERE isCurrent = 1")
    suspend fun currentPlaying(): QueuedTrack?
    @Transaction
    @Query("SELECT * FROM queue WHERE isCurrent = 1")
    fun currentPlayingFlow(): Flow<QueuedTrack?>
    @Transaction
    @Query("SELECT * FROM queue WHERE added > :currentAddedDate ORDER BY added ASC LIMIT 1")
    suspend fun nextSong(currentAddedDate: Long): QueuedTrack?
    @Transaction
    @Query("UPDATE queue SET isCurrent = 1 WHERE trackId = :trackId")
    suspend fun playQueued(trackId: Long)
    @Transaction
    @Query("UPDATE track SET lastPlayed = :playDate WHERE trackId = :trackId")
    suspend fun playTrack(trackId: Long, playDate: Long)
    @Transaction
    @Query("UPDATE queue SET lastPosition = :pos WHERE trackId = :trackId")
    suspend fun storeCurrentPos(trackId: Long, pos: Long)
    @Transaction
    @Query("UPDATE queue SET isCurrent = 0 WHERE isCurrent = 1")
    suspend fun finish()

    @Transaction
    @Query("DELETE FROM queue WHERE isCurrent = 0")
    suspend fun clear()

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: QueueItem)

    @Transaction
    @Delete
    suspend fun delete(item: QueueItem)

    @Transaction
    suspend fun replaceCurrent(new: QueueItem) {
        val cur = currentPlaying()
        cur?.let {
            delete(it.queuedItem)
            queueAndPlay(new)
        }
    }

    @Transaction
    suspend fun play(track: Long) {
        playTrack(track, Instant.now().toEpochMilli())
        playQueued(track)
    }

    @Transaction
    suspend fun queueAndPlay(item: QueueItem) {
        insert(item)
        playTrack(item.track, Instant.now().toEpochMilli())
        playQueued(item.track)
    }

    @Transaction
    suspend fun finishAndPlayNext(): QueuedTrack? {
        val cur = currentPlaying()
        return cur?.let {
            finish()
            val next = nextSong(it.queuedItem.added.toEpochMilli())
            next?.let {
                play(it.queuedItem.track)
            }
            next
        }
    }

}