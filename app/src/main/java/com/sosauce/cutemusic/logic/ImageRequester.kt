package com.sosauce.cutemusic.logic

import android.content.Context
import coil.request.ImageRequest

fun imageRequester(img: Any?, context: Context): ImageRequest {
    return ImageRequest.Builder(context)
        .data(img)
        .crossfade(true)
        .allowRgb565(true) // Saves some memory
        .build()
}