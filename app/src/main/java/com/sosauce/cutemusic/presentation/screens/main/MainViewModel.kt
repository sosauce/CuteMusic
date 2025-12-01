package com.sosauce.cutemusic.presentation.screens.main

import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.models.toCuteTrack
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val mediaStoreHelper: MediaStoreHelper
) : ViewModel() {

    private val _state = MutableStateFlow(MainState(isLoading = true))
    val state = _state.asStateFlow()


    init {
        viewModelScope.launch {
            mediaStoreHelper.fetchLatestMusics()
                .collectLatest { tracks ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            tracks = tracks.fastMap { track -> track.toCuteTrack() }
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