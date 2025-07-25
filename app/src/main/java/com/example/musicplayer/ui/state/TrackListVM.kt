package com.example.musicplayer.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.Track
import com.example.musicplayer.data.TrackFilter
import com.example.musicplayer.data.TrackWithAlbum
import com.example.musicplayer.services.player.PlayerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackListVM(
    private val trackSource: TrackFilter,
    private val playerController: PlayerController
) : ViewModel() {

    private val _searchString = MutableStateFlow("")
    val searchString = _searchString.asStateFlow()

    private val _selectedTracks = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTracks = _selectedTracks.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tracks = _searchString.flatMapLatest {
        withContext(Dispatchers.IO) {
            trackSource.collectTracks(it)
        }
    }.stateIn(
        initialValue = emptyList(),
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun updateSearchString(str: String) = _searchString.update { str }

    fun selectTrack(id: Long) =
        _selectedTracks.update {
            if (it.contains(id))
                it - id
            else it + id
        }

    fun clearSelection() = _selectedTracks.update { emptySet() }
    fun selectList(tracks: Set<Long>) = _selectedTracks.update { tracks }

    fun replaceQueue(tracks: List<TrackWithAlbum>, currentId: Long) {
        viewModelScope.launch {
            playerController.replaceQueue(tracks, currentId)
        }
    }

    fun queueAll(tracks: List<TrackWithAlbum>, mustPlay: Boolean = false) {
        viewModelScope.launch {
            playerController.queueAll(tracks, mustPlay = mustPlay)
        }
    }

}