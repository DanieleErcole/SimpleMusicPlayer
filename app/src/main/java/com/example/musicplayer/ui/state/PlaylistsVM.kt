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
import com.example.musicplayer.data.TrackWithAlbum
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

class PlaylistsVM(private val musicRepo: MusicRepository) : ViewModel() {

    private val _searchString = MutableStateFlow("")
    val searchString = _searchString.asStateFlow()

    val allPlaylists = musicRepo.getAllPlaylists()
        .stateIn(
            initialValue = emptyList(),
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredPlaylist = _searchString.flatMapLatest {
        musicRepo.getAllPlaylistsFiltered(it)
    }.stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun updateSearchString(str: String) = _searchString.update { str }

    fun newPlaylist(name: String) {
        viewModelScope.launch {
            musicRepo.newPlaylist(
                Playlist(
                    name = name,
                    created = Instant.now()
                )
            )
        }
    }

    fun addToPlaylist(tracks: List<TrackWithAlbum>, playlistId: Long) {
        viewModelScope.launch {
            musicRepo.addToPlaylist(
                tracks = tracks.map { it.internal.trackId },
                playlist = playlistId
            )
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            musicRepo.deletePlaylist(playlist)
        }
    }

    fun renamePlaylist(playlist: Long, newName: String) {
        viewModelScope.launch {
            musicRepo.renamePlaylist(playlist, newName)
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