package com.example.musicplayer.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.PlayerState
import com.example.musicplayer.data.PlayerStateRepository
import com.example.musicplayer.data.QueuedTrack
import com.example.musicplayer.services.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CurrentPlayingVM(
    private val musicRepo: MusicRepository,
    private val plStateRepo: PlayerStateRepository,
    private val player: Player
) : ViewModel() {

    val curTrack: StateFlow<QueuedTrack?> = musicRepo.currentPlayingFlow()
        .stateIn(
            initialValue = null,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
    val position: StateFlow<Long> = flow {
        while (true) {
            if (player.isReady())
                emit(player.getCurrentPosition())
            delay(1000)
        }
    }.stateIn(
        initialValue = 0L,
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )
    val playerState = plStateRepo.playerState
        .stateIn(
            initialValue = PlayerState.default(),
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    val sliderValue: StateFlow<Float> = position
        .combine(curTrack) { pos, track ->
            track?.let {
                ((pos * 100) / it.track.internal.durationMs).toFloat()
            } ?: 0f
        }.stateIn(
            initialValue = 0f,
            scope = viewModelScope,
            started = SharingStarted.Eagerly
        )

    fun togglePauseResume() {
        viewModelScope.launch {
            player.togglePauseResume()
        }
    }

    fun setVolume(v: Float) {
        viewModelScope.launch {
            player.setVolume(v)
        }
    }

    fun seekTo(pos: Long) = player.seekTo(pos)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicPlayerApplication)
                val repo = application.container.musicRepository
                CurrentPlayingVM(musicRepo = repo, plStateRepo = application.playerStateRepository, player = application.player)
            }
        }
    }

}