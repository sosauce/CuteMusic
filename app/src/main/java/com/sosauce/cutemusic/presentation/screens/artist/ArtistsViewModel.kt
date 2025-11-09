package com.sosauce.cutemusic.presentation.screens.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.models.Artist
import com.sosauce.cutemusic.domain.repository.ArtistsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtistsViewModel(
    private val artistsRepository: ArtistsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ArtistsState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(artists = artistsRepository.fetchArtists()) }
            _state.update { it.copy(isLoading = false) }
        }
    }

}


data class ArtistsState(
    val isLoading: Boolean = false,
    val artists: List<Artist> = emptyList()
)