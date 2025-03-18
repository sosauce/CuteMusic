package com.sosauce.cutemusic.ui.shared_components

import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.domain.repository.SafManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PostViewModel(
    private val mediaStoreHelper: MediaStoreHelper,
    safManager: SafManager
) : ViewModel() {


    val musics = mediaStoreHelper.fetchLatestMusics()
        .combine(safManager.fetchLatestSafTracks()) { localMusics, safMusics ->
            localMusics + safMusics
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            mediaStoreHelper.musics
        )

    val safTracks = safManager.fetchLatestSafTracks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )


    val albums = mediaStoreHelper.fetchLatestAlbums().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val artists = mediaStoreHelper.fetchLatestArtists().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val folders = mediaStoreHelper.fetchLatestFoldersWithMusics().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )


    private companion object {
        const val CUTE_ERROR = "CuteError"
    }

    private val _albumSongs = MutableStateFlow<List<MediaItem>>(emptyList())
    val albumSongs: StateFlow<List<MediaItem>> = _albumSongs.asStateFlow()

    private val _artistAlbums = MutableStateFlow<List<Album>>(emptyList())
    val artistAlbums: StateFlow<List<Album>> = _artistAlbums.asStateFlow()

    private val _artistSongs = MutableStateFlow<List<MediaItem>>(emptyList())
    val artistSongs: StateFlow<List<MediaItem>> = _artistSongs.asStateFlow()


    fun loadAlbumSongs(album: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _albumSongs.update {
                    musics.value.fastFilter { it.mediaMetadata.albumTitle.toString() == album }
                }
            } catch (e: Exception) {
                Log.e(CUTE_ERROR, e.message, e)
            }
        }
    }

    fun loadArtistSongs(artistName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _artistSongs.update {
                    musics.value.fastFilter { it.mediaMetadata.artist == artistName }
                }
            } catch (e: Exception) {
                Log.e(CUTE_ERROR, e.message, e)
            }
        }

    }

    fun loadArtistAlbums(artistName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _artistAlbums.update {
                    albums.value.fastFilter { it.artist == artistName }
                }
            } catch (e: Exception) {
                Log.e(CUTE_ERROR, e.message, e)
            }
        }
    }

    fun deleteMusic(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        viewModelScope.launch {
            mediaStoreHelper.deleteMusics(
                uris,
                intentSenderLauncher
            )
        }
    }


    fun editMusic(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        viewModelScope.launch {
            mediaStoreHelper.editMusic(
                uris,
                intentSenderLauncher
            )
        }
    }


}

