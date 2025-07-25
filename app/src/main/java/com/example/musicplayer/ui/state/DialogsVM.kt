package com.example.musicplayer.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.data.TrackWithAlbum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DialogsVM : ViewModel() {

    private val _infoTrack = MutableStateFlow<TrackWithAlbum?>(null)
    val infoTrack = _infoTrack.asStateFlow()

    private val _addTracks = MutableStateFlow<List<TrackWithAlbum>>(emptyList())
    val addTracks = _addTracks.asStateFlow()

    private val _openNewDialog = MutableStateFlow(false)
    val openNewDialog = _openNewDialog.asStateFlow()

    fun toggleInfoDialog(track: TrackWithAlbum? = null) = _infoTrack.update { track }
    fun toggleAddDialog(tracks: List<TrackWithAlbum>? = null) = _addTracks.update { tracks ?: emptyList() }
    fun toggleNewDialog() = _openNewDialog.update { !_openNewDialog.value }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicPlayerApplication)
                DialogsVM()
            }
        }
    }

}