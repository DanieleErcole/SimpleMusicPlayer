package com.example.musicplayer.ui.state

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.data.UserPreferencesRepository
import com.example.musicplayer.services.MusicScanner
import com.example.musicplayer.services.player.PlayerController
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