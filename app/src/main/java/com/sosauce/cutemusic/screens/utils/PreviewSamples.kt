package com.sosauce.cutemusic.screens.utils

import android.net.Uri
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.logic.NowPlayingState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

object PreviewSamples {

	val FAKE_MUSIC_MODEL = Music(id = 0, title = "Music", artist = "Android", uri = Uri.EMPTY)

	val FAKE_MUSICS_MODELS: ImmutableList<Music> = List(20) { FAKE_MUSIC_MODEL }
		.toPersistentList()

	val FAKE_NOW_PLAYING_STATE = NowPlayingState(
		currentlyPlaying = "Jetpack Compose",
		currentlyArtist = "Android",
		currentMusicDuration = 7000L,
		isPlaying = true
	)

	val FAKE_MUSIC_STATE = MusicState(currentlyPlaying = "Musics", isPlaying = true)
}