package com.sosauce.cutemusic.activities

import android.net.Uri
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class MusicViewModel(
    private val player: Player
    ) : ViewModel() {


    var currentValue by mutableLongStateOf(0L)
    var title by mutableStateOf(player.mediaMetadata.title.toString())
    var artist by mutableStateOf(player.mediaMetadata.artist.toString())
    var previousTitle by mutableStateOf("")
    var previousArtist by mutableStateOf("")
    var art by mutableStateOf(player.mediaMetadata.artworkData)
    var previousArt: ByteArray? by mutableStateOf(byteArrayOf())
    var isPlayerPlaying by mutableStateOf(player.isPlaying)
    var isPlayerLooping by mutableStateOf(isLooping())
    var isShuffleEnabled by mutableStateOf(player.shuffleModeEnabled)




    init {
        viewModelScope.launch {
            delay(30)
            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    isPlayerPlaying = isPlaying
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                    isShuffleEnabled = shuffleModeEnabled
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    super.onRepeatModeChanged(repeatMode)
                    isPlayerLooping = isLooping()

                }


                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    super.onMediaMetadataChanged(mediaMetadata)
                    previousTitle = title
                    previousArtist = artist
                    previousArt = art
                    title = player.mediaMetadata.title.toString().ifEmpty { "<unknown>" }
                    artist = player.mediaMetadata.artist.toString().ifEmpty { "<unknown>" }
                    art = player.mediaMetadata.artworkData
                }
            })
        }
    }


    fun totalDuration(): String {
        return if (player.playbackState == Player.STATE_READY) {
            val totalSeconds = player.duration / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            String.format("%d:%02d", minutes, seconds)
        } else { "" }
    }

    fun timeLeft(): String {
        return if (player.playbackState == Player.STATE_READY) {
            val totalSeconds = player.duration / 1000
            val currentPositionSeconds = player.currentPosition / 1000
            val remainingDurationSeconds = totalSeconds - currentPositionSeconds
            val remainingMinutes = remainingDurationSeconds / 60
            val remainingSeconds = remainingDurationSeconds % 60
            String.format("%d:%02d", remainingMinutes, remainingSeconds)
        } else { "" }
    }

    @Composable
    fun iconTint(): Color {
        return when(isPlayerLooping) {
            true -> MaterialTheme.colorScheme.primary
            false -> MaterialTheme.colorScheme.onBackground
        }
    }

    @Composable
    fun shuffleIconTint(): Color {
        return when(isShuffleEnabled) {
            true -> MaterialTheme.colorScheme.primary
            false -> MaterialTheme.colorScheme.onBackground
        }
    }



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

    private fun isLooping(): Boolean {
        return when (player.repeatMode) {
            Player.REPEAT_MODE_OFF -> false
            Player.REPEAT_MODE_ONE -> true
            else -> false
        }
    }

    fun greetings(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when {
            hour < 12 -> "Hi !"
            hour < 17 -> "Good afternoon!"
            else -> "Good evening!"
        }
    }







}



