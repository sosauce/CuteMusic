package com.sosauce.cutemusic.ui.screens.main

data class MusicState(
    var currentlyPlaying: String = "",
    var isPlaying: Boolean = false,
    var artwork: ByteArray? = null
)