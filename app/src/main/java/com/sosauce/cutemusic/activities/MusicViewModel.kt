package com.sosauce.cutemusic.activities

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.logic.PlayerActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

class MusicViewModel(private val player: Player) : ViewModel() {

    private val _state = MutableStateFlow(MusicState())
    val state: StateFlow<MusicState> get() = _state.asStateFlow()

    private fun setState(reducer: MusicState.() -> MusicState) {
        _state.value = state.value.reducer()
    }

    var selectedItem by mutableIntStateOf(0)
    var previousTitle by mutableStateOf("")
    var previousArtist by mutableStateOf("")

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                handleIsPlayingChange(isPlaying)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                player.shuffleModeEnabled = shuffleModeEnabled
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                super.onRepeatModeChanged(repeatMode)
                player.repeatMode = repeatMode

            }

            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)

                if (player.isPlaying) {
                    viewModelScope.launch {
                        while (player.isPlaying) {
                            val currentPosition = player.currentPosition
                            handleDurationChange(player.duration, currentPosition)
                            delay(1.seconds)
                        }
                    }
                }
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                previousTitle = state.value.currentlyPlaying
                previousArtist = state.value.currentlyArtist
                // previousArt = art
                handleMetadataChanges(
                    newTitle = mediaMetadata.title.toString().ifEmpty { "<unknown>" },
                    newArtist = mediaMetadata.artist.toString().ifEmpty { "<unknown>" },
                    newArtwork = mediaMetadata.artworkData,
                )
            }
        })
    }

//    @Composable
//    fun iconTint(): Color {
//        return when(isPlayerLooping) {
//            true -> MaterialTheme.colorScheme.primary
//            false -> MaterialTheme.colorScheme.onBackground
//        }
//    }
//
//    @Composable
//    fun shuffleIconTint(): Color {
//        return when(isShuffleEnabled) {
//            true -> MaterialTheme.colorScheme.primary
//            false -> MaterialTheme.colorScheme.onBackground
//        }
//    }


    fun play(uri: Uri) {
        val index = findIndexOfSong(uri)
        if (index != -1) {
            player.seekTo(index, 0)
            player.play()
        } else {
            // I'll add sum here if a user ever reports a bug
        }
    }

    private fun findIndexOfSong(uri: Uri): Int {
        val playlistSize = player.mediaItemCount
        for (i in 0 until playlistSize) {
            val currentMediaItem = player.getMediaItemAt(i)
            if (currentMediaItem.mediaId == uri.toString()) {
                return i
            }
        }
        return -1
    }

    fun handlePlayerActions(action: PlayerActions) {
        when (action) {
            is PlayerActions.Play -> player.play()
            is PlayerActions.Pause -> player.pause()
            is PlayerActions.SeekToNextMusic -> player.seekToNextMediaItem()
            is PlayerActions.SeekToPreviousMusic -> player.seekToPrevious()
            is PlayerActions.ApplyLoop -> if (player.repeatMode == Player.REPEAT_MODE_ONE) player.repeatMode =
                Player.REPEAT_MODE_OFF else player.repeatMode = Player.REPEAT_MODE_ONE

            is PlayerActions.ApplyShuffle -> player.shuffleModeEnabled = !player.shuffleModeEnabled
        }
    }

    fun handleMetadataChanges(
        newTitle: String,
        newArtist: String,
        newArtwork: ByteArray?,
    ) {
        setState {
            copy(
                currentlyPlaying = newTitle,
                currentlyArtist = newArtist,
                artwork = newArtwork
            )
        }
    }

    fun handleIsPlayingChange(newIsPlaying: Boolean) = setState { copy(isPlaying = newIsPlaying) }
    fun handleDurationChange(newDuration: Long, newCurrentPos: Long) =
        setState { copy(currentMusicDuration = newDuration, currentPosition = newCurrentPos) }
}




