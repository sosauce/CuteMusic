package com.sosauce.cutemusic.data

import android.net.Uri
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.sosauce.cutemusic.domain.model.Lyrics
import java.io.File

data class MusicState(
    var currentlyPlaying: String = "",
    var currentArtist: String = "",
    val currentArtistId: Long = 0,
    var currentArt: Uri? = null,
    var isCurrentlyPlaying: Boolean = false,
    var currentPosition: Long = 0L,
    var currentMusicDuration: Long = 0L,
    var currentMusicUri: String = "",
    var currentLyrics: List<Lyrics> = listOf(),
    var isLooping: Boolean = false,
    var isShuffling: Boolean = false,
    var currentPath: String = "",
    val currentAlbum: String = "",
    val currentAlbumId: Long = 0,
    val currentSize: Long = 0,
    val currentLrcFile: File? = null,
    val playbackParameters: PlaybackParameters = PlaybackParameters.DEFAULT,
)
