package com.sosauce.cutemusic.presentation.screens.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.models.Album
import com.sosauce.cutemusic.data.models.Artist
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.domain.repository.ArtistsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtistDetailsViewModel(
    private val artistName: String,
    private val artistsRepository: ArtistsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ArtistDetailsState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val artist = artistsRepository.fetchArtistDetails(artistName)
            val albums = artistsRepository.fetchArtistAlbums(artistName)
            _state.update {
                it.copy(
                    artist = artist,
                    albums = albums
                )
            }
        }
        viewModelScope.launch {
            artistsRepository.fetchLatestArtistTracks(artistName).collectLatest { tracks ->
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

data class ArtistDetailsState(
    val isLoading: Boolean = false,
    val artist: Artist = Artist(),
    val tracks: List<CuteTrack> = emptyList(),
    val albums: List<Album> = emptyList()
)