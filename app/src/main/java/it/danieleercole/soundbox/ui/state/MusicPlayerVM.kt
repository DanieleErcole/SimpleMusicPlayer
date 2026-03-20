package it.danieleercole.soundbox.ui.state

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import it.danieleercole.soundbox.MusicPlayerApplication
import it.danieleercole.soundbox.data.UserPreferencesRepository
import it.danieleercole.soundbox.services.MusicScanner
import it.danieleercole.soundbox.services.player.PlayerController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MusicPlayerVM(
    private val userPrefs: UserPreferencesRepository,
    private val playerController: PlayerController,
    private val scanner: MusicScanner
) : ViewModel() {

    val snackBarState = SnackbarHostState()
    val errors = playerController.errorFlow

    suspend fun canAutoScan() = userPrefs.autoScan.first()

    fun storeCurrentTrackInfo() {
        viewModelScope.launch {
            playerController.storeCurrentInfo()
        }
    }

    fun releaseRes() {
        playerController.releasePlayer()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicPlayerApplication)
                MusicPlayerVM(
                    userPrefs = application.userPreferencesRepository,
                    playerController = application.playerController,
                    scanner = application.scanner
                )
            }
        }
    }

}