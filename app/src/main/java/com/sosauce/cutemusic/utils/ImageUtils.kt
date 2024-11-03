package com.sosauce.cutemusic.utils

import android.content.ContentUris
import android.content.Context
import androidx.core.net.toUri
import coil3.request.crossfade
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import java.io.FileNotFoundException
import coil3.request.ImageRequest as ImageRequest3

object ImageUtils {
    fun imageRequester(img: Any?, context: Context): ImageRequest3 {
        val request = ImageRequest3.Builder(context)
            .data(img)
            .crossfade(true)
            .transformations(
                RoundedCornersTransformation(15f)
            )
            .build()

        return request
    }

    fun getAlbumArt(albumId: Long): Any? {
        val sArtworkUri = "content://media/external/audio/albumart".toUri()
        return try {
            ContentUris.withAppendedId(sArtworkUri, albumId)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}