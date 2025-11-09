package com.sosauce.cutemusic.presentation.screens.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.models.Album
import com.sosauce.cutemusic.domain.repository.AlbumsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumsViewModel(
    private val albumsRepository: AlbumsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AlbumsState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(albums = albumsRepository.fetchAlbums()) }
            _state.update { it.copy(isLoading = false) }
        }
    }


}


data class AlbumsState(
    val isLoading: Boolean = false,
    val albums: List<Album> = emptyList()
)