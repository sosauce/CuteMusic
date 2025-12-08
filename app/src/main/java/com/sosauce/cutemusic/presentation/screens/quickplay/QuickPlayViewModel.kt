package com.sosauce.cutemusic.presentation.screens.quickplay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.kyant.taglib.Metadata
import com.kyant.taglib.TagLib
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.utils.changeRepeatMode
import com.sosauce.cutemusic.utils.getUriFromByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuickPlayViewModel(
    private val trackUri: Uri,
    private val application: Application
) : AndroidViewModel(application) {

    private val audioAttributes = AudioAttributes
        .Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val listener = object : Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _musicState.update {
                it.copy(
                    isPlaying = isPlaying
                )
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            _musicState.update {
                it.copy(
                    repeatMode = repeatMode
                )
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            viewModelScope.launch {
                while (player.isPlaying) {
                    _musicState.update {
                        it.copy(
                            //duration = player.duration,
                            position = player.currentPosition
                        )
                    }
                    delay(500)
                }
            }
        }
    }


    private val player = ExoPlayer.Builder(application)
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true)
        .build()
        .apply {
            addListener(listener)
        }

    var isSongLoaded by mutableStateOf(false)

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()


    init {

        viewModelScope.launch(Dispatchers.IO) {
            val mediaItem = MediaItem.fromUri(trackUri)
            withContext(Dispatchers.Main) {
                player.addMediaItem(mediaItem)
                player.prepare()
            }

            _musicState.update {
                it.copy(
                    track = loadTrackData()
                )
            }
            isSongLoaded = true

            withContext(Dispatchers.Main) {
                player.play()
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        player.removeListener(listener)
        player.release()
    }


    fun retrieveDuration(): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(application, trackUri)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        } finally {
            retriever.release()
        }
    }

    private fun loadTrackData(): CuteTrack {
        return application.contentResolver.openFileDescriptor(trackUri, "r")?.use { fd ->

            val metadata = loadAudioMetadata(fd)
            val title = metadata?.propertyMap?.get("TITLE")?.getOrNull(0) ?: "<unknown>"
            val artist = metadata?.propertyMap?.get("ARTIST")?.joinToString(", ") ?: "<unknown>"

            val artUri =
                TagLib.getFrontCover(fd.dup().detachFd())?.data?.getUriFromByteArray(application)

            CuteTrack(
                title = title,
                artist = artist,
                durationMs = retrieveDuration(),
                artUri = artUri ?: Uri.EMPTY
            )
        } ?: throw IllegalArgumentException("Unable to open file descriptor for uri")
    }

    private fun loadAudioMetadata(songFd: ParcelFileDescriptor): Metadata? {
        val fd = songFd.dup()?.detachFd() ?: throw NullPointerException()

        return TagLib.getMetadata(fd)
    }


    fun handlePlayerAction(action: PlayerActions) {
        when (action) {
            is PlayerActions.PlayOrPause -> if (player.isPlaying) player.pause() else player.play()
            is PlayerActions.UpdateCurrentPosition -> {
                _musicState.update {
                    it.copy(
                        position = action.position
                    )
                }
            }

            is PlayerActions.SeekToSlider -> player.seekTo(action.position)
            is PlayerActions.SeekTo -> player.seekTo(player.currentPosition + action.position)
            is PlayerActions.RewindTo -> player.seekTo(player.currentPosition - action.position)
            is PlayerActions.ChangeRepeatMode -> player.changeRepeatMode()
            else -> Unit
        }
    }
}