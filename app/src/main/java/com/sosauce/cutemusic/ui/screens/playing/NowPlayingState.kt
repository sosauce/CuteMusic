package com.sosauce.cutemusic.ui.screens.playing

import androidx.media3.common.MediaItem

data class NowPlayingState(
    var currentlyPlaying: String = "",
    var currentlyArtist: String = "",
    var currentMusicDuration: Long = 0L,
    var currentPosition: Long = 0L,
    var isPlaying: Boolean = false,
    var artwork: ByteArray? = null,
    val musics: List<MediaItem>? = emptyList()
)