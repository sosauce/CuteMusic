package com.sosauce.cutemusic.main.quickplay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.sosauce.cutemusic.data.actions.PlayerActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuickPlayViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val audioAttributes = AudioAttributes
        .Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val listener = object : Player.Listener {
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            _uiState.update {
                it.copy(
                    title = mediaMetadata.title.toString(),
                    artist = mediaMetadata.artist.toString(),
                    artUri = mediaMetadata.artworkUri.toString(),
                )
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _uiState.update {
                it.copy(
                    isPlaying = isPlaying
                )
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            viewModelScope.launch {
                while (player.isPlaying) {
                    _uiState.update {
                        it.copy(
                            duration = player.duration,
                            currentPosition = player.currentPosition
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


    private val _uiState = MutableStateFlow(QuickPlayUiState())
    val uiState = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            while (player.mediaItemCount == 0) delay(300)

            _uiState.update {
                it.copy(
                    isSongLoaded = true
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.removeListener(listener)
        player.release()
    }


    fun loadSong(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.addMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun loadAlbumArt(context: Context, uri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            val art = retriever.embeddedPicture
            art?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }


    fun handlePlayerAction(action: PlayerActions) {
        when (action) {
            is PlayerActions.PlayOrPause -> if (player.isPlaying) player.pause() else player.play()
            is PlayerActions.UpdateCurrentPosition -> {
                _uiState.update {
                    it.copy(
                        currentPosition = action.position
                    )
                }
            }

            is PlayerActions.SeekToSlider -> player.seekTo(action.position)
            is PlayerActions.SeekTo -> player.seekTo(player.currentPosition + action.position)
            is PlayerActions.RewindTo -> player.seekTo(player.currentPosition - action.position)
            else -> {}
        }
    }


}