package com.example.musicplayer.services.player

import android.os.PowerManager
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.session.MediaSession.Callback
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.musicplayer.data.LocalMusicRepository
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.PlayerStateRepository
import com.example.musicplayer.data.db.AppDatabase
import com.example.musicplayer.utils.dataStore
import com.example.musicplayer.utils.toMediaItem
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class PlayerService : MediaSessionService(), Callback, Player.Listener {

    private lateinit var session: MediaSession

    val musicRepo: MusicRepository by lazy {
        val db = AppDatabase.getDatabase(applicationContext)
        LocalMusicRepository(db.trackDao(), db.playlistDao(), db.albumDao(), db.queueDao())
    }
    val stateRepo: PlayerStateRepository by lazy {
        PlayerStateRepository(applicationContext.dataStore)
    }

    // The queue elements are present in both the db and the player, the player one even in shuffleMode still maintain the original queue positions
    // I maintain consistency by replacing/removing/swapping positions both in the db and in the player queue

    private val playerScope = CoroutineScope(Dispatchers.Main + Job())

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this)
            .setWakeMode(PowerManager.PARTIAL_WAKE_LOCK)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA)
                    .build(),
                true
            )
            .setSeekBackIncrementMs(10000L)
            .setSeekForwardIncrementMs(10000L)
            .build()

        // Save battery, offload the audio processing to the dedicated hardware if supported
        player.trackSelectionParameters =
            player.trackSelectionParameters
                .buildUpon()
                .setAudioOffloadPreferences(
                    TrackSelectionParameters.AudioOffloadPreferences.Builder()
                        .setAudioOffloadMode(TrackSelectionParameters.AudioOffloadPreferences.AUDIO_OFFLOAD_MODE_ENABLED)
                        .setIsGaplessSupportRequired(true)
                        .build()
                )
                .build()

        player.playWhenReady = false
        player.addListener(this)

        session = MediaSession.Builder(this, player)
            .setCallback(this)
            .build()
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        session.player.apply {
            playerScope.launch {
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
                    musicRepo.setPlay(currentMediaItemIndex)
                    Log.i(PlayerService::class.simpleName, "Playing $currentMediaItemIndex")
                } else if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO || reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
                    val currentIndex = currentMediaItemIndex
                    val isRepeatOff = repeatMode == Player.REPEAT_MODE_OFF

                    Log.i(PlayerService::class.simpleName, "Transitioning to $currentMediaItemIndex")
                    musicRepo.finishAndPlayNextPos(
                        currentIndex,
                        doNothingToCurrent = isRepeatOff // Do nothing to current if the player is not in repeat mode, the player simply pauses
                    )
                }
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        session.player.apply {
            if (playbackState == Player.STATE_ENDED && repeatMode == Player.REPEAT_MODE_OFF)
                playerScope.launch { stateRepo.updatePaused(true) }
        }
    }

    override fun onVolumeChanged(volume: Float) {
        playerScope.launch { stateRepo.updateVolume(volume * 100f) }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        playerScope.launch {
            if (isPlaying) {
                if (stateRepo.getPlayerState().paused)
                    stateRepo.updatePaused(false)
            } else if (session.player.playbackState == Player.STATE_READY) // Paused
                stateRepo.updatePaused(true)
        }
    }

    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        val settable = SettableFuture.create<MediaSession.MediaItemsWithStartPosition>()
        val defaultRes =
            MediaSession.MediaItemsWithStartPosition(
                emptyList(),
                0,
                0
            )

        playerScope.future {
            settable.set(musicRepo.getQueueTracks().let {
                if (it.isEmpty())
                    defaultRes
                else {
                    val cur = musicRepo.currentPlaying()
                    MediaSession.MediaItemsWithStartPosition(
                        it.map { it.track.toMediaItem() },
                        cur?.queuedItem?.position ?: 0,
                        cur?.queuedItem?.lastPosition ?: 0L
                    )
                }
            })
        }
        return settable
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(this::class.simpleName, "Shutting down, releasing player and session...")
        session.apply {
            player.release()
            release()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

}