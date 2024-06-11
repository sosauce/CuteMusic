package com.sosauce.cutemusic.ui.shared_components

import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.domain.model.Music
import com.sosauce.cutemusic.main.App
import com.sosauce.cutemusic.ui.customs.artworkAsBitmap
import com.sosauce.cutemusic.ui.customs.convertToMediaItem
import com.sosauce.cutemusic.ui.customs.playAtIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicViewModel(
     val musics: List<Music>,
     private val controllerFuture: ListenableFuture<MediaController>
) : ViewModel() {

    val playerState: MutableState<PlayerState> = mutableStateOf(PlayerState.STOPPED)
    private var mediaController: MediaController? by mutableStateOf(null)


    var selectedItem by mutableIntStateOf(0)


    var currentlyPlaying by mutableStateOf("")
    var currentArtist by mutableStateOf("")
    var currentArt: Bitmap? by mutableStateOf(null)
    var isCurrentlyPlaying by mutableStateOf(false)
    var currentPosition by mutableLongStateOf(0L)
    var currentMusicDuration by mutableLongStateOf(0L)

    private val playerListener = object : Player.Listener {
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            currentlyPlaying = (mediaMetadata.title ?: currentlyPlaying).toString()
            currentArtist = (mediaMetadata.artist ?: currentArtist).toString()
            currentArt = mediaMetadata.artworkAsBitmap() ?: currentArt
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


    fun playAtIndex(uri: Uri) {
        try {
            mediaController!!.playAtIndex(
                uri = uri,
                setState = { playerState.value = PlayerState.PLAYING }
            )
        } catch (e:Exception) {
            Log.d("CuteError", "There was a problem playing the music.")
        }
    }

    fun populateLists() {
        musics.forEach {
            mediaController!!.addMediaItem(it.convertToMediaItem(it.uri))
        }
        mediaController!!.prepare()

    }

    fun setPlaybackSpeed(speed: Float) {
        mediaController!!.playbackParameters = PlaybackParameters(speed, speed) // Pitch and speed not being the same is just ugly so set both to be the same #besties
    }

    fun setLoop(shouldLoop: Boolean) {
        if (shouldLoop) {
            mediaController!!.repeatMode = Player.REPEAT_MODE_ONE
        } else {
            mediaController!!.repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    fun setShuffle(shouldShuffle: Boolean) {
        if (shouldShuffle) {
            mediaController!!.shuffleModeEnabled = true
        } else {
            mediaController!!.shuffleModeEnabled = false
        }
    }


    fun handlePlayerActions(action: PlayerActions) {
        when (action) {
            is PlayerActions.PlayOrPause -> if (mediaController!!.isPlaying) mediaController!!.pause() else mediaController!!.play()
            is PlayerActions.SeekToNextMusic -> mediaController!!.seekToNextMediaItem()
            is PlayerActions.SeekToPreviousMusic -> mediaController!!.seekToPreviousMediaItem()
            is PlayerActions.SeekTo -> mediaController!!.seekTo(action.position)
        }
    }

}

class MusicViewModelFactory(
    private val app: App,
    private val musics: List<Music>
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MusicViewModel(
            musics = musics,
            controllerFuture = app.container.controllerFuture
        ) as T
    }
}


