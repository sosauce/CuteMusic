package com.sosauce.cutemusic.domain.model

import android.graphics.Bitmap
import android.net.Uri

data class Music(
    val id: Long,
    val name: String,
    val artist: String,
    val uri: Uri,
    val albumId: Long,
    val art: Bitmap? = null
)