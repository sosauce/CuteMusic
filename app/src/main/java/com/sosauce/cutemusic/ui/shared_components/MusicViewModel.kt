package com.sosauce.cutemusic.ui.shared_components

import android.app.Application
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberKillService
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.ui.customs.playAtIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class MusicViewModel (
    private val controllerFuture: ListenableFuture<MediaController>,
    application: Application
) : AndroidViewModel(application) {

    private var mediaController: MediaController? by mutableStateOf(null)
    private val shouldKill by rememberKillService(application)


    var selectedItem by mutableIntStateOf(0)



    var currentlyPlaying by mutableStateOf("")
    var currentArtist by mutableStateOf("")
    var currentArt: Uri? by mutableStateOf(null)
    var isCurrentlyPlaying by mutableStateOf(false)
    var currentPosition by mutableLongStateOf(0L)
    var currentMusicDuration by mutableLongStateOf(0L)
    var currentMusicUri by mutableStateOf("")
    var currentLyrics by mutableStateOf(listOf<Lyrics>())
    var isLooping by mutableStateOf(false)
    var isShuffling by mutableStateOf(false)
    var currentPath by mutableStateOf("")

    private var currentLrcFile by mutableStateOf<File?>(null)

    private val playerListener = object : Player.Listener {
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            currentlyPlaying = (mediaMetadata.title ?: currentlyPlaying).toString()
            currentArtist = (mediaMetadata.artist ?: currentArtist).toString()
            currentArt = mediaMetadata.artworkUri ?: currentArt
            currentPath = (mediaMetadata.extras?.getString("path") ?: currentPath)
            currentMusicUri = mediaMetadata.extras?.getString("uri") ?: currentMusicUri
            currentLrcFile = loadLrcFile(currentPath)
            currentLyrics = parseLrcFile(currentLrcFile)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            isCurrentlyPlaying = isPlaying
        }


        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            viewModelScope.launch {
                while (player.isPlaying) {
                    currentMusicDuration = player.duration
                    currentPosition = player.currentPosition
                    delay(550)
                }
            }
        }
    }

    companion object {
        const val CUTE_ERROR = "CuteError"
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

    private fun loadLrcFile(path: String): File? {
        val lrcFilePath = path.replaceAfterLast('.', "lrc")
        val lrcFile = File(lrcFilePath)
        return if (lrcFile.exists()) lrcFile else null
    }

    private fun parseLrcFile(file: File?): List<Lyrics> {
        val lyrics = mutableListOf<Lyrics>()
        if (file == null) {
            return emptyList()
        }

        viewModelScope.launch {
            file.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2})]""")
                    val matchResult = regex.find(line)

                    if (matchResult != null) {
                        val (minutes, seconds, hundredths) = matchResult.destructured
                        val timeInMillis =
                            minutes.toLong() * 60_000 + seconds.toLong() * 1000 + hundredths.toLong() * 10
                        val lyricText = line.substring(matchResult.range.last + 1).trim()
                        lyrics.add(
                            Lyrics(
                                timeInMillis,
                                lyricText
                            )
                        )
                    }
                }
            }
        }

        return lyrics
    }

    fun loadEmbeddedLyrics(
        path: String
    ): String {
        val file = AudioFileIO.read(File(path))

        file.tag.apply {
            val embeddedLyrics = getFirst(FieldKey.LYRICS)

            return if (embeddedLyrics != "") {
                embeddedLyrics
            } else {
                "No lyrics found !"
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        mediaController!!.removeListener(playerListener)
        mediaController!!.release()
    }




    fun getPlaybackSpeed() = mediaController!!.playbackParameters


    private fun playAtIndex(mediaId: String) {
        try {
            mediaController!!.playAtIndex(
                mediaId = mediaId
            )
        } catch (e: Exception) {
            Log.d(CUTE_ERROR, e.message.toString())
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


    fun isPlaylistEmptyAndDataNotNull(): Boolean {
        return if (mediaController == null) false else mediaController!!.mediaItemCount != 0
    }


    fun setPlaybackSpeed(
        speed: Float = 1f,
        pitch: Float = 1f
    ) {
        mediaController!!.playbackParameters = PlaybackParameters(
            speed,
            pitch
        )
    }



    fun setLoop(
        shouldLoop: Boolean
    ) {
        if (shouldLoop) {
            mediaController!!.repeatMode = Player.REPEAT_MODE_ONE
            isLooping = true
        } else {
            mediaController!!.repeatMode = Player.REPEAT_MODE_OFF
            isLooping = false
        }
    }

    fun setShuffle(shouldShuffle: Boolean) {
        mediaController!!.shuffleModeEnabled = shouldShuffle
        isShuffling = mediaController!!.shuffleModeEnabled
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
            is PlayerActions.RestartSong -> mediaController!!.seekTo(0)
        }
    }
}

