package com.sosauce.cutemusic.audio

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val uri: Uri
)

@Stable
data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val numberOfSongs: Int,
    var albumArt: Any?,
    var songs: List<Music>
)

@Stable
data class Artist(
    val id: Long,
    val name: String,
    val numberOfSongs: Int,
    val numberOfAlbums: Int,
    var songs: List<Music>,
    var albums: List<Album>
)
