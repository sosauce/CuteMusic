package com.sosauce.cutemusic.presentation.screens.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.models.Album
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.domain.repository.AlbumsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumDetailsViewModel(
    private val albumName: String,
    private val albumsRepository: AlbumsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AlbumDetailsState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val album = albumsRepository.fetchAlbumDetails(albumName)
            _state.update { it.copy(album = album) }
        }

        viewModelScope.launch {
            albumsRepository.fetchLatestAlbumTracks(albumName).collectLatest { tracks ->
                _state.update {
                    it.copy(
                        tracks = tracks,
                        isLoading = false
                    )
                }
            }
        }
    }


}

data class AlbumDetailsState(
    val isLoading: Boolean = false,
    val album: Album = Album(),
    val tracks: List<CuteTrack> = emptyList()
)