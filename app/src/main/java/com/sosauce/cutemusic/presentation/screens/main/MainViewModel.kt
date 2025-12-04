package com.sosauce.cutemusic.presentation.screens.main

import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.AbstractTracksScanner
import com.sosauce.cutemusic.data.models.CuteTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val abstractTracksScanner: AbstractTracksScanner
) : ViewModel() {

    private val _state = MutableStateFlow(MainState(isLoading = true))
    val state = _state.asStateFlow()


    init {
        viewModelScope.launch {
            abstractTracksScanner.fetchLatestTracks(null, null)
                .collectLatest { tracks ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            tracks = tracks
                        )
                    }
            }
        }
    }
}


data class MainState(
    val isLoading: Boolean = false,
    val tracks: List<CuteTrack> = emptyList()
)