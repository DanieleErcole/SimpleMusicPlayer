package it.danieleercole.soundbox.ui.state

import androidx.compose.foundation.gestures.ScrollableState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class ScrollableViewModel<out T : ScrollableState>(t : T) : ViewModel() {
    private val _scrollState = MutableStateFlow(t)
    val scrollState = _scrollState.asStateFlow()
}