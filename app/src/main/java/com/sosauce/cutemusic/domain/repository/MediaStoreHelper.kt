package com.sosauce.cutemusic.domain.repository

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.domain.model.Folder

interface MediaStoreHelper {

    val musics: List<MediaItem>
    val albums: List<Album>
    val artists: List<Artist>

    fun fetchMusics(): List<MediaItem>

    fun fetchAlbums(): List<Album>

    fun fetchArtists(): List<Artist>

    fun fetchFoldersWithMusics(): List<Folder>

    suspend fun deleteMusics(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    )

    suspend fun editMusic(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    )

}