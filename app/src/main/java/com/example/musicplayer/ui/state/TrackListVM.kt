package com.example.musicplayer.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.Track
import com.example.musicplayer.data.TrackFilter
import com.example.musicplayer.services.Player
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackListVM(
    private val trackSource: TrackFilter,
    private val player: Player
) : ViewModel() {

    private val _searchString = MutableStateFlow("")
    val searchString = _searchString.asStateFlow()

    private val _selectedTracks = MutableStateFlow<List<Long>>(emptyList())
    val selectedTracks = _selectedTracks.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tracks = _searchString.flatMapLatest {
        trackSource.collectTracks(it)
    }.stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun updateSearchString(str: String) = _searchString.update { str }

    fun selectTrack(id: Long) =
        _selectedTracks.update {
            if (it.contains(id))
                it - id
            else it + id
        }

    fun clearSelection() = _selectedTracks.update { emptyList() }
    fun selectList(tracks: List<Long>) = _selectedTracks.update { tracks }

    fun replaceQueue(tracks: List<Track>, currentId: Long) {
        viewModelScope.launch {
            player.replaceQueue(tracks, currentId)
        }
    }

    fun queue(track: Track) {
        viewModelScope.launch {
            player.queue(track)
        }
    }

    fun queueAll(tracks: List<Track>, mustPlay: Boolean = false) {
        viewModelScope.launch {
            player.queueAll(tracks, mustPlay = mustPlay)
        }
    }

}