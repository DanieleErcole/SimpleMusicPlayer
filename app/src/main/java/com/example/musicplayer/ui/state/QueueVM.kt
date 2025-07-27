package com.example.musicplayer.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.QueueItem
import com.example.musicplayer.data.QueuedTrack
import com.example.musicplayer.data.TrackWithAlbum
import com.example.musicplayer.services.player.PlayerController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QueueVM(
    private val musicRepo: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {

    private val _update = MutableStateFlow(false)
    val update = _update.asStateFlow()

    private val _selectedTracks = MutableStateFlow<Set<Int>>(emptySet())
    val selectedTracks = _selectedTracks.asStateFlow()

    suspend fun queue(): List<QueuedTrack> = musicRepo.getQueueTracks()

    // I do this because I have to create a copy of the queue to reorder without depending on db flow changes that triggers recomposition,
    // I update the queue when out of the screen
    fun updateUIQueue() = _update.update { !_update.value }

    fun moveTrack(from: Int, to: Int) {
        viewModelScope.launch {
            playerController.moveTrack(from, to)
        }
    }

    fun clearQueue() {
        viewModelScope.launch {
            playerController.clearQueue()
        }
        updateUIQueue()
    }

    fun selectTrack(item: ReorderableQueueItem) =
        _selectedTracks.update {
            if (it.contains(item.position))
                it - item.position
            else it + item.position
        }

    fun clearSelection() = _selectedTracks.update { emptySet() }
    fun selectList(tracks: Set<Int>) = _selectedTracks.update { tracks }

    fun playTrack(pos: Int) {
        viewModelScope.launch {
            playerController.playTrack(pos)
        }
        updateUIQueue()
    }

    fun queueAll(tracks: List<TrackWithAlbum>, mustPlay: Boolean = false) {
        viewModelScope.launch {
            playerController.queueAll(tracks, mustPlay = mustPlay)
        }
        updateUIQueue()
    }

    fun dequeueAll(items: List<QueuedTrack>) {
        viewModelScope.launch {
            playerController.dequeue(items.map { it.queuedItem })
        }
        updateUIQueue()
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