package com.sosauce.cutemusic.ui.shared_components

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.customs.playAtIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicViewModel(
    private val controllerFuture: ListenableFuture<MediaController>
) : ViewModel() {

    private var mediaController: MediaController? by mutableStateOf(null)


    var selectedItem by mutableIntStateOf(0)


    var currentlyPlaying by mutableStateOf("")
    var currentArtist by mutableStateOf("")
    var currentArt: Uri? by mutableStateOf(null)
    var isCurrentlyPlaying by mutableStateOf(false)
    var currentPosition by mutableLongStateOf(0L)
    var currentMusicDuration by mutableLongStateOf(0L)
    var currentMusicUri by mutableStateOf("")


    private val playerListener = object : Player.Listener {
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            currentlyPlaying = (mediaMetadata.title ?: currentlyPlaying).toString()
            currentArtist = (mediaMetadata.artist ?: currentArtist).toString()
            currentArt = mediaMetadata.artworkUri ?: currentArt
            currentMusicUri = mediaMetadata.extras?.getString("uri") ?: currentMusicUri
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            isCurrentlyPlaying = isPlaying
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            if (player.isPlaying) {
                viewModelScope.launch {
                    while (player.isPlaying) {
                        currentMusicDuration = player.duration
                        currentPosition = player.currentPosition
                        delay(500)
                    }
                }
            }
        }
    }


    init {
        controllerFuture.addListener(
            {
                Handler(Looper.getMainLooper()).post {
                    mediaController = controllerFuture.get()
                    mediaController!!.addListener(playerListener)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onCleared() {
        super.onCleared()
        mediaController!!.removeListener(playerListener)
    }


    fun getPlaybackSpeed() = mediaController!!.playbackParameters


    private fun playAtIndex(mediaId: String) {
        try {
            mediaController!!.playAtIndex(
                mediaId = mediaId
            )
        } catch (e: Exception) {
            Log.d("CuteError", "There was a problem playing the music.")
        }
    }

    fun itemClicked(
        mediaId: String,
        musics1: List<MediaItem>
    ) {

        if (mediaController!!.mediaItemCount == 0) {
            musics1.forEach {
                mediaController!!.addMediaItem(it)
            }
            mediaController!!.prepare()
        }

        playAtIndex(mediaId)

    }

    fun isPlaylistEmpty(): Boolean {
        return if (mediaController == null) false else mediaController!!.mediaItemCount != 0
    }


    fun setPlaybackSpeed(speed: Float) {
        mediaController!!.playbackParameters = PlaybackParameters(
            speed,
            speed
        ) // Pitch and speed not being the same is just ugly so set both to be the same #besties
    }


    fun setLoop(shouldLoop: Boolean) {
        if (shouldLoop) {
            mediaController!!.repeatMode = Player.REPEAT_MODE_ONE
        } else {
            mediaController!!.repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    fun setShuffle(shouldShuffle: Boolean) {
        mediaController!!.shuffleModeEnabled = shouldShuffle
    }

    fun quickPlay(uri: Uri?) {
        mediaController!!.clearMediaItems()
        uri?.let { MediaItem.fromUri(it) }?.let {
            mediaController!!.setMediaItem(
                it
            )
        }
        mediaController!!.prepare()
        mediaController!!.play()
    }


    fun handlePlayerActions(action: PlayerActions) {
        when (action) {
            is PlayerActions.PlayOrPause -> if (mediaController!!.isPlaying) mediaController!!.pause() else mediaController!!.play()
            is PlayerActions.SeekToNextMusic -> mediaController!!.seekToNextMediaItem()
            is PlayerActions.SeekToPreviousMusic -> mediaController!!.seekToPreviousMediaItem()
            is PlayerActions.SeekTo -> mediaController!!.seekTo(mediaController!!.currentPosition + action.position)
            is PlayerActions.SeekToSlider -> mediaController!!.seekTo(action.position)
            is PlayerActions.RewindTo -> mediaController!!.seekTo(mediaController!!.currentPosition - action.position)
        }
    }
}


