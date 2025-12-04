package com.sosauce.cutemusic.data.models

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem

data class CuteTrack(
    val mediaId: String = "",
    val uri: Uri = Uri.EMPTY,
    val artUri: Uri = Uri.EMPTY,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val albumId: Long = 0,
    val artistId: Long = 0,
    val durationMs: Long = 0,
    val trackNumber: Int = 0,
    val year: Int = 0,
    val size: Long = 0,
    val folder: String = "",
    val path: String = "",
    val isSaf: Boolean = false,
    val dateModified: Long = 0,
    val mediaItem: MediaItem = MediaItem.EMPTY
)

//fun MediaItem.toCuteTrack(): CuteTrack {
//    val extras = mediaMetadata.extras ?: Bundle.EMPTY
//
//
//    return CuteTrack(
//        mediaId = mediaId,
//        uri = localConfiguration?.uri ?: Uri.EMPTY,
//        artUri = mediaMetadata.artworkUri ?: Uri.EMPTY,
//        title = mediaMetadata.title?.toString() ?: "<unknown>",
//        artist = mediaMetadata.artist?.toString() ?: "<unknown>",
//        album = mediaMetadata.albumTitle?.toString() ?: "<unknown>",
//        albumId = extras.getLong("album_id", -1L),
//        artistId = extras.getLong("artist_id", -1L),
//        durationMs = mediaMetadata.durationMs ?: 0L,
//        trackNumber = mediaMetadata.trackNumber ?: -1,
//        year = mediaMetadata.recordingYear ?: -1,
//        size = extras.getLong("size", 0L),
//        folder = extras.getString("folder") ?: "",
//        path = extras.getString("path") ?: "",
//        isSaf = extras.getBoolean("is_saf", false),
//        dateModified = extras.getLong("date_modified", 0L)
//    )
//}


