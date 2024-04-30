package com.sosauce.cutemusic.audio

import android.net.Uri

data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val uri: Uri
)

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val numberOfSongs: Int,
    val uri: Uri
)
