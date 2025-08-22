package com.example.musicplayer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.musicplayer.data.QueueItem
import com.example.musicplayer.data.QueuedTrack
import com.example.musicplayer.data.Track
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface QueueDao {

    @Query("SELECT COUNT(position) FROM queue")
    suspend fun size(): Int
    @Transaction
    @Query("SELECT * FROM queue ORDER BY position ASC")
    suspend fun getQueueTracks(): List<QueuedTrack>
    @Transaction
    @Query("SELECT * FROM queue ORDER BY position ASC")
    fun getQueueTracksFlow(): Flow<List<QueuedTrack>>
    @Transaction
    @Query("SELECT * FROM queue WHERE isCurrent = 1")
    suspend fun currentPlaying(): QueuedTrack?
    @Transaction
    @Query("SELECT * FROM queue WHERE isCurrent = 1")
    fun currentPlayingFlow(): Flow<QueuedTrack?>
    @Query("UPDATE queue SET position = position - 1 WHERE position > :removedPos")
    suspend fun refreshPositionsOnRemove(removedPos: Int)
    @Query("UPDATE queue SET position = :new WHERE position = :old")
    suspend fun updatePos(old: Int, new: Int)

    @Transaction
    @Query("SELECT * FROM queue WHERE position = :pos LIMIT 1")
    suspend fun track(pos: Int): QueuedTrack?
    @Query("UPDATE queue SET isCurrent = 1 WHERE trackId = :trackId AND position = :pos")
    suspend fun playQueued(trackId: Long, pos: Int)
    @Query("UPDATE track SET lastPlayed = :playDate WHERE trackId = :trackId")
    suspend fun playTrack(trackId: Long, playDate: Long)
    @Query("UPDATE queue SET lastPosition = :pos WHERE isCurrent = 1")
    suspend fun storeCurrentPos(pos: Long)
    @Query("UPDATE queue SET isCurrent = 0 WHERE isCurrent = 1")
    suspend fun finish()

    @Query("DELETE FROM queue")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: QueueItem)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBlk(items: List<QueueItem>)

    @Delete
    suspend fun delete(item: QueueItem)
    @Delete
    suspend fun deleteTrack(t: Track)
    @Delete
    suspend fun deleteBlk(items: List<QueueItem>)

    @Transaction
    suspend fun play(track: QueueItem) {
        playTrack(track.track, Instant.now().toEpochMilli())
        playQueued(track.track, track.position)
    }

    @Transaction
    suspend fun play(pos: Int) {
        track(pos)?.queuedItem?.let {
            play(it)
        }
    }

    @Transaction
    suspend fun finishAndPlayNextPos(nextPos: Int, doNothing: Boolean) {
        currentPlaying()?.let {
            track(nextPos)?.let {
                finish()
                play(it.queuedItem)
            } ?: run {
                if (!doNothing)
                    finish()
            }
        }
    }

    @Transaction
    suspend fun deleteCurrentAndPlayNextPos() {
        currentPlaying()?.let {
            val curPos = it.queuedItem.position
            val size = size()

            deleteAndRefresh(it.queuedItem)
            deleteTrack(it.track.internal)

            track(if (curPos == size - 1) curPos - 1 else curPos)?.let {
                play(it.queuedItem)
            }
        }
    }

    @Transaction
    suspend fun replaceQueue(new: List<QueueItem>) {
        clear()
        insertBlk(new)
    }

    @Transaction
    suspend fun deleteAndRefresh(item: QueueItem) {
        val size = size()
        delete(item)
        if (item.position < size - 1)
            refreshPositionsOnRemove(item.position)
    }

    @Transaction
    suspend fun deleteAllAndRefresh(items: List<QueueItem>) {
        // I sort the items by position (decrescent) so the refresh effects don't affect the other items to remove,
        // if that happens the items in the local list won't be removed because their position in the queue changed
        items.sortedBy { it.position }.reversed().forEach {
            deleteAndRefresh(it)
        }
    }

    @Transaction
    suspend fun moveTrack(from: Int, to: Int) {
        // Set the item to -1 because it could be the same track as the one in the position it's moving to (position, trackId are primary key)
        updatePos(from, -1)
        updatePos(to, from)
        updatePos(-1, to)
    }

}