package com.sosauce.cutemusic.ui.shared_components

import android.app.Application
import android.content.ComponentName
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.cutemusic.data.MusicState
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.main.PlaybackService
import com.sosauce.cutemusic.utils.applyLoop
import com.sosauce.cutemusic.utils.applyPlaybackSpeed
import com.sosauce.cutemusic.utils.applyShuffle
import com.sosauce.cutemusic.utils.playAtIndex
import com.sosauce.cutemusic.utils.playFromAlbum
import com.sosauce.cutemusic.utils.playFromArtist
import com.sosauce.cutemusic.utils.playRandom
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class MusicViewModel(
    application: Application,
    private val mediaStoreHelper: MediaStoreHelper
) : AndroidViewModel(application) {

    private var mediaController: MediaController? by mutableStateOf(null)
    var selectedItem by mutableIntStateOf(0)

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()


    private val playerListener = object : Player.Listener {
        @UnstableApi
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            _musicState.value = _musicState.value.copy(
                currentlyPlaying = mediaMetadata.title.toString(),
                currentArtist = mediaMetadata.artist.toString(),
                currentArtistId = mediaMetadata.extras?.getLong("artist_id") ?: 0,
                currentArt = mediaMetadata.artworkUri,
                currentPath = mediaMetadata.extras?.getString("path") ?: "No Path Found!",
                currentMusicUri = mediaMetadata.extras?.getString("uri") ?: "No Uri Found!",
                currentLrcFile = getLrcFile(),
                currentAlbum = mediaMetadata.albumTitle.toString(),
                currentAlbumId = mediaMetadata.extras?.getLong("album_id") ?: 0,
                currentSize = mediaMetadata.extras?.getLong("size") ?: 0,
                currentMusicDuration = mediaMetadata.durationMs ?: 0,
                currentLyrics = parseLyrics()
            )
        }


        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            super.onPlaybackParametersChanged(playbackParameters)
            _musicState.value = _musicState.value.copy(
                playbackParameters = playbackParameters
            )
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _musicState.value = _musicState.value.copy(
                isCurrentlyPlaying = isPlaying
            )
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            when (repeatMode) {
                Player.REPEAT_MODE_ONE -> {
                    _musicState.value = _musicState.value.copy(
                        isLooping = true
                    )
                }

                Player.REPEAT_MODE_OFF -> {
                    _musicState.value = _musicState.value.copy(
                        isLooping = false
                    )
                }

                else -> {
                    _musicState.value = _musicState.value.copy(
                        isLooping = false
                    )
                }
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            _musicState.value = _musicState.value.copy(
                isShuffling = shuffleModeEnabled
            )
        }


        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            viewModelScope.launch {
                while (player.isPlaying) {
                    _musicState.value = _musicState.value.copy(
                        currentMusicDuration = player.duration,
                        currentPosition = player.currentPosition
                    )
                    delay(500)
                }
            }
        }
    }

    init {
        MediaController
            .Builder(
                application,
                SessionToken(
                    application,
                    ComponentName(application, PlaybackService::class.java)
                )
            )
            .buildAsync()
            .apply {
                addListener(
                    {
                        mediaController = get()
                        mediaController!!.addListener(playerListener)
                        mediaController!!.setMediaItems(mediaStoreHelper.musics)
                    },
                    MoreExecutors.directExecutor()
                )

            }
    }


    private fun getLrcFile(): File? {
        val lrcFilePath = musicState.value.currentPath.replaceAfterLast('.', "lrc")
        val lrcFile = File(lrcFilePath)
        return if (lrcFile.exists()) lrcFile else null
    }

    fun parseLyrics(): List<Lyrics> {
        val file = musicState.value.currentLrcFile
        val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2})]""")

        if (file == null) return emptyList()

        return try {
            file.bufferedReader().useLines { lines ->
                lines.asSequence().mapNotNull { line ->
                    regex.find(line)?.let { matchResult ->
                        val (minutes, seconds, hundredths) = matchResult.destructured
                        val millis =
                            minutes.toLong() * 60_000 + seconds.toLong() * 1000 + hundredths.toLong() * 10
                        val lyric = line.substring(matchResult.range.last + 1).trim()
                        Lyrics(millis, lyric)
                    }
                }.toList()
            }
        } catch (e: Exception) {
            e.stackTrace
            emptyList()
        }
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


    fun isPlayerReady(): Boolean {
        return if (mediaController == null) false else
            when (mediaController!!.playbackState) {
                Player.STATE_IDLE -> false
                Player.STATE_READY -> true
                else -> true
            }
    }

    fun handlePlayerActions(action: PlayerActions) {
        when (action) {
            is PlayerActions.RestartSong -> mediaController!!.seekTo(0)
            is PlayerActions.PlayRandom -> mediaController!!.playRandom()
            is PlayerActions.ApplyLoop -> mediaController!!.applyLoop()
            is PlayerActions.ApplyShuffle -> mediaController!!.applyShuffle()
            is PlayerActions.PlayOrPause -> if (mediaController!!.isPlaying) mediaController!!.pause() else mediaController!!.play()
            is PlayerActions.SeekToNextMusic -> mediaController!!.seekToNextMediaItem()
            is PlayerActions.SeekToPreviousMusic -> mediaController!!.seekToPreviousMediaItem()
            is PlayerActions.SeekTo -> mediaController!!.seekTo(mediaController!!.currentPosition + action.position)
            is PlayerActions.SeekToSlider -> mediaController!!.seekTo(action.position)
            is PlayerActions.RewindTo -> mediaController!!.seekTo(mediaController!!.currentPosition - action.position)
            is PlayerActions.StopPlayback -> mediaController!!.stop()
            is PlayerActions.ApplyPlaybackSpeed -> mediaController!!.applyPlaybackSpeed(
                action.speed,
                action.pitch
            )

            is PlayerActions.StartAlbumPlayback -> mediaController!!.playFromAlbum(
                action.albumName,
                action.mediaId,
                mediaStoreHelper.musics
            )

            is PlayerActions.StartArtistPlayback -> mediaController!!.playFromArtist(
                action.artistName,
                action.mediaId,
                mediaStoreHelper.musics
            )

            is PlayerActions.StartPlayback -> {
                // If a user started album/artist playback, we need to make sure all songs are re-fed to Exoplayer when they want to play from anywhere else
                if (mediaController!!.mediaItemCount != mediaStoreHelper.musics.size) {
                    mediaController!!.setMediaItems(mediaStoreHelper.musics)
                    mediaController!!.playAtIndex(action.mediaId)
                } else {
                    mediaController!!.playAtIndex(action.mediaId)
                }
            }

            is PlayerActions.UpdateCurrentPosition -> {
                _musicState.value = _musicState.value.copy(
                    currentPosition = action.position
                )
            }

            is PlayerActions.QuickPlay -> {
                if (mediaController != null) {
                    mediaController!!.clearMediaItems()
                    mediaController!!.setMediaItem(
                        MediaItem.fromUri(action.uri)
                    )
                    mediaController!!.prepare()
                    mediaController!!.play()
                }
            }
        }
    }
}
