package com.sosauce.cutemusic.logic

import androidx.compose.runtime.Immutable

@Immutable
data class MusicState(
    var currentlyPlaying: String = "",
    var currentlyArtist: String = "",
    var currentMusicDuration: Long = 0L, // change type if its not long
    var currentPosition: Long = 0L,
    var isPlaying: Boolean = false,
    var artwork: ByteArray? = null
)