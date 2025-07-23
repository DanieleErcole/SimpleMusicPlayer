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
import com.example.musicplayer.services.player.PlayerController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicPlayerVM(
    private val userPrefs: UserPreferencesRepository,
    private val playerController: PlayerController
) : ViewModel() {

    val snackBarState = SnackbarHostState()
    val firstLaunch: StateFlow<Boolean> = userPrefs.firstLaunch
        .stateIn(
            initialValue = true,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
    val scannedDirectories: StateFlow<List<String>> = userPrefs.scannedDirectories
        .stateIn(
            initialValue = emptyList(),
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
    val errors = playerController.errorFlow

    fun firstLaunched() {
        viewModelScope.launch {
            userPrefs.firstLaunched()
        }
    }

    fun updateScannedDirs(list: List<String>) {
        viewModelScope.launch {
            userPrefs.updateScannedDirs(list)
        }
    }

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
                    playerController = application.playerController
                )
            }
        }
    }

}