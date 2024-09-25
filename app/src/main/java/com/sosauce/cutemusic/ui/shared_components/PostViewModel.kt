package com.sosauce.cutemusic.ui.shared_components

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn


class PostViewModel(
    mediaStoreHelper: MediaStoreHelper
) : ViewModel() {


    val musics =
        mediaStoreHelper
            .getMusics()
            .sortedBy { it.mediaMetadata.title.toString().lowercase() }

    var albums = mediaStoreHelper
        .getAlbums()
        .sortedBy { it.name.lowercase() }

    var artists = mediaStoreHelper
        .getArtists()
        .sortedBy { it.name.lowercase() }

    var folders = mediaStoreHelper
        .getFoldersWithMusics()

    var albumSongs by mutableStateOf(listOf<MediaItem>())
    var artistSongs by mutableStateOf(listOf<MediaItem>())
    var artistAlbums by mutableStateOf(listOf<Album>())

    fun albumSongs(albumId: Long) {
        try {
            albumSongs = musics.filter { it.mediaMetadata.extras?.getLong("albumId") == albumId }
        } catch (e: Exception) {
            Log.e("CuteError", e.message, e)
        }
    }

    fun artistSongs(artistName: String) {
        try {
            artistSongs = musics.filter { it.mediaMetadata.artist == artistName }
        } catch (e: Exception) {
            Log.e("CuteError", e.message, e)
        }

    }

    fun artistAlbums(artistName: String) {
        try {
            artistAlbums = albums.filter { it.artist == artistName }
        } catch (e: Exception) {
            Log.e("CuteError", e.message, e)
        }

    }
}
