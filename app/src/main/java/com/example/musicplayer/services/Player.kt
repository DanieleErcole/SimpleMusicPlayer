package com.example.musicplayer.services

import android.content.Context
import android.media.session.MediaSession
import android.os.PowerManager
import android.util.Log
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
import java.time.Instant

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

        //TODO: on loop set None set pauseWhenFinished to true if queue not empty, set false otherwise
        //TODO: if a track's file doesn't exits notify the user and delete it from the db
        internal.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        Log.i(this::class.simpleName, "Track started")
                    } else {
                        // Not playing because playback is paused, ended, suppressed, or the player
                        // is buffering, stopped or failed. Check player.playWhenReady,
                        // player.playbackState, player.playbackSuppressionReason and
                        // player.playerError for details.
                        // Ended, I must play the next one or do nothing if there's no more tracks in the queue (check loop state)
                        if (internal.playbackState == Player.STATE_ENDED) {
                            Log.i(this::class.simpleName, "Track ended")
                            playerScope.launch {
                                // Finish track and play next song
                                val next = musicRepo.finishAndPlayNext()
                                val state = stateRepo.getPlayerState()
                                when (state.loopMode) {
                                    Loop.None -> {TODO()}
                                    Loop.Queue -> {TODO()}
                                    Loop.Track -> {TODO()}
                                }
                            }
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e(this::class.simpleName, "Playback error: $error")
                }
            }
        )
        internal.playWhenReady = true

        playerScope.launch {
            musicRepo.currentPlaying()?.let {
                internal.pause()
                internal.setMediaItem(MediaItem.fromUri(it.track.internal.location), it.queuedItem.lastPosition ?: 0L)
                internal.prepare()
                //TODO: decide how to implement loop
            }
        }
    }

    suspend fun storeCurrentTrackInfo() {
        musicRepo.currentPlaying()?.let {
            musicRepo.storeCurrentPos(it.track.internal.trackId, getCurrentPosition())
        }
    }

    fun isReady(): Boolean = ::internal.isInitialized

    private fun play(track: Track, replace: Boolean = false) {
        if (replace)
            internal.stop()
        internal.setMediaItem(MediaItem.fromUri(track.location))
        internal.prepare()
    }

    suspend fun queue(track: Track, replace: Boolean = false) {
        val queue = musicRepo.getQueueTracks()
        if (queue.find { it.queuedItem.track == track.trackId } != null)
            return

        //TODO: implement transactions everywhere, it's necessary to reduce lag
        val isFirst = musicRepo.currentPlaying() == null
        val queued = QueueItem(
            track = track.trackId,
            added = Instant.now(),
            isCurrent = isFirst,
            lastPosition = null
        )
        if (replace && !isFirst)
            musicRepo.replaceCurrent(queued)
        else if (isFirst)
            musicRepo.queueAndPlay(queued)
        else musicRepo.queue(queued)

        if (isFirst || replace)
            play(track, replace)
    }

    suspend fun queueAll(tracks: List<Track>) = tracks.forEach { queue(it) }

    suspend fun clearQueue() = musicRepo.clearQueue()

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

    suspend fun setVolume(v: Float) = stateRepo.updateVolume(v)

    @UnstableApi
    suspend fun setLoop(mode: Loop) {
        when (mode) {
            Loop.None -> internal.pauseAtEndOfMediaItems = musicRepo.getQueueTracks().isNotEmpty()
            Loop.Queue, Loop.Track -> internal.pauseAtEndOfMediaItems = false
        }
        stateRepo.updateLoop(mode)
    }

    fun getCurrentPosition() = if (::internal.isInitialized) internal.currentPosition else 0L

    fun seekTo(position: Long) = internal.seekTo(position)

}