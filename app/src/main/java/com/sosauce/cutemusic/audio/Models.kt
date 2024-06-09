package com.sosauce.cutemusic.audio

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

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
	val albumArt: Any?,
	val songs: ImmutableList<Music> = persistentListOf()
)

@Stable
data class Artist(
	val id: Long,
	val name: String,
	val numberOfSongs: Int,
	val numberOfAlbums: Int,
	val songs: ImmutableList<Music> = persistentListOf(),
	val albums: ImmutableList<Album> = persistentListOf()
)
