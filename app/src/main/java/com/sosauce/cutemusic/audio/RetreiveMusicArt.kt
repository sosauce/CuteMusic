package com.sosauce.cutemusic.audio

import android.content.Context
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getMusicArt(context: Context, music: Music): ByteArray? {
    return withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, music.uri)
        retriever.embeddedPicture
    }
}
