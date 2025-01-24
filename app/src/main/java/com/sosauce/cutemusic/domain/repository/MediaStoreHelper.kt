package com.sosauce.cutemusic.domain.repository

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.domain.model.Folder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MediaStoreHelper {

    val musics: List<MediaItem>
    val albums: List<Album>
    val artists: List<Artist>
    val folders: List<Folder>

    fun fetchMusics(): List<MediaItem>
    fun fetchLatestMusics(): Flow<List<MediaItem>>

    fun fetchAlbums(): List<Album>
    fun fetchLatestAlbums(): Flow<List<Album>>

    fun fetchArtists(): List<Artist>
    fun fetchLatestArtists(): Flow<List<Artist>>

    fun fetchFoldersWithMusics(): List<Folder>
    fun fetchLatestFoldersWithMusics(): Flow<List<Folder>>

    suspend fun deleteMusics(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    )

    suspend fun editMusic(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    )

}