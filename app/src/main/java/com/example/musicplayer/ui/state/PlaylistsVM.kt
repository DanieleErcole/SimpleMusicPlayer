package com.example.musicplayer.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class PlaylistsVM(private val musicRepo: MusicRepository) : ViewModel() {

    val playlists: StateFlow<List<Playlist>> = musicRepo.getAllPlaylists()
        .stateIn(
            initialValue = emptyList(),
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    private val _openAddDialog = MutableStateFlow(false)
    val openAddDialog = _openAddDialog.asStateFlow()

    private val _openNewDialog = MutableStateFlow(false)
    val openNewDialog = _openNewDialog.asStateFlow()

    fun toggleAddDialog() = _openAddDialog.update { !_openAddDialog.value }

    fun toggleNewDialog() = _openNewDialog.update { !_openNewDialog.value }

    fun newPlaylist(name: String) {
        viewModelScope.launch {
            musicRepo.newPlaylist(
                Playlist(
                    name = name,
                    created = ZonedDateTime.now()
                )
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicPlayerApplication)
                PlaylistsVM(musicRepo = application.container.musicRepository)
            }
        }
    }

}