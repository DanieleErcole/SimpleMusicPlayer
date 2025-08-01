package com.example.musicplayer.ui.state

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.data.UserPreferencesRepository
import com.example.musicplayer.services.MusicScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsVM(
    private val prefsRepo: UserPreferencesRepository,
    private val scanner: MusicScanner
) : ViewModel() {

    val autoScan = prefsRepo.autoScan
        .stateIn(
            initialValue = true,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun rescan(ctx: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                scanner.scanDirectories(ctx)
            }
        }
    }

    fun toggleAutoScan() {
        viewModelScope.launch {
            prefsRepo.updateAutoScan(!autoScan.value)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicPlayerApplication)
                SettingsVM(
                    prefsRepo = application.userPreferencesRepository,
                    scanner = application.scanner
                )
            }
        }
    }

}