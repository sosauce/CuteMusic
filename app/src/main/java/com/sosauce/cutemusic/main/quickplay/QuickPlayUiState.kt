package com.sosauce.cutemusic.main.quickplay

data class QuickPlayUiState(
    val isSongLoaded: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val artUri: String = "",
    val duration: Long = 0,
    val currentPosition: Long = 0,
    val isPlaying: Boolean = false
)
