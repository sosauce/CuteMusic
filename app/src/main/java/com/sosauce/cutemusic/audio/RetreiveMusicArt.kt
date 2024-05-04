package com.sosauce.cutemusic.audio

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException

suspend fun getMusicArt(context: Context, music: Music): ByteArray? {
    return withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, music.uri)
            retriever.embeddedPicture
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
}

fun getAlbumArt(contentResolver: ContentResolver, albumId: Long): Bitmap? {
    val uri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.loadThumbnail(uri, Size(640, 640), null)
        } else {
            TODO("VERSION.SDK_INT < Q")
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    }
}








