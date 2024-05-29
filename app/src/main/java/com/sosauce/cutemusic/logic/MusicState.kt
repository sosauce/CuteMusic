package com.sosauce.cutemusic.logic

import androidx.compose.runtime.Stable

@Stable
data class MusicState(
    var currentlyPlaying: String = "",
    var isPlaying: Boolean = false,
)

// Avoid MainScreen useless recompositions when currentPosition is changing
@Stable
data class NowPlayingState(
    var currentlyPlaying: String = "",
    var currentlyArtist: String = "",
    var currentMusicDuration: Long = 0L,
    var currentPosition: Long = 0L,
    var isPlaying: Boolean = false,
    var artwork: ByteArray? = null
)