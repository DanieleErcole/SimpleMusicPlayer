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

    @Query("SELECT COUNT(position) FROM queue")
    suspend fun size(): Int
    @Query("SELECT * FROM queue ORDER BY position ASC")
    suspend fun getQueueTracks(): List<QueuedTrack>
    @Query("SELECT * FROM queue ORDER BY position ASC")
    fun getQueueTracksFlow(): Flow<List<QueuedTrack>>
    @Query("SELECT * FROM queue WHERE isCurrent = 1")
    suspend fun currentPlaying(): QueuedTrack?
    @Query("SELECT * FROM queue WHERE isCurrent = 1")
    fun currentPlayingFlow(): Flow<QueuedTrack?>
    @Query("UPDATE queue SET position = :removedPos - 1 WHERE position > :removedPos")
    suspend fun refreshPositions(removedPos: Int)

    @Query("SELECT * FROM queue WHERE position > :pos ORDER BY position ASC LIMIT 1")
    suspend fun nextSong(pos: Int): QueuedTrack?
    @Query("SELECT * FROM queue WHERE position < :pos ORDER BY position DESC LIMIT 1")
    suspend fun prevSong(pos: Int): QueuedTrack?
    @Query("SELECT * FROM queue WHERE position = 0 LIMIT 1")
    suspend fun first(): QueuedTrack?
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

    @Transaction
    suspend fun play(track: QueueItem) {
        playTrack(track.track, Instant.now().toEpochMilli())
        playQueued(track.track, track.position)
    }

    @Transaction
    suspend fun queueAndPlay(item: QueueItem) {
        insert(item)
        if (size() > 0) // If I call this and something is playing I play it instantly without replacing it
            finish()
        play(item)
    }

    @Transaction
    suspend fun finishAndPlayNext(replayCurrentIfNull: Boolean): QueuedTrack? =
        currentPlaying()?.let {
            finish()
            nextSong(it.queuedItem.position)?.let {
                play(it.queuedItem)
                it
            } ?: run {
                if (replayCurrentIfNull) { // If there's only one track play the same one
                    play(it.queuedItem)
                    it
                } else null
            }
        }

    @Transaction
    suspend fun finishAndPlayPrev(): QueuedTrack? =
        currentPlaying()?.let {
            finish()
            prevSong(it.queuedItem.position)?.let {
                play(it.queuedItem)
                it
            } ?: run {
                // If there's only one track play the same one
                play(it.queuedItem)
                it
            }
        }

    @Transaction
    suspend fun replaceQueue(new: List<QueueItem>) {
        clear()
        insertBlk(new)
        currentPlaying()?.let {
            play(it.queuedItem)
        }
    }

    @Transaction
    suspend fun deleteAndRefresh(item: QueueItem) {
        delete(item)
        if (item.position < size() - 1)
            refreshPositions(item.position)
    }

    @Transaction
    suspend fun restartQueue(): QueuedTrack? =
        first()?.let {
            play(it.queuedItem)
            it
        }


}