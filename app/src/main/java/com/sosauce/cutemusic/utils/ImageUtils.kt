package com.sosauce.cutemusic.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.ImageResult
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.request.transformations
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException

object ImageUtils {

    @Composable
    fun imageRequester(img: Any?): ImageRequest {
        val context = LocalContext.current
        return  ImageRequest.Builder(context)
            .data(img)
            .crossfade(true)
            .transformations()
            .diskCacheKey(img.toString())
            .memoryCacheKey(img.toString())
            .build()
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

    // Kinda ugly fix to always load the new art for MaterialArt, but I guess it's better than loading the viewmodel in the app's theme
    suspend fun loadNewArt(
        context: Context,
        art: Uri?,
        onImageLoadSuccess: (ImageBitmap) -> Unit
    ) = withContext(Dispatchers.IO) {
        val imageLoader = ImageLoader.Builder(context).build()
        val request = ImageRequest.Builder(context)
            .data(art)
            .allowHardware(false)
            .build()
        val result = imageLoader.execute(request)

        if (result is SuccessResult) {
            onImageLoadSuccess(result.image.toBitmap().asImageBitmap())
        }
    }
}