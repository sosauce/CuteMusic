package com.sosauce.cutemusic.data.states

import android.net.Uri
import androidx.compose.runtime.Stable
import com.sosauce.cutemusic.domain.model.Lyrics

@Stable
data class MusicState(
    val title: String = "",
    val artist: String = "",
    val artistId: Long = 0,
    val art: Uri? = null,
    val isPlaying: Boolean = false,
    val position: Long = 0L,
    val duration: Long = 0L,
    val uri: String = "",
    val path: String = "",
    val album: String = "",
    val albumId: Long = 0,
    val size: Long = 0,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val isPlayerReady: Boolean = false,
    val sleepTimerActive: Boolean = false,
    val mediaId: String = "",
    val mediaIndex: Int = 0,
    val loadedMedias: List<String> = emptyList(), // List of mediaIds ofc!
    val lyrics: List<Lyrics> = emptyList()
)
