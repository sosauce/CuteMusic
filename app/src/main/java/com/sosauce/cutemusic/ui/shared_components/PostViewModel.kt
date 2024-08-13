package com.sosauce.cutemusic.ui.shared_components

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.domain.blacklist.BlackDao
import com.sosauce.cutemusic.domain.blacklist.BlackEvent
import com.sosauce.cutemusic.domain.blacklist.BlackState
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.domain.model.BlacklistedFolder
import com.sosauce.cutemusic.domain.model.Folder
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PostViewModel(
    private val mediaStoreHelper: MediaStoreHelper,
    private val dao: BlackDao,
): ViewModel() {

    var musics by mutableStateOf(listOf<MediaItem>())
    var albums by mutableStateOf(listOf<Album>())
    var artists by mutableStateOf(listOf<Artist>())
    var folders by mutableStateOf(listOf<Folder>())

    var albumSongs by mutableStateOf(listOf<MediaItem>())
    var artistSongs by mutableStateOf(listOf<MediaItem>())
    var artistAlbums by mutableStateOf(listOf<Album>())

    private var blacklistedFolders = dao.getBlackFolders()
    val _state = MutableStateFlow(BlackState())
    val state = combine(_state, blacklistedFolders) { state, blacklistedFolder1 ->
        state.copy(
            blacklistedFolders = blacklistedFolder1
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BlackState())


    init {
        viewModelScope.launch {

            loadLists()

            state.map { it.blacklistedFolders }
                .collectLatest { blacklistedFolders ->
                    try {
                        musics = mediaStoreHelper.getMusics()
                            .filter { music -> music.mediaMetadata.extras?.getString("folder") !in blacklistedFolders.map { it.path } }
                            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.mediaMetadata.title.toString() })
                    } catch (e: Exception) {
                        Log.e("CuteError", e.message, e)
                    }
                }
        }
    }

    private fun loadLists() {
        viewModelScope.launch {

                try {
                    musics = mediaStoreHelper.getMusics()
                        //.filter { music -> music.folder !in state.value.blacklistedFolders.map { it.path } }
                        .sortedBy { it.mediaMetadata.title.toString().lowercase() }
                    albums = mediaStoreHelper.getAlbums().sortedBy { it.name.lowercase() }
                    artists = mediaStoreHelper.getArtists().sortedBy { it.name.lowercase() }
                    folders = mediaStoreHelper.getFoldersWithMusics()
                } catch (e: Exception) {
                    Log.e("CuteError", e.message, e)
                }
        }
    }

    private fun rescanMediaStore(context: Context) {
        MediaScannerConnection.scanFile(context, arrayOf(Environment.getExternalStorageDirectory().toString()), null, null)
    }

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

    fun onEvent(event: BlackEvent) {
        when (event) {
            is BlackEvent.AddBlack -> {
                val blacklistedFolder = BlacklistedFolder(
                    name = state.value.name.value,
                    path = state.value.path.value,
                )
                viewModelScope.launch {
                    dao.insertBlackFolder(blacklistedFolder)
                }
                _state.update {
                    it.copy(
                        name = mutableStateOf(""),
                        path = mutableStateOf("")
                    )
                }
            }
            is BlackEvent.DeleteBlack -> {
                viewModelScope.launch {
                    dao.deleteBlackFolder(event.blackFolder)
                }
            }
        }
    }
}
