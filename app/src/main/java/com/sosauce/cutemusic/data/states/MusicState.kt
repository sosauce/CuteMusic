package com.sosauce.cutemusic.data.states

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.media3.common.PlaybackParameters

@Stable
data class MusicState(
    val currentlyPlaying: String = "",
    val currentArtist: String = "",
    val currentArtistId: Long = 0,
    val currentArt: Uri? = null,
    val isCurrentlyPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val currentMusicDuration: Long = 0L,
    val currentMusicUri: String = "",
    val isLooping: Boolean = false,
    val isShuffling: Boolean = false,
    val currentPath: String = "",
    val currentAlbum: String = "",
    val currentAlbumId: Long = 0,
    val currentSize: Long = 0,
    val playbackParameters: PlaybackParameters = PlaybackParameters.DEFAULT,
    val isPlayerReady: Boolean = false,
    val sleepTimer: Long = 0,
    val currentMediaId: String = ""
)
