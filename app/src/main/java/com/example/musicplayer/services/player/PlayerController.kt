package com.example.musicplayer.services.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayer.data.Loop
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.PlayerStateRepository
import com.example.musicplayer.data.QueueItem
import com.example.musicplayer.data.TrackWithAlbum
import com.example.musicplayer.data.UserPreferencesRepository
import com.example.musicplayer.utils.toMediaItem
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.io.IOException

class PlayerController(
    private val musicRepo: MusicRepository,
    private val stateRepo: PlayerStateRepository,
    private val prefsRepo: UserPreferencesRepository
) : Player.Listener {

    private lateinit var controller: MediaController
    private val playerScope = CoroutineScope(Dispatchers.Main + Job())

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    @OptIn(UnstableApi::class)
    fun init(ctx: Context, token: SessionToken) {
        val future = MediaController.Builder(ctx, token)
            .buildAsync()
        future.addListener({
            if (future.isDone) {
                controller = future.get()
                controller.addListener(this)
                playerScope.launch {
                    val state = stateRepo.getPlayerState()
                    setVolume(state.volume)
                    setLoop(state.loopMode)

                    musicRepo.currentPlaying()?.let {
                        setPaused(!prefsRepo.isAutoPlay())
                        play()
                    }
                }
            }
        }, MoreExecutors.directExecutor())
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e(this::class.simpleName, "Playback error: $error")
        playerScope.launch {
            if (error.cause is IOException) {
                val path = musicRepo.currentPlaying()?.track?.internal?.location ?: "unknown"
                musicRepo.currentPlaying()?.let {
                    musicRepo.deleteAndPlayNextPos(it.queuedItem.position)
                }
                _errorFlow.emit("Failed to load the track at $path")
            } else {
                _errorFlow.emit("Unknown error: ${error.cause}")
                musicRepo.finishAndPlayNextPos(controller.currentMediaItemIndex)
            }
        }
    }

    suspend fun storeCurrentInfo() {
        musicRepo.storeCurrentPos(getCurrentPosition())
    }

    fun isReady(): Boolean = ::controller.isInitialized

    private fun play(trackPos: Int? = null, startingPos: Long = 0L) {
        trackPos?.let {
            controller.seekTo(it, startingPos)
        }
        controller.prepare()
    }

    suspend fun playTrack(pos: Int) {
        musicRepo.finishAndPlayNextPos(pos)
        setPaused(false)
        play(pos)
    }

    suspend fun queueAll(tracks: List<TrackWithAlbum>, mustPlay: Boolean = false) {
        if (tracks.isEmpty()) return

        val mutList = tracks.toMutableList()
        if (musicRepo.currentPlaying() == null || mustPlay) {
            val first = mutList.removeAt(0)
            val size = musicRepo.queueSize()
            musicRepo.queueAndPlay(
                QueueItem(
                    track = first.internal.trackId,
                    position = size,
                    isCurrent = true,
                    lastPosition = null
                )
            )

            controller.addMediaItem(first.toMediaItem())
            setPaused(false)
            play(if (mustPlay) size else null)
        }

        if (mutList.isNotEmpty()) {
            musicRepo.queueAll(mutList.mapIndexed { pos, item ->
                QueueItem(
                    track = item.internal.trackId,
                    position = musicRepo.queueSize() + pos,
                    isCurrent = false,
                    lastPosition = null
                )
            })
            controller.addMediaItems(mutList.map { it.toMediaItem() })
        }
    }

    suspend fun replaceQueue(new: List<TrackWithAlbum>, newCurrent: Long) {
        val items = new.mapIndexed { pos, item ->
            QueueItem(
                track = item.internal.trackId,
                position = pos,
                isCurrent = item.internal.trackId == newCurrent,
                lastPosition = null
            )
        }

        musicRepo.replaceQueue(items)

        val curPos = items.first { it.isCurrent }.position
        controller.setMediaItems(
            new.map { it.toMediaItem() },
            curPos,
            0L
        )
        setPaused(false)
        play(curPos)
    }

    suspend fun dequeue(items: List<QueueItem>) {
        items.forEach { controller.removeMediaItem(it.position) }
        musicRepo.dequeueAll(items)
    }

    suspend fun moveTrack(from: Int, to: Int) {
        Log.i(this::class.simpleName, "$from - $to")
        if (from == to || from < 0 || to < 0)
            return
        controller.moveMediaItem(from, to)
        musicRepo.moveTrack(from, to)
    }

    suspend fun clearQueue() {
        controller.apply {
            stop()
            clearMediaItems()
        }
        musicRepo.clearQueue()
    }

    fun skipNext() = if (controller.hasNextMediaItem()) {
        controller.seekToNextMediaItem()
        setPaused(false)
    } else controller.seekToDefaultPosition() // If end of queue replay the same track from the start

    fun skipPrev() = if (controller.hasPreviousMediaItem()) {
        controller.seekToPreviousMediaItem()
        setPaused(false)
    } else controller.seekToDefaultPosition() // If prev not present replay the same track from the start

    fun togglePauseResume() = if (controller.isPlaying) setPaused(true) else setPaused(false)

    fun setPaused(v: Boolean) = if (v) controller.pause() else controller.play()
    fun setVolume(v: Float) {
        controller.volume = v / 100f
    }

    suspend fun toggleShuffle() {
        withContext(Dispatchers.IO) {
            stateRepo.updateShuffle(!stateRepo.getPlayerState().shuffle)
        }
        controller.shuffleModeEnabled = !controller.shuffleModeEnabled
    }

    suspend fun setLoop(mode: Loop) {
        stateRepo.updateLoop(mode)
        controller.repeatMode = when (mode) {
            Loop.None -> Player.REPEAT_MODE_OFF
            Loop.Queue -> Player.REPEAT_MODE_ALL
            Loop.Track -> Player.REPEAT_MODE_ONE
        }
    }

    suspend fun getCurrentPosition() = if (isReady() && controller.currentPosition > 0)
        controller.currentPosition
    else musicRepo.currentPlaying()?.queuedItem?.lastPosition ?: 0L

    fun seekTo(position: Long) = controller.seekTo(position)

    fun seekTenSecs(rewind: Boolean) = if (rewind) controller.seekBack() else controller.seekForward()

    fun releasePlayer() = controller.release()

}