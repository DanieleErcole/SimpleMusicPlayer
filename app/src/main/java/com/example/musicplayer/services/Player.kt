package com.example.musicplayer.services

import android.content.Context
import android.media.session.MediaSession
import android.os.PowerManager
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.data.Loop
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.PlayerStateRepository
import com.example.musicplayer.data.QueueItem
import com.example.musicplayer.data.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Player(
    private val musicRepo: MusicRepository,
    private val stateRepo: PlayerStateRepository
) {

    private lateinit var internal: ExoPlayer
    private lateinit var session: MediaSession

    private val playerScope = CoroutineScope(Dispatchers.Main + Job())

    fun init(ctx: Context) {
        internal = ExoPlayer.Builder(ctx)
            .setWakeMode(PowerManager.PARTIAL_WAKE_LOCK)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA)
                    .build(),
                true
            )
            .build()

        //TODO: on loop set None set pauseWhenFinished to true if queue not empty or if on last track, set false otherwise
        //TODO: if a track's file doesn't exits notify the user and delete it from the db
        internal.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        Log.i(this::class.simpleName, "Track started")
                    } else {
                        if (internal.playbackState == Player.STATE_ENDED) {
                            // Track ended, not resumed or stopped
                            Log.i(this::class.simpleName, "Track ended")
                            playerScope.launch {
                                // Finish track and play next song
                                val state = stateRepo.getPlayerState()
                                when (state.loopMode) {
                                    Loop.None -> musicRepo.finishAndPlayNext()?.let {
                                        play(it.track.internal)
                                        internal.play()
                                    } // Here there will be always at least a track, because if not, the player will simply pause the current one and won't release it
                                    Loop.Queue -> musicRepo.finishAndPlayNext()?.let { // play the next one regularly
                                        play(it.track.internal)
                                        internal.play()
                                    } ?: musicRepo.restartQueue()?.let { // get the first queue's track and play it
                                        play(it.track.internal)
                                        internal.play()
                                    }
                                    Loop.Track -> musicRepo.currentPlaying()?.let { // Replay the track that ended
                                        play(it.track.internal)
                                        internal.play()
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e(this::class.simpleName, "Playback error: $error")
                    //TODO: On error notify it and play the next one
                    // if the error is about the existence of the file remove it from the db entirely
                }
            }
        )
        internal.playWhenReady = true

        playerScope.launch {
            musicRepo.currentPlaying()?.let {
                setPaused(true)
                setVolume(stateRepo.getPlayerState().volume)
                internal.setMediaItem(MediaItem.fromUri(it.track.internal.location), it.queuedItem.lastPosition ?: 0L)
                internal.prepare()
            }
        }
    }

    suspend fun storeCurrentTrackInfo() {
        musicRepo.currentPlaying()?.let {
            musicRepo.storeCurrentPos(getCurrentPosition())
        }
    }

    fun isReady(): Boolean = ::internal.isInitialized

    private fun play(track: Track, replace: Boolean = false) {
        if (replace)
            internal.stop()
        internal.setMediaItem(MediaItem.fromUri(track.location))
        internal.prepare()
    }

    @OptIn(UnstableApi::class)
    suspend fun queue(track: Track) {
        val isFirst = musicRepo.currentPlaying() == null
        val queued = QueueItem(
            track = track.trackId,
            position = musicRepo.queueSize(),
            isCurrent = isFirst,
            lastPosition = null
        )
        if (isFirst) {
            musicRepo.queueAndPlay(queued)
            play(track)
        } else {
            musicRepo.queue(queued)
            if (internal.pauseAtEndOfMediaItems)
                internal.pauseAtEndOfMediaItems = false
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun queueAll(tracks: List<Track>, mustPlay: Boolean = false) {
        val mutList = tracks.toMutableList()
        if (musicRepo.currentPlaying() == null || mustPlay) {
            val first = mutList.removeAt(0)
            musicRepo.queueAndPlay(QueueItem(
                track = first.trackId,
                position = musicRepo.queueSize(),
                isCurrent = true,
                lastPosition = null
            ))

            play(first, replace = true)
        }

        if (tracks.isNotEmpty())
            musicRepo.queueAll(mutList.mapIndexed { pos, item ->
                QueueItem(
                    track = item.trackId,
                    position = musicRepo.queueSize() + pos,
                    isCurrent = false,
                    lastPosition = null
                )
            })

        if (internal.pauseAtEndOfMediaItems)
            internal.pauseAtEndOfMediaItems = false
    }

    suspend fun replaceQueue(new: List<Track>, newCurrent: Long) {
        musicRepo.replaceQueue(new.mapIndexed { pos, item ->
            QueueItem(
                track = item.trackId,
                position = pos,
                isCurrent = item.trackId == newCurrent,
                lastPosition = null
            )
        })
        play(new.find { it.trackId == newCurrent }!!)
    }

    suspend fun clearQueue() {
        internal.stop()
        musicRepo.clearQueue()
    }

    suspend fun skipNext() =
        musicRepo.finishAndPlayNext(replayCurrentIfNull = true)?.let {
            play(it.track.internal, replace = true)
        }

    suspend fun skipPrev() =
        musicRepo.finishAndPlayPrev()?.let {
            play(it.track.internal, replace = true)
        }

    suspend fun togglePauseResume() {
        if (internal.isPlaying)
            setPaused(true)
        else setPaused(false)
    }

    suspend fun setPaused(v: Boolean) {
        if (v) internal.pause()
        else internal.play()
        stateRepo.updatePaused(v)
    }

    suspend fun setVolume(v: Float) {
        internal.volume = v / 100f
        stateRepo.updateVolume(v)
    }

    @OptIn(UnstableApi::class)
    suspend fun setLoop(mode: Loop) {
        when (mode) {
            Loop.None -> {
                val size = musicRepo.queueSize()
                internal.pauseAtEndOfMediaItems = size == 0
                        || musicRepo.currentPlaying()?.let { it.queuedItem.position == size - 1 } ?: false
            }
            Loop.Queue, Loop.Track -> internal.pauseAtEndOfMediaItems = false
        }
        stateRepo.updateLoop(mode)
    }

    fun getCurrentPosition() = if (isReady()) internal.currentPosition else 0L

    fun seekTo(position: Long) = internal.seekTo(position)

    fun seekTenSecs(rewind: Boolean) = internal
        .seekTo(internal.currentPosition + if (rewind) -10000 else 10000)

    fun releasePlayer() = internal.release()

}