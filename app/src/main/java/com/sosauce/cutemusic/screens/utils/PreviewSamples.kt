package com.sosauce.cutemusic.screens.utils

import android.net.Uri
import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.audio.Artist
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.logic.NowPlayingState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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

	val FAKE_ALBUM_MODEL = Album(
		id = 1L,
		name = "Android",
		artist = "Google",
		albumArt = null,
		numberOfSongs = FAKE_MUSICS_MODELS.size,
		songs = FAKE_MUSICS_MODELS
	)

	val FAKE_ARTIST_MODEL = Artist(
		id = 2L,
		name = "Koltin",
		numberOfSongs = FAKE_MUSICS_MODELS.size,
		numberOfAlbums = 1,
		songs = FAKE_MUSICS_MODELS,
		albums = persistentListOf(FAKE_ALBUM_MODEL)
	)


	val FAKE_ARTISTS_MODELS = List(20) { FAKE_ARTIST_MODEL }.toPersistentList()
}