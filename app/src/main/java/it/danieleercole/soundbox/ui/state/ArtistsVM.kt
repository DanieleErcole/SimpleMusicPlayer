package it.danieleercole.soundbox.ui.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import it.danieleercole.soundbox.MusicPlayerApplication
import it.danieleercole.soundbox.data.MusicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ArtistsVM(
    private val musicRepo: MusicRepository
) : ScrollableViewModel<LazyListState>(LazyListState()) {

    private val _searchString = MutableStateFlow("")
    val searchString = _searchString.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val artists = searchString.flatMapLatest {
        musicRepo.getAllArtists(it)
    }.stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun updateSearchString(str: String) = _searchString.update { str }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicPlayerApplication)
                ArtistsVM(musicRepo = application.container.musicRepository)
            }
        }
    }

}