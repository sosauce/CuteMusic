package com.sosauce.cutemusic.ui.shared_components

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.domain.model.Music
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import kotlinx.coroutines.launch


class PostViewModel(
    private val mediaStoreHelper: MediaStoreHelper
): ViewModel() {

    var musics by mutableStateOf(listOf<Music>())
    var albums by mutableStateOf(listOf<Album>())
    var artists by mutableStateOf(listOf<Artist>())

    var albumSongs by mutableStateOf(listOf<Music>())
    var artistSongs by mutableStateOf(listOf<Music>())
    var artistAlbums by mutableStateOf(listOf<Album>())

    init {
        viewModelScope.launch {
            try {
                musics = mediaStoreHelper.getMusics()
                albums = mediaStoreHelper.getAlbums()
                artists = mediaStoreHelper.getArtists()
            } catch (e: Exception) {
                Log.e("CuteError", e.message, e)
            }
        }
    }

    fun albumSongs(albumId: Long) {
             try {
                albumSongs = musics.filter { it.albumId == albumId }
            } catch (e: Exception) {
                Log.e("CuteError", e.message, e)
            }
    }

    fun artistSongs(artistName: String) {
            try {
                artistSongs = musics.filter { it.artist == artistName }
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
