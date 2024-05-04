package com.sosauce.cutemusic.audio

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val uri: Uri
)

@Immutable
data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val numberOfSongs: Int,
    var albumArt: Bitmap?,
    var songs: List<Music>
)

@Immutable
data class Artist(
    val id: Long,
    val name: String,
    val numberOfSongs: Int,
    val numberOfAlbums: String,
    var songs: List<Music>,
    var albums: List<Album>
)
