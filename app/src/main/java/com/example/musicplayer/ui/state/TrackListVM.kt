package com.example.musicplayer.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.TrackWithAlbum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class TrackListVM(trackSource: Flow<List<TrackWithAlbum>>) : ViewModel() {

    private val _searchString = MutableStateFlow("")
    val searchString = _searchString.asStateFlow()

    private val _selectedTracks = MutableStateFlow<List<Long>>(emptyList())
    val selectedTracks = _selectedTracks.asStateFlow()

    val tracks = combine(trackSource, _searchString) { tracks, search ->
        tracks.filter { track ->
            search.isEmpty()
                    || track.internal.title.contains(search, ignoreCase = true)
                    || track.album.name.contains(search, ignoreCase = true)
        }
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

}