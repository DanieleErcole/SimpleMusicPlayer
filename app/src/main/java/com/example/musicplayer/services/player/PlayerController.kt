package com.example.musicplayer.services.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayer.data.Loop
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.PlayerStateRepository
import com.example.musicplayer.data.QueueItem
import com.example.musicplayer.data.Track
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PlayerController(
    private val musicRepo: MusicRepository,
    private val stateRepo: PlayerStateRepository
) {

    private lateinit var controller: MediaController
    private val playerScope = CoroutineScope(Dispatchers.Main + Job())

    //TODO: detect when the queue has finished playing (it must work even in shuffle mode)

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    @OptIn(UnstableApi::class)
    fun init(ctx: Context, token: SessionToken) {
        val future = MediaController.Builder(ctx, token)
            .buildAsync()
        future.addListener({
            if (future.isDone) {
                controller = future.get()
                controller.addListener(
                    object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            playerScope.launch {
                                if (isPlaying) {
                                    Log.i(this::class.simpleName, "Track started")
                                } else {
                                    //if (controller.playbackState == Player.STATE_ENDED) // Track ended, not resumed or stopped
                                        //onTrackFinished()
                                }
                            }
                        }

                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            playerScope.launch {
                                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO || reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK)
                                    musicRepo.finishAndPlayNextPos(
                                        controller.currentMediaItemIndex,
                                        doNothingToCurrent = controller.repeatMode == Player.REPEAT_MODE_OFF
                                    )
                            }
                        }

                        override fun onPlayerError(error: PlaybackException) {
                            Log.e(this::class.simpleName, "Playback error: $error")
                            //TODO: remove this from the queue?
                            playerScope.launch {
                                /*if (error.cause is IOException)
                                    musicRepo.currentPlaying()?.let {
                                        musicRepo.deleteTrack(it.track.internal)
                                    }*/
                                val path = musicRepo.currentPlaying()?.track?.internal?.location ?: "unknown"
                                _errorFlow.emit("Failed to load the track at $path")
                                /*musicRepo.finishAndPlayNext()?.let {
                                    play(it.queuedItem)
                                    controller.play()
                                }*/
                            }
                        }
                    }
                )
                playerScope.launch {
                    musicRepo.currentPlaying()?.let {
                        setVolume(stateRepo.getPlayerState().volume)
                        play()
                    }
                }
            }
        }, MoreExecutors.directExecutor())
    }

    suspend fun storeCurrentInfo() {
        stateRepo.updatePaused(true)
        musicRepo.currentPlaying()?.let {
            musicRepo.storeCurrentPos(getCurrentPosition())
        }
    }

    fun isReady(): Boolean = ::controller.isInitialized

    private fun play(trackPos: Int? = null, startingPos: Long = 0L) {
        trackPos?.let {
            controller.seekTo(it, startingPos)
        }
        controller.prepare()
    }

    suspend fun queueAll(tracks: List<Track>, mustPlay: Boolean = false) {
        val mutList = tracks.toMutableList()
        if (musicRepo.currentPlaying() == null || mustPlay) {
            val first = mutList.removeAt(0)
            val size = musicRepo.queueSize()
            musicRepo.queueAndPlay(
                QueueItem(
                    track = first.trackId,
                    position = size,
                    isCurrent = true,
                    lastPosition = null
                )
            )

            //play(first, replace = true)
            controller.addMediaItem(MediaItem.fromUri(first.location))
            play(if (mustPlay) size else null)
        }

        if (mutList.isNotEmpty()) {
            musicRepo.queueAll(mutList.mapIndexed { pos, item ->
                QueueItem(
                    track = item.trackId,
                    position = musicRepo.queueSize() + pos,
                    isCurrent = false,
                    lastPosition = null
                )
            })
            controller.addMediaItems(mutList.map { MediaItem.fromUri(it.location) })
        }
    }

    suspend fun replaceQueue(new: List<Track>, newCurrent: Long) {
        val items = new.mapIndexed { pos, item ->
            QueueItem(
                track = item.trackId,
                position = pos,
                isCurrent = item.trackId == newCurrent,
                lastPosition = null
            )
        }

        musicRepo.replaceQueue(items)

        val curPos = items.first { it.isCurrent }.position
        controller.setMediaItems(
            new.map { MediaItem.fromUri(it.location) },
            curPos,
            0L
        )
        play(curPos)
    }

    suspend fun clearQueue() {
        controller.stop()
        musicRepo.clearQueue()
    }

    fun skipNext() = if (controller.hasNextMediaItem())
        controller.seekToNextMediaItem()
    else controller.seekToDefaultPosition() // If end of queue replay the same track from the start

    fun skipPrev() = if (controller.hasPreviousMediaItem())
        controller.seekToPreviousMediaItem()
    else controller.seekToDefaultPosition() // If prev not present replay the same track from the start

    fun togglePauseResume() = if (controller.isPlaying) setPaused(true) else setPaused(false)

    fun setPaused(v: Boolean) = if (v) controller.pause() else controller.play()
    fun setVolume(v: Float) {
        controller.volume = v / 100f
    }

    suspend fun toggleShuffle() {
        stateRepo.updateShuffle(!stateRepo.getPlayerState().shuffle)
        controller.shuffleModeEnabled = !controller.shuffleModeEnabled
    }

    suspend fun setLoop(mode: Loop) {
        stateRepo.updateLoop(mode)
        controller.repeatMode = when (mode) {
            Loop.None -> Player.REPEAT_MODE_OFF
            Loop.Queue -> Player.REPEAT_MODE_ONE
            Loop.Track -> Player.REPEAT_MODE_ALL
        }
    }

    suspend fun getCurrentPosition() = if (isReady())
        controller.currentPosition
    else musicRepo.currentPlaying()?.queuedItem?.lastPosition ?: 0L

    fun seekTo(position: Long) = controller.seekTo(position)

    fun seekTenSecs(rewind: Boolean) = if (rewind) controller.seekBack() else controller.seekForward()

    fun releasePlayer() = controller.release()

}