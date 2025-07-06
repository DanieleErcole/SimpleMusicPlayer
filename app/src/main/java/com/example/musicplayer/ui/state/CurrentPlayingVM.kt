package com.example.musicplayer.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.QueuedTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CurrentPlayingVM(
    private val musicRepo: MusicRepository
) : ViewModel() {

    private val _curPlayingTrack = MutableStateFlow<QueuedTrack?>(null)
    val curTrack = _curPlayingTrack.asStateFlow()

    init {
        viewModelScope.launch {
            musicRepo.currentPlayingFlow().collect { t ->
                _curPlayingTrack.update { t }
            }
        }
        //TODO: play the track if there's one that must be played (and it's not already playing)
    }



}