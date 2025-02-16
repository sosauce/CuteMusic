package com.sosauce.cutemusic.domain.repository

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.kyant.taglib.Metadata
import com.kyant.taglib.Picture
import com.kyant.taglib.TagLib
import com.sosauce.cutemusic.data.datastore.getSafTracks
import com.sosauce.cutemusic.utils.getUriFromByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.util.UUID

class SafManager(
    private val context: Context
) {


    @UnstableApi
    fun fetchLatestSafTracks(): StateFlow<List<MediaItem>> = getSafTracks(context)
        .map { tracks ->
            tracks.map { uri ->
                uriToMediaItem2(uri.toUri())
            }
        }
        .stateIn(
            CoroutineScope(Dispatchers.IO),
            SharingStarted.WhileSubscribed(5000),
            listOf()
        )


    private suspend fun uriToMediaItem2(uri: Uri): MediaItem {
        return withContext(Dispatchers.IO) {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { fd ->
                val metadata = loadAudioMetadata(fd)
                val image = loadAudioArt(fd)

                val title = metadata?.propertyMap["TITLE"]?.getOrNull(0) ?: "<unknown>"
                val artist = metadata?.propertyMap["ARTIST"]?.joinToString(", ") ?: "<unknown>"
                val album = metadata?.propertyMap["ALBUM"]?.getOrNull(0)
                val duration = metadata?.propertyMap["DURATION"]?.getOrNull(0)

                val imageToBase64 = Base64.encodeToString(image?.data, Base64.DEFAULT)
                val artUri = Uri.parse("data:image/png;base64,$imageToBase64")

                return@withContext MediaItem
                    .Builder()
                    .setUri(uri)
                    .setMediaId(uri.hashCode().toString())
                    .setMediaMetadata(
                        MediaMetadata
                            .Builder()
                            .setIsBrowsable(false)
                            .setIsPlayable(true)
                            .setTitle(title)
                            .setArtist(artist)
                            .setAlbumTitle(album)
                            .setArtworkUri(artUri)
                            .setDurationMs(duration?.toLong() ?: 0)
                            .setExtras(
                                Bundle()
                                    .apply {
                                        putString("folder", "SAF")
                                        putLong("size", fd.statSize)
                                        putString("path", "${uri.path}")
                                        putString("uri", uri.toString())
                                        putLong("album_id", 0)
                                        putLong("artist_id", 0)
                                        putBoolean("is_saf", true)
                                        // putInt("isFavorite", isFavorite)
                                    }).build()
                    )
                    .build()
            } ?: throw IllegalArgumentException("Unable to open file descriptor for uri")
        }
    }


    private suspend fun loadAudioMetadata(songFd: ParcelFileDescriptor): Metadata? {
        val fd = songFd.dup()?.detachFd() ?: throw NullPointerException()

        return withContext(Dispatchers.IO) {
            TagLib.getMetadata(fd)
        }
    }

    private suspend fun loadAudioArt(songFd: ParcelFileDescriptor): Picture? {
        val fd = songFd.dup()?.detachFd() ?: throw NullPointerException()

        return withContext(Dispatchers.IO) {
            TagLib.getFrontCover(fd)
        }
    }


    @UnstableApi
    private suspend fun uriToMediaItem(uri: Uri): MediaItem = withContext(Dispatchers.IO) {

        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(context, uri)

            val id = uri.hashCode()
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
            val size =
                context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val artUri = retriever.embeddedPicture?.getUriFromByteArray(context)

            return@withContext MediaItem
                .Builder()
                .setUri(uri)
                .setMediaId(id.toString())
                .setMediaMetadata(
                    MediaMetadata
                        .Builder()
                        .setIsBrowsable(false)
                        .setIsPlayable(true)
                        .setTitle(title)
                        .setArtist(artist)
                        .setAlbumTitle(album)
                        .setArtworkUri(artUri)
                        .setDurationMs(duration?.toLong() ?: 0)
                        .setExtras(
                            Bundle()
                                .apply {
                                    putString("folder", "SAF")
                                    putLong("size", size)
                                    putString("path", "${uri.path}")
                                    putString("uri", uri.toString())
                                    putLong("album_id", 0)
                                    putLong("artist_id", 0)
                                    putBoolean("is_saf", true)
                                    // putInt("isFavorite", isFavorite)
                                }).build()
                )
                .build()

        } catch (e: Exception) {
            Log.d("FAILED_SAF", "uriToMediaItem: ${e.stackTrace} ${e.message}")
        } finally {
            retriever.release()
        }

        return@withContext MediaItem
            .Builder()
            .setUri(uri)
            .setMediaId(UUID.randomUUID().toString())
            .setMediaMetadata(
                MediaMetadata
                    .Builder()
                    .setIsBrowsable(false)
                    .setIsPlayable(true)
                    .setTitle("No title")
                    .setArtist("No artist")
                    .setAlbumTitle("No album")
                    .setArtworkUri(Uri.EMPTY)
                    .setDurationMs(0)
                    .setExtras(
                        Bundle()
                            .apply {
                                putString("folder", "SAF")
                                putLong("size", 0)
                                putString("path", "${uri.path}")
                                putString("uri", uri.toString())
                                putLong("album_id", 0)
                                putLong("artist_id", 0)
                                putBoolean("is_saf", true)
                                // putInt("isFavorite", isFavorite)
                            }).build()
            )
            .build()
    }

}