package com.sosauce.cutemusic.domain.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class Music(
    val id: Long,
    val name: String,
    val artist: String,
    val folder: String,
    val uri: Uri,
    val albumId: Long,
    var art: Bitmap? = null
)