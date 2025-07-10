package com.example.musicplayer.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicPlayerVM(private val userPrefs: UserPreferencesRepository) : ViewModel() {

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

    fun updateScannedDirs(list: List<String>) {
        viewModelScope.launch {
            userPrefs.updateScannedDirs(list)
        }
    }

    fun firstLaunched() {
        viewModelScope.launch {
            userPrefs.firstLaunched()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicPlayerApplication)
                MusicPlayerVM(userPrefs = application.userPreferencesRepository)
            }
        }
    }

}