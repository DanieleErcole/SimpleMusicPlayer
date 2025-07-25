package com.example.musicplayer.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.QueuedTrack
import com.example.musicplayer.services.player.PlayerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QueueVM(
    private val musicRepo: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {

    val queue = musicRepo.getQueueTracksFlow()
        .stateIn(
            initialValue = emptyList(),
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000)
        )

    suspend fun queue(): List<QueuedTrack> = musicRepo.getQueueTracks()

    //fun queue(): List<TrackWithAlbum> = playerController.getQueue()
    //fun currentIndex(): Int = playerController.getCurrentIndex()

    fun moveTrack(first: Int, second: Int) {
        viewModelScope.launch {
            playerController.moveTrack(first, second)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicPlayerApplication)
                QueueVM(
                    musicRepo = application.container.musicRepository,
                    playerController = application.playerController
                )
            }
        }
    }

}