package com.sosauce.cutemusic.screens.utils

import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.audio.Artist
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.logic.NowPlayingState

val Music.readableTitle: String
	get() = with(title) { if (length >= 25) take(25) + "..." else this }

val MusicState.currentlyPlayingReadable: String
	get() = if (currentlyPlaying.length >= 18) currentlyPlaying.take(18) + "..." else currentlyPlaying

val NowPlayingState.currentPlayingReadable: String
	get() = if (currentlyPlaying.length >= 35) currentlyPlaying.take(35) + "..."
	else currentlyPlaying

val NowPlayingState.currentArtisitReadableName: String
	get() = if (currentlyArtist.length >= 35) currentlyArtist.take(35) + "..." else currentlyArtist

val Artist.readableName: String
	get() = if (name.length >= 25) name.take(25) + "..." else name

val Album.artistReadable: String
	get() = if (artist.length >= 18) artist.take(18) + "..." + " · " else artist + " · "

val Album.readableName:String
	get() = if (name.length >= 15) name.take(15) + "..." else name
