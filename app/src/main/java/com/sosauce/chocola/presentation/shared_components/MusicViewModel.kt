@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.presentation.shared_components

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.CountDownTimer
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.PlaybackService
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.utils.applyPlaybackPitch
import com.sosauce.chocola.utils.applyPlaybackSpeed
import com.sosauce.chocola.utils.applyShuffle
import com.sosauce.chocola.utils.changeRepeatMode
import com.sosauce.chocola.utils.copyMutate
import com.sosauce.chocola.utils.playOrPause
import com.sosauce.chocola.utils.playRandom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration

class MusicViewModel(
    private val application: Application,
    private val userPreferences: UserPreferences
) : AndroidViewModel(application) {

    private var mediaController: MediaController? = null
    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    var sleepCountdownTimer: CountDownTimer? = null
    private val playerListener =
        object : Player.Listener {

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                if (mediaItem == null) return

                musicState.value.loadedMedias.fastFirstOrNull { track ->
                    track.mediaId == mediaItem.mediaId
                }?.also { track ->


                    _musicState.update {
                        it.copy(
                            track = track,
                            audioSessionAudio = mediaController!!.sessionExtras.getInt(
                                "audioSessionId",
                                0
                            ),
                            mediaIndex = mediaController!!.currentMediaItemIndex
                        )
                    }
                }
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                super.onPlaybackParametersChanged(playbackParameters)
                _musicState.update {
                    it.copy(
                        speed = playbackParameters.speed,
                        pitch = playbackParameters.pitch
                    )
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                _musicState.update {
                    it.copy(
                        isPlaying = isPlaying
                    )
                }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                _musicState.update {
                    it.copy(
                        shuffle = shuffleModeEnabled
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

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                _musicState.update {
                    it.copy(
                        position = newPosition.positionMs.coerceIn(0, musicState.value.track.durationMs)
                    )
                }
            }

            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                viewModelScope.launch {
                    while (player.isPlaying) {
                        _musicState.update {
                            it.copy(
                                position = player.currentPosition
                            )
                        }
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
                        loadPlaybackPreferences()
                    },
                    MoreExecutors.directExecutor()
                )
            }

        viewModelScope.launch {
            userPreferences.getPauseOnMute().collectLatest { pauseOnMute ->
                if (pauseOnMute) {
                    observeIsMuted().collectLatest { isMute ->
                        if (isMute) mediaController!!.pause()
                    }
                }
            }

        }
    }


    // I'm not a big fan of allat, but it works
    private fun loadPlaybackPreferences() {
        viewModelScope.launch {

            val savedMusicState = userPreferences.getSavedMusicState()

            mediaController?.run {
                changeRepeatMode(savedMusicState.repeatMode)
                applyShuffle(savedMusicState.shuffle)
                applyPlaybackSpeed(savedMusicState.speed)
                applyPlaybackPitch(savedMusicState.pitch)
                val mediaItems = savedMusicState.loadedMedias.fastMap {
                    MediaItem.fromUri(it.uri).buildUpon()
                        .setMediaId(it.mediaId)
                        .build()
                }
                if (savedMusicState.loadedMedias.isNotEmpty()) {
                    setMediaItems(
                        mediaItems,
                        savedMusicState.mediaIndex,
                        savedMusicState.position
                    )
                    prepare()
                    _musicState.update { it.copy(track = savedMusicState.track, loadedMedias = savedMusicState.loadedMedias) }
                }
            }
        }
    }


    // https://stackoverflow.com/a/78301908/28577483
    private fun observeIsMuted() = callbackFlow {
        val audioManager =
            application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volumeObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                val cv = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                if (cv == 0) trySend(true)
            }
        }

        application.contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            volumeObserver
        )
        awaitClose { application.contentResolver.unregisterContentObserver(volumeObserver) }

    }

    override fun onCleared() {
        super.onCleared()
        runBlocking { userPreferences.saveSavedMusicState(musicState.value) }
        mediaController!!.removeListener(playerListener)
        mediaController!!.release()
    }

    fun handlePlayerActions(action: PlayerActions) {
        when (action) {
            is PlayerActions.RestartSong -> mediaController!!.seekTo(0)
            is PlayerActions.PlayRandom -> mediaController!!.playRandom()
            is PlayerActions.PlayOrPause -> mediaController!!.playOrPause()
            is PlayerActions.SeekToNextMusic -> mediaController!!.seekToNextMediaItem()
            is PlayerActions.SeekToPreviousMusic -> mediaController!!.seekToPreviousMediaItem()
            is PlayerActions.SeekTo -> mediaController!!.seekTo(mediaController!!.currentPosition + action.position)
            is PlayerActions.SeekToSlider -> mediaController!!.seekTo(action.position)
            is PlayerActions.RewindTo -> mediaController!!.seekTo(mediaController!!.currentPosition - action.position)
            is PlayerActions.StopPlayback -> {
                mediaController?.run {
                    stop()
                    clearMediaItems()
                    seekTo(0)
                }

                _musicState.update {
                    it.copy(
                        loadedMedias = emptyList()
                    )
                }
            }
            is PlayerActions.SeekToMusicIndex -> mediaController!!.seekTo(action.index, 0)
            is PlayerActions.Shuffle -> mediaController!!.applyShuffle(!musicState.value.shuffle)
            is PlayerActions.ChangeRepeatMode -> mediaController!!.changeRepeatMode()
            is PlayerActions.SetSpeed -> mediaController!!.applyPlaybackSpeed(action.speed)
            is PlayerActions.SetPitch -> mediaController!!.applyPlaybackPitch(action.pitch)
            is PlayerActions.CancelSleepTimer -> {
                sleepCountdownTimer?.cancel()
                sleepCountdownTimer = null
                _musicState.update {
                    it.copy(
                        sleepTimerRemainingDuration = 0
                    )
                }
            }
            is PlayerActions.Play -> {
                val mediaItemsToPlay = action.tracks.fastMap { it.mediaItem }

                // MediaController needs to update playlist
                if (action.tracks != musicState.value.loadedMedias) {
                    _musicState.update {
                        it.copy(
                            loadedMedias = action.tracks
                        )
                    }
                    mediaController!!.setMediaItems(mediaItemsToPlay)
                }
                if (action.random) {
                    mediaController!!.playRandom()
                } else {
                    mediaController!!.seekTo(action.index, 0)
                    mediaController!!.play()
                }
            }

            is PlayerActions.UpdateCurrentPosition -> {
                _musicState.update {
                    it.copy(
                        position = action.position
                    )
                }
            }

            is PlayerActions.SetSleepTimer -> {
                val totalTimeMillis =
                    Duration.ofHours(action.hours).plusMinutes(action.minutes).toMillis()

                // Cancel any active timer before setting a new one
                sleepCountdownTimer?.cancel()
                sleepCountdownTimer = null

                sleepCountdownTimer = object : CountDownTimer(totalTimeMillis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        _musicState.update {
                            it.copy(
                                sleepTimerRemainingDuration = millisUntilFinished
                            )
                        }
                    }

                    override fun onFinish() {
                        mediaController!!.pause()
                        cancel()
                        sleepCountdownTimer = null
                        _musicState.update {
                            it.copy(
                                sleepTimerRemainingDuration = 0
                            )
                        }
                    }


                }
                sleepCountdownTimer?.start()
            }

            is PlayerActions.ReArrangeQueue -> {
                mediaController!!.moveMediaItem(
                    action.from,
                    action.to
                )
                val loadedMedias = musicState.value.loadedMedias.copyMutate {
                    val itemToMove = this[action.from]
                    removeAt(action.from)
                    add(action.to, itemToMove)
                }

                _musicState.update {
                    it.copy(
                        loadedMedias = loadedMedias
                    )
                }
            }

            is PlayerActions.RemoveFromQueue -> {
                val index = musicState.value.loadedMedias.indexOf(action.track)
                mediaController!!.removeMediaItem(index)

                val loadedMedias = musicState.value.loadedMedias.copyMutate {
                    remove(action.track)
                }
                _musicState.update {
                    it.copy(
                        loadedMedias = loadedMedias
                    )
                }
            }

            is PlayerActions.AddToQueue -> {


                val uniqueTracks = action.cuteTracks.fastFilter { it !in musicState.value.loadedMedias }
                val mediaItems = uniqueTracks.fastMap { MediaItem.fromUri(it.uri) }
                mediaController!!.addMediaItems(mediaItems)

                val loadedMedias = musicState.value.loadedMedias.copyMutate {
                    addAll(uniqueTracks)
                }
                _musicState.update {
                    it.copy(
                        loadedMedias = loadedMedias
                    )
                }
            }
        }
    }
}