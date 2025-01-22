package com.sosauce.cutemusic.data.states

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.media3.common.PlaybackParameters
import com.sosauce.cutemusic.domain.model.Lyrics
import java.io.File

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
    val currentLyrics: List<Lyrics> = listOf(),
    val isLooping: Boolean = false,
    val isShuffling: Boolean = false,
    val currentPath: String = "",
    val currentAlbum: String = "",
    val currentAlbumId: Long = 0,
    val currentSize: Long = 0,
    val currentLrcFile: File? = null,
    val playbackParameters: PlaybackParameters = PlaybackParameters.DEFAULT,
    val isPlayerReady: Boolean = false,
    val sleepTimer: Long = 0,
    val currentMediaId: String = ""
)
