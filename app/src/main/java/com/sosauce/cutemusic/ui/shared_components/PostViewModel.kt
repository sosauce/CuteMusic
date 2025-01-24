package com.sosauce.cutemusic.ui.shared_components

import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.domain.repository.SafManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.filter


class PostViewModel(
    private val mediaStoreHelper: MediaStoreHelper,
    //private val safManager: SafManager
) : ViewModel() {



//    @SuppressLint("UnsafeOptInUsageError")
//    var musics = combine(safTracks, mediaStoreHelper.fetchLatestMusics()) { safList, trackList ->
//        safList + trackList
//    }.stateIn(
//        CoroutineScope(Dispatchers.IO),
//        SharingStarted.WhileSubscribed(5000),
//        mediaStoreHelper.musics
//    )

    val musics = mediaStoreHelper.fetchLatestMusics().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        mediaStoreHelper.musics
    )

//    val safTracks = safManager.fetchLatestSafTracks().stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(5000),
//        emptyList()
//    )


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

    var albumSongs by mutableStateOf(listOf<MediaItem>())
    var artistSongs by mutableStateOf(listOf<MediaItem>())
    var artistAlbums by mutableStateOf(listOf<Album>())

    fun albumSongs(album: String) {
        try {
            albumSongs = musics.value.filter { it.mediaMetadata.albumTitle.toString() == album }
        } catch (e: Exception) {
            Log.e(CUTE_ERROR, e.message, e)
        }
    }

    fun artistSongs(artistName: String) {
        try {
            artistSongs = musics.value.filter { it.mediaMetadata.artist == artistName }
        } catch (e: Exception) {
            Log.e(CUTE_ERROR, e.message, e)
        }

    }

    fun artistAlbums(artistName: String) {
        try {
            artistAlbums = albums.value.filter { it.artist == artistName }
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


}

