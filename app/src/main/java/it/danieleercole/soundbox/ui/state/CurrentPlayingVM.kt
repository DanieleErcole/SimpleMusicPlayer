package it.danieleercole.soundbox.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import it.danieleercole.soundbox.MusicPlayerApplication
import it.danieleercole.soundbox.data.Loop
import it.danieleercole.soundbox.data.MusicRepository
import it.danieleercole.soundbox.data.PlayerStateRepository
import it.danieleercole.soundbox.data.QueuedTrack
import it.danieleercole.soundbox.services.player.PlayerController
import it.danieleercole.soundbox.utils.DEFAULT_VOLUME
import it.danieleercole.soundbox.utils.floatPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CurrentPlayingVM(
    private val musicRepo: MusicRepository,
    private val plStateRepo: PlayerStateRepository,
    private val playerController: PlayerController
) : ViewModel() {

    val curTrack: StateFlow<QueuedTrack?> = musicRepo.currentPlayingFlow()
        .stateIn(
            initialValue = null,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
    val position: StateFlow<Long> = flow {
        while (true) {
            emit(playerController.getCurrentPosition())
            delay(1000)
        }
    }.flowOn(Dispatchers.Main).stateIn(
        initialValue = 0L,
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    val volume = plStateRepo.volume
        .stateIn(
            initialValue = DEFAULT_VOLUME,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
    val paused = plStateRepo.paused
        .stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
    val shuffle = plStateRepo.shuffle
        .stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
    val loop = plStateRepo.loop
        .stateIn(
            initialValue = Loop.None,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    val sliderValue: StateFlow<Float> = position
        .combine(curTrack) { pos, track ->
            track?.let { t ->
                floatPosition(pos, t.track.internal.durationMs)
            } ?: 0f
        }.stateIn(
            initialValue = 0f,
            scope = viewModelScope,
            started = SharingStarted.Eagerly
        )

    fun togglePauseResume() = playerController.togglePauseResume()

    fun toggleShuffleMode() {
        viewModelScope.launch {
            playerController.toggleShuffle()
        }
    }

    fun skipNext() = playerController.skipNext()
    fun skipPrev() = playerController.skipPrev()
    fun setVolume(v: Float) = playerController.setVolume(v)

    fun setLoopMode(mode: Loop) {
        viewModelScope.launch {
            playerController.setLoop(mode)
        }
    }

    fun seekTo(pos: Long) = playerController.seekTo(pos)

    fun seekForward() = playerController.seekTenSecs(false)

    fun seekRewind() = playerController.seekTenSecs(true)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicPlayerApplication)
                val repo = application.container.musicRepository
                CurrentPlayingVM(musicRepo = repo, plStateRepo = application.playerStateRepository, playerController = application.playerController)
            }
        }
    }

}