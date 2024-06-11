package com.sosauce.cutemusic.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.net.toUri
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException

object ImageUtils {
    fun imageRequester(img: Any?, context: Context): ImageRequest {
        return ImageRequest.Builder(context)
            .data(img)
            .crossfade(true)
            .transformations(
                RoundedCornersTransformation(15f)
            )
            .build()
    }

    suspend fun getMusicArt(context: Context, uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.use {
                    it.setDataSource(context, uri)
                    val picture = it.embeddedPicture?: return@withContext null
                    BitmapFactory.decodeByteArray(picture, 0, picture.size)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    fun getAlbumArt(albumId: Long): Any? {
        val sArtworkUri = "content://media/external/audio/albumart".toUri()
        return try {
            return ContentUris.withAppendedId(sArtworkUri, albumId)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}