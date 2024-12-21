package com.sosauce.cutemusic.ui.shared_components

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.CountDownTimer
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
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
import com.kyant.taglib.TagLib
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.MusicState
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.domain.repository.SafManager
import com.sosauce.cutemusic.main.PlaybackService
import com.sosauce.cutemusic.utils.applyLoop
import com.sosauce.cutemusic.utils.applyPlaybackSpeed
import com.sosauce.cutemusic.utils.applyShuffle
import com.sosauce.cutemusic.utils.playAtIndex
import com.sosauce.cutemusic.utils.playFromAlbum
import com.sosauce.cutemusic.utils.playFromArtist
import com.sosauce.cutemusic.utils.playRandom
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException

@OptIn(FlowPreview::class)
@SuppressLint("UnsafeOptInUsageError")
class MusicViewModel(
    private val application: Application,
    private val mediaStoreHelper: MediaStoreHelper,
    private val safManager: SafManager
) : AndroidViewModel(application) {

    private var mediaController: MediaController? by mutableStateOf(null)
    var selectedItem by mutableIntStateOf(0)

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    var sleepCountdownTimer: CountDownTimer? = null


    private val playerListener = @UnstableApi
    object : Player.Listener {
        @UnstableApi
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            _musicState.value = _musicState.value.copy(
                currentlyPlaying = mediaMetadata.title.toString(),
                currentArtist = mediaMetadata.artist.toString(),
                currentArtistId = mediaMetadata.extras?.getLong("artist_id") ?: 0,
                currentArt = mediaMetadata.artworkUri,
                currentPath = mediaMetadata.extras?.getString("path") ?: "No path found!",
                currentMusicUri = mediaMetadata.extras?.getString("uri") ?: "No uri found!",
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
            _musicState.update {
                it.copy(
                    playbackParameters = playbackParameters
                )
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _musicState.update {
                it.copy(
                    isCurrentlyPlaying = isPlaying
                )
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            when (repeatMode) {
                Player.REPEAT_MODE_ONE -> {
                    _musicState.update {
                        it.copy(
                            isLooping = true
                        )
                    }
                }

                Player.REPEAT_MODE_OFF -> {
                    _musicState.update {
                        it.copy(
                            isLooping = false
                        )
                    }
                }

                else -> {
                    _musicState.update {
                        it.copy(
                            isLooping = false
                        )
                    }
                }
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            _musicState.update {
                it.copy(
                    isShuffling = shuffleModeEnabled
                )
            }
        }


        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            viewModelScope.launch {
                while (player.isPlaying) {
                    _musicState.update {
                        it.copy(
                            currentMusicDuration = player.duration,
                            currentPosition = player.currentPosition
                        )
                    }
                    delay(500)
                }
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_IDLE -> {
                    _musicState.update {
                        it.copy(
                            isPlayerReady = false
                        )
                    }
                }

                Player.STATE_READY -> {
                    _musicState.update {
                        it.copy(
                            isPlayerReady = true
                        )
                    }
                }

                else -> {
                    _musicState.update {
                        it.copy(
                            isPlayerReady = true
                        )
                    }
                }
            }
        }
    }

    init {
//        if (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_CAR) {
//            MediaController
//                .Builder(
//                    application,
//                    SessionToken(
//                        application,
//                        ComponentName(application, AutoPlaybackService::class.java)
//                    )
//                )
//                .buildAsync()
//                .apply {
//                    addListener(
//                        {
//                            mediaController = get()
//                            mediaController!!.addListener(playerListener)
//                        },
//                        MoreExecutors.directExecutor()
//                    )
//
//                }
//        } else {
//        }
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
                        viewModelScope.launch {
                            combine(
                                mediaStoreHelper.fetchLatestMusics(),
                                safManager.fetchLatestSafTracks()
                            ) { musics, safTracks ->
                                val combinedList = musics + safTracks
                                combinedList
                            }
                                .debounce(500)
                                .collectLatest { combinedList ->
                                    mediaController!!.replaceMediaItems(
                                        0,
                                        combinedList.size - 1,
                                        combinedList
                                    )
                                }
                        }

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

    fun loadEmbeddedLyrics(): String {

        val fd = getFileDescriptorFromPath(application, musicState.value.currentPath)
        return fd?.dup()?.detachFd()?.let {
            TagLib.getMetadata(it)?.propertyMap["LYRICS"]?.getOrNull(0)
                ?: application.getString(R.string.no_lyrics_note)
        } ?: application.getString(R.string.no_lyrics_note)

    }

    @SuppressLint("Range")
    private fun getFileDescriptorFromPath(
        context: Context,
        filePath: String,
        mode: String = "r"
    ): ParcelFileDescriptor? {
        val resolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.Files.FileColumns._ID)
        val selection = "${MediaStore.Files.FileColumns.DATA}=?"
        val selectionArgs = arrayOf(filePath)

        resolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val fileId = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                if (fileId == -1) {
                    return null
                } else {
                    val fileUri = Uri.withAppendedPath(uri, fileId.toString())
                    try {
                        return resolver.openFileDescriptor(fileUri, mode)
                    } catch (e: FileNotFoundException) {
                        Log.e("MediaStoreReceiver", "File not found: ${e.message}")
                    }
                }
            }
        }

        return null
    }

    override fun onCleared() {
        super.onCleared()
        mediaController!!.removeListener(playerListener)
        mediaController!!.release()
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

            is PlayerActions.SetSleepTimer -> {
                val totalTimeMillis =
                    (action.hours * 60 * 60 * 1000L) + (action.minutes * 60 * 1000L)

                // Cancel any active timer before setting a new one
                sleepCountdownTimer?.cancel()
                sleepCountdownTimer = null

                sleepCountdownTimer = object : CountDownTimer(totalTimeMillis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        _musicState.value = _musicState.value.copy(
                            sleepTimer = millisUntilFinished
                        )
                    }

                    override fun onFinish() {
                        mediaController!!.pause()
                        cancel()
                    }
                }
                sleepCountdownTimer?.start()
            }
        }
    }
}
