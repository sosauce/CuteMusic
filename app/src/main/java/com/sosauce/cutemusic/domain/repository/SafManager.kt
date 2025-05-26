package com.sosauce.cutemusic.domain.repository

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.kyant.taglib.Metadata
import com.kyant.taglib.TagLib
import com.sosauce.cutemusic.data.datastore.getSafTracks
import com.sosauce.cutemusic.utils.getUriFromByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SafManager(
    private val context: Context
) {


    fun fetchLatestSafTracks(): Flow<List<MediaItem>> = getSafTracks(context)
        .map { tracks ->
            tracks.map { uri ->
                uriToMediaItem(uri.toUri())
            }
        }


    private suspend fun uriToMediaItem(uri: Uri): MediaItem {
        return withContext(Dispatchers.IO) {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { fd ->
                val metadata = loadAudioMetadata(fd)

                val title = metadata?.propertyMap?.get("TITLE")?.getOrNull(0) ?: "<unknown>"
                val artist = metadata?.propertyMap?.get("ARTIST")?.joinToString(", ") ?: "<unknown>"
                val album = metadata?.propertyMap?.get("ALBUM")?.getOrNull(0)
                val duration = metadata?.propertyMap?.get("DURATION")?.getOrNull(0)
                val artUri =
                    TagLib.getFrontCover(fd.dup().detachFd())?.data?.getUriFromByteArray(context)

                MediaItem
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

}