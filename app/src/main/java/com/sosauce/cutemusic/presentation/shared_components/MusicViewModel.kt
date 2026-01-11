@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.cutemusic.presentation.shared_components

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.CountDownTimer
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.cutemusic.data.LyricsParser
import com.sosauce.cutemusic.data.datastore.UserPreferences
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.PlaybackService
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.utils.applyPlaybackPitch
import com.sosauce.cutemusic.utils.applyPlaybackSpeed
import com.sosauce.cutemusic.utils.applyShuffle
import com.sosauce.cutemusic.utils.changeRepeatMode
import com.sosauce.cutemusic.utils.copyMutate
import com.sosauce.cutemusic.utils.playOrPause
import com.sosauce.cutemusic.utils.playRandom
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration

class MusicViewModel(
    private val application: Application,
    private val lyricsParser: LyricsParser,
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

                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) return

                viewModelScope.launch {
                    musicState.value.loadedMedias.fastFirstOrNull { track ->
                        track.mediaId == mediaItem?.mediaId
                    }?.also { track ->
                        _musicState.update {
                            it.copy(
                                track = track,
                                lyrics = lyricsParser.parseLyrics(track.path),
                                audioSessionAudio = mediaController!!.sessionExtras.getInt(
                                    "audioSessionId",
                                    0
                                ),
                                mediaIndex = mediaController!!.currentMediaItemIndex
                            )
                        }
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

//                        viewModelScope.launch {
//                            saveSavedPosition(application, musicState.value.position)
//                            saveSavedMediaId(application, musicState.value.track.mediaId)
//                        }

                        _musicState.update {
                            it.copy(track = CuteTrack())
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
                        position = newPosition.positionMs
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

//        viewModelScope.launch {
//            getPauseOnMute(application.applicationContext).collectLatest { pauseOnMute ->
//                if (pauseOnMute) {
//                    observeIsMuted().collectLatest { isMute ->
//                        if (isMute) mediaController!!.pause()
//                    }
//                }
//            }
//
//        }
    }


    // I'm not a big fan of allat but it works
    private fun loadPlaybackPreferences() {
        viewModelScope.launch {

            val savedMusicState = userPreferences.getSavedMusicState()

            mediaController!!.changeRepeatMode(savedMusicState.repeatMode)
            mediaController!!.applyShuffle(savedMusicState.shuffle)
            mediaController!!.applyPlaybackSpeed(savedMusicState.speed)
            mediaController!!.applyPlaybackPitch(savedMusicState.pitch)

            if (savedMusicState.loadedMedias.isNotEmpty()) {
                mediaController!!.setMediaItems(
                    savedMusicState.loadedMedias.fastMap {
                        MediaItem.Builder().setUri(it.uri).setMediaId(it.mediaId).build()
                    },
                    savedMusicState.mediaIndex,
                    savedMusicState.position
                )
                mediaController!!.prepare()
                _musicState.update { it.copy(track = savedMusicState.track) }
            }
        }
    }


    // https://stackoverflow.com/a/78301908/28577483
    private fun observeIsMuted() = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.getIntExtra("android.media.VOLUME_CHANGED_ACTION", 0)) {
                    AudioManager.STREAM_MUSIC -> trySend(
                        intent.getBooleanExtra(
                            "android.media.EXTRA_STREAM_VOLUME_MUTED",
                            false
                        )
                    )
                }
            }
        }

        application.registerReceiver(
            receiver,
            IntentFilter("android.media.STREAM_MUTE_CHANGED_ACTION")
        )
        awaitClose { application.unregisterReceiver(receiver) }

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
            is PlayerActions.StopPlayback -> mediaController!!.stop()
            is PlayerActions.SeekToMusicIndex -> mediaController!!.seekTo(action.index, 0)
            is PlayerActions.Shuffle -> mediaController!!.applyShuffle(!musicState.value.shuffle)
            is PlayerActions.ChangeRepeatMode -> mediaController!!.changeRepeatMode()
            is PlayerActions.SetSpeed -> mediaController!!.applyPlaybackSpeed(action.speed)
            is PlayerActions.SetPitch -> mediaController!!.applyPlaybackPitch(action.pitch)
            is PlayerActions.Play -> {
                val mediaItemsToPlay = action.tracks.fastMap { it.mediaItem }

                // MediaController needs to update playlist
                if (action.tracks != musicState.value.loadedMedias) {
                    println("loaded medias updated")
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
                val exists =
                    musicState.value.loadedMedias.fastAny { it.mediaId == action.cuteTrack.mediaId }
                if (!exists) {
                    mediaController!!.addMediaItem(MediaItem.fromUri(action.cuteTrack.uri))
                }
            }
        }
    }
}