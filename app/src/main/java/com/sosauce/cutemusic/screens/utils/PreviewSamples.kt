package com.sosauce.cutemusic.screens.utils

import android.net.Uri
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.logic.NowPlayingState

object PreviewSamples {

	val FAKE_MUSIC_MODEL = Music(id = 0, title = "Music", artist = "Android", uri = Uri.EMPTY)

	val FAKE_NOW_PLAYING_STATE = NowPlayingState(
		currentlyPlaying = "Jetpack Compose",
		currentlyArtist = "Android",
		currentMusicDuration = 7000L,
		isPlaying = true
	)
}