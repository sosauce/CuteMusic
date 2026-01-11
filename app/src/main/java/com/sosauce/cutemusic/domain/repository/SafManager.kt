@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.cutemusic.domain.repository

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import com.kyant.taglib.Metadata
import com.kyant.taglib.TagLib
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.getSafTracks
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.utils.getUriFromByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

class SafManager(
    private val context: Context
) {


    fun fetchLatestSafTracks(): Flow<List<CuteTrack>> = getSafTracks(context)
        .mapLatest { tracks ->
            tracks.map { uri ->
                uriToTrack(uri.toUri())
            }
        }
        .flowOn(Dispatchers.IO)


    private fun uriToTrack(uri: Uri): CuteTrack {
        return context.contentResolver.openFileDescriptor(uri, "r")?.use { fd ->
            val metadata = loadAudioMetadata(fd)

            val title = metadata?.propertyMap?.get("TITLE")?.getOrNull(0) ?: "<unknown>"
            val artist = metadata?.propertyMap?.get("ARTIST")?.joinToString(", ") ?: "<unknown>"
            val album = metadata?.propertyMap?.get("ALBUM")?.getOrNull(0)
            val duration = metadata?.propertyMap?.get("DURATION")?.getOrNull(0)
            val artUri =
                TagLib.getFrontCover(fd.dup().detachFd())?.data?.getUriFromByteArray(context)

            CuteTrack(
                mediaId = uri.hashCode().toString(),
                uri = uri,
                artUri = artUri ?: Uri.EMPTY,
                title = title,
                artist = artist,
                album = album ?: context.getString(R.string.unknown),
                albumId = 0,
                artistId = 0,
                durationMs = duration?.toLong() ?: 0,
                trackNumber = 0,
                year = 0,
                size = fd.statSize,
                folder = "SAF",
                path = uri.path ?: "Unknown path",
                isSaf = true,
                dateModified = 0,
                mediaItem = MediaItem.fromUri(uri)
            )
        } ?: throw IllegalArgumentException("Unable to open file descriptor for uri")
    }


    private fun loadAudioMetadata(songFd: ParcelFileDescriptor): Metadata? {
        val fd = songFd.dup()?.detachFd() ?: throw NullPointerException()

        return TagLib.getMetadata(fd)
    }

}