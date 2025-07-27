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

data class ConfirmDialogState(
    val action: () -> Unit,
    val title: String,
    val text: String? = null
)

data class AddDialogState(
    val endAction: (() -> Unit)? = null,
    val tracks: List<TrackWithAlbum>
)

class DialogsVM : ViewModel() {

    private val _confirmDialog = MutableStateFlow<ConfirmDialogState?>(null)
    val confirmDialog = _confirmDialog.asStateFlow()

    private val _infoTrack = MutableStateFlow<TrackWithAlbum?>(null)
    val infoTrack = _infoTrack.asStateFlow()

    private val _addDialog = MutableStateFlow<AddDialogState?>(null)
    val addDialog = _addDialog.asStateFlow()

    private val _openNewDialog = MutableStateFlow(false)
    val openNewDialog = _openNewDialog.asStateFlow()

    fun setConfirmDialog(title: String? = null, text: String? = null, action: (() -> Unit)? = null) =
        _confirmDialog.update {
            if (action != null && title != null)
                ConfirmDialogState(action, title, text)
            else null
        }

    fun setInfoDialog(track: TrackWithAlbum? = null) = _infoTrack.update { track }

    fun setAddDialog(tracks: List<TrackWithAlbum>? = null, onEndAction: (() -> Unit)? = null) =
        _addDialog.update {
            tracks?.let {
                AddDialogState(onEndAction, it)
            }
        }

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