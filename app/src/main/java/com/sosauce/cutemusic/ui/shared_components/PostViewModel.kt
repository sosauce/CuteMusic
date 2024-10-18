package com.sosauce.cutemusic.ui.shared_components

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.domain.repository.MediaStoreObserver
import com.sosauce.cutemusic.utils.ListToHandle
import com.sosauce.cutemusic.utils.SortingType
import kotlinx.coroutines.launch


class PostViewModel(
    private val mediaStoreHelper: MediaStoreHelper,
    private val application: Application
) : AndroidViewModel(application) {

    var musics by mutableStateOf(
        mediaStoreHelper.musics
    )

    var albums by mutableStateOf(
        mediaStoreHelper.albums
    )

    var artists by mutableStateOf(
        mediaStoreHelper.artists
    )

    var folders by mutableStateOf(
        mediaStoreHelper.fetchFoldersWithMusics()
    )

    private val observer = MediaStoreObserver {
        musics = mediaStoreHelper.fetchMusics()
    }


    init {
        application.contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
    }

    companion object {
        const val CUTE_ERROR = "CuteError"
    }

    override fun onCleared() {
        super.onCleared()
        application.contentResolver.unregisterContentObserver(observer)
    }

    var albumSongs by mutableStateOf(listOf<MediaItem>())
    var artistSongs by mutableStateOf(listOf<MediaItem>())
    var artistAlbums by mutableStateOf(listOf<Album>())

    fun albumSongs(album: String) {
        try {
            albumSongs = musics.filter { it.mediaMetadata.albumTitle.toString() == album }
        } catch (e: Exception) {
            Log.e(CUTE_ERROR, e.message, e)
        }
    }

    fun artistSongs(artistName: String) {
        try {
            artistSongs = musics.filter { it.mediaMetadata.artist == artistName }
        } catch (e: Exception) {
            Log.e(CUTE_ERROR, e.message, e)
        }

    }

    fun artistAlbums(artistName: String) {
        try {
            artistAlbums = albums.filter { it.artist == artistName }
        } catch (e: Exception) {
            Log.e(CUTE_ERROR, e.message, e)
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

    fun handleFiltering(
        listToHandle: ListToHandle,
        sortingType: SortingType,
    ) {
        when (listToHandle) {
            ListToHandle.TRACKS -> {
                musics = if (sortingType == SortingType.ASCENDING)
                    musics.sortedBy { it.mediaMetadata.title.toString() }
                else
                    musics.sortedByDescending { it.mediaMetadata.title.toString() }
            }

            ListToHandle.ALBUMS -> {
                albums = if (sortingType == SortingType.ASCENDING)
                    albums.sortedBy { it.name }
                else
                    albums.sortedByDescending { it.name }
            }

            ListToHandle.ARTISTS -> {
                artists = if (sortingType == SortingType.ASCENDING)
                    artists.sortedBy { it.name }
                else
                    artists.sortedByDescending { it.name }
            }
        }
    }

    fun handleSearch(
        listToHandle: ListToHandle,
        query: String = ""
    ) {
        when (listToHandle) {
            ListToHandle.TRACKS -> {
                musics = mediaStoreHelper.musics.filter {
                    it.mediaMetadata.title?.contains(
                        other = query,
                        ignoreCase = true
                    ) == true
                }
            }

            ListToHandle.ALBUMS -> {
                albums = mediaStoreHelper.albums.filter {
                    it.name.contains(
                        other = query,
                        ignoreCase = true
                    )
                }
            }

            ListToHandle.ARTISTS -> {
                artists = mediaStoreHelper.artists.filter {
                    it.name.contains(
                        other = query,
                        ignoreCase = true
                    )
                }
            }
        }
    }
}

