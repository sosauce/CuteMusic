package com.sosauce.cutemusic.activities

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.audio.Artist
import com.sosauce.cutemusic.audio.MediaStoreHelper
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.logic.NowPlayingState
import com.sosauce.cutemusic.logic.PlayerActions
import com.sosauce.cutemusic.logic.PlayerState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MusicViewModel(
	private val player: Player,
	contentResolver: ContentResolver
) : ViewModel() {

	private val _npState = MutableStateFlow(NowPlayingState())
	val npState: StateFlow<NowPlayingState> = _npState

	private val _state = MutableStateFlow(MusicState())
	val state: StateFlow<MusicState> = _state

	private val _albums = MutableStateFlow<List<Album>>(emptyList())
	val albums: StateFlow<ImmutableList<Album>> = _albums.map { it.toImmutableList() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(2000),
			initialValue = persistentListOf()
		)

	private val _artists = MutableStateFlow<List<Artist>>(emptyList())
	val artists: StateFlow<ImmutableList<Artist>> = _artists.map { it.toImmutableList() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(2000),
			initialValue = persistentListOf()
		)

	val playerState: MutableState<PlayerState> = mutableStateOf(PlayerState.STOPPED)


	private fun setState(reducer: NowPlayingState.() -> NowPlayingState) {
		_npState.value = npState.value.reducer()
	}

	var selectedItem by mutableIntStateOf(0)


	private val playerListener = object : Player.Listener {

		private val MediaMetadata.artWorkBitmap: Bitmap?
			get() = artworkData?.let { bytes ->
				BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
			}

		override fun onIsPlayingChanged(isPlaying: Boolean) {
			super.onIsPlayingChanged(isPlaying)
			setState { copy(isPlaying = isPlaying) }
		}

		override fun onEvents(player: Player, events: Player.Events) {
			super.onEvents(player, events)

			if (player.isPlaying) {
				viewModelScope.launch {
					while (player.isPlaying) {
						val currentPosition = player.currentPosition
						setState {
							copy(
								currentMusicDuration = player.duration,
								currentPosition = currentPosition
							)
						}
						delay(1.seconds)
					}
				}
			}
		}

		override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
			super.onMediaMetadataChanged(mediaMetadata)
			setState {
				copy(
					currentlyPlaying = (mediaMetadata.title ?: currentlyPlaying).toString()
						.ifEmpty { "<unknown>" },
					currentlyArtist = (mediaMetadata.artist ?: currentlyArtist).toString()
						.ifEmpty { "<unknown>" },
					artwork = mediaMetadata.artWorkBitmap ?: artwork
				)
			}
		}
	}


	init {
		// update the state on change
		_npState.onEach { npState ->
			_state.update {
				MusicState(
					currentlyPlaying = npState.currentlyPlaying,
					isPlaying = npState.isPlaying
				)
			}
		}.launchIn(viewModelScope)

		// prepare the player
		viewModelScope.launch {
			preparePlayer(contentResolver)
		}
		// add the listener when the viewmodel is created
		player.addListener(playerListener)
	}

	fun play(uri: Uri) {
		findIndexOfSong(uri).takeIf { it != -1 }?.let { index ->
			player.seekTo(index, 0)
			player.play()
			playerState.value = PlayerState.PLAYING
		} ?: run {
			// Show a toast if problem but meh ion think there will ever be
		}
	}


	private fun findIndexOfSong(uri: Uri): Int {
		val uriString = uri.toString()
		return (0 until player.mediaItemCount).indexOfFirst { player.getMediaItemAt(it).mediaId == uriString }
	}


	fun handlePlayerActions(action: PlayerActions) {
		when (action) {
			is PlayerActions.PlayOrPause -> if (player.isPlaying) player.pause() else player.play()
			is PlayerActions.SeekToNextMusic -> player.seekToNextMediaItem()
			is PlayerActions.SeekToPreviousMusic -> player.seekToPreviousMediaItem()
			is PlayerActions.SeekTo -> player.seekTo(action.position)
		}
	}

	private suspend fun preparePlayer(contentResolver: ContentResolver) {
		_albums.value = MediaStoreHelper.getAlbums(contentResolver)
		_artists.value = MediaStoreHelper.getArtists(contentResolver)
	}

	override fun onCleared() {
		super.onCleared()
		// remove the listerner when the viewmodel is cleared
		player.removeListener(playerListener)
	}


	companion object {
		@Suppress("UNCHECKED_CAST")
		fun create(
			player: Player,
			contentResolver: ContentResolver
		): ViewModelProvider.Factory {
			return object : ViewModelProvider.Factory {
				override fun <T : ViewModel> create(modelClass: Class<T>): T {
					return MusicViewModel(player, contentResolver) as T
				}
			}
		}
	}
}



