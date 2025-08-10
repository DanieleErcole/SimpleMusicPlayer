package com.example.musicplayer.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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

data class RenameDialogState(
    val playlist: Long,
    val endAction: (String) -> Unit
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

    private val _renameDialog = MutableStateFlow<RenameDialogState?>(null)
    val renameDialog = _renameDialog.asStateFlow()

    private val _permDialog = MutableStateFlow(false)
    val permDialog = _permDialog.asStateFlow()

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

    fun setNewDialog() = _openNewDialog.update { !_openNewDialog.value }
    fun setRenameDialog(playlist: Long? = null, action: ((String) -> Unit)? = null) =
        _renameDialog.update {
            if (playlist != null && action != null)
                RenameDialogState(playlist, action)
            else null
        }
    fun togglePermDialog() = _permDialog.update { !_permDialog.value }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { DialogsVM() }
        }
    }

}