package com.sosauce.cutemusic.audio

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val uri: Uri
)