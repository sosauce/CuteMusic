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
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.cutemusic.data.AbstractTracksScanner
import com.sosauce.cutemusic.data.LyricsParser
import com.sosauce.cutemusic.data.datastore.getPitch
import com.sosauce.cutemusic.data.datastore.getRepeatMode
import com.sosauce.cutemusic.data.datastore.getSavedMediaId
import com.sosauce.cutemusic.data.datastore.getSavedPosition
import com.sosauce.cutemusic.data.datastore.getShouldShuffle
import com.sosauce.cutemusic.data.datastore.getSpeed
import com.sosauce.cutemusic.data.datastore.savePitch
import com.sosauce.cutemusic.data.datastore.saveRepeatMode
import com.sosauce.cutemusic.data.datastore.saveSavedMediaId
import com.sosauce.cutemusic.data.datastore.saveSavedPosition
import com.sosauce.cutemusic.data.datastore.saveShouldShuffle
import com.sosauce.cutemusic.data.datastore.saveSpeed
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.PlaybackService
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.domain.repository.SafManager
import com.sosauce.cutemusic.utils.LastPlayed
import com.sosauce.cutemusic.utils.applyPlaybackPitch
import com.sosauce.cutemusic.utils.applyPlaybackSpeed
import com.sosauce.cutemusic.utils.applyShuffle
import com.sosauce.cutemusic.utils.changeRepeatMode
import com.sosauce.cutemusic.utils.copyMutate
import com.sosauce.cutemusic.utils.equalsIgnoreOrder
import com.sosauce.cutemusic.utils.playOrPause
import com.sosauce.cutemusic.utils.playRandom
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration

class MusicViewModel(
    private val application: Application,
    private val abstractTracksScanner: AbstractTracksScanner,
    private val safManager: SafManager,
    private val lyricsParser: LyricsParser
) : AndroidViewModel(application) {

    private var mediaController: MediaController? = null
    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()


    var sleepCountdownTimer: CountDownTimer? = null


    private val allTracks = combine(
        abstractTracksScanner.fetchLatestTracks(null, null),
        safManager.fetchLatestSafTracks()
    ) { local, saf -> local + saf }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val safTracks = safManager.fetchLatestSafTracks()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    private val playerListener =
        object : Player.Listener {

//            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
//                super.onMediaMetadataChanged(mediaMetadata)
//                viewModelScope.launch {
//                    _musicState.update {
//                        it.copy(
//                            title = mediaMetadata.title?.toString() ?: "Nothing playing!",
//                            artist = mediaMetadata.artist?.toString() ?: "No artist!",
//                            mediaId = mediaController!!.currentMediaItem?.mediaId ?: "",
//                            artistId = mediaMetadata.extras?.getLong("artist_id") ?: 0,
//                            art = mediaMetadata.artworkUri,
//                            path = mediaMetadata.extras?.getString("path") ?: "No path found!",
//                            uri = mediaController!!.currentMediaItem?.localConfiguration?.uri.toString(),
//                            album = mediaMetadata.albumTitle.toString(),
//                            albumId = mediaMetadata.extras?.getLong("album_id") ?: 0,
//                            size = mediaMetadata.extras?.getLong("size") ?: 0,
//                            duration = mediaMetadata.durationMs ?: 0,
//                            lyrics = lyricsParser.parseLyrics(
//                                mediaMetadata.extras?.getString("path") ?: "No path found!",
//                            ),
//                            audioSessionAudio = mediaController!!.sessionExtras.getInt(
//                                "audioSessionId",
//                                0
//                            )
//                        )
//                    }
//                }
//            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                viewModelScope.launch {
                    delay(100)
                    allTracks.value.fastFirstOrNull { track ->
                        track.mediaItem == mediaItem
                    }.also {
                        it?.let { track ->
                            _musicState.update { state ->
                                state.copy(
                                    track = track,
//                                    title = track.title,
//                                    artist = track.artist,
//                                    album = track.album,
//                                    mediaId = track.mediaId,
//                                    artistId = track.artistId,
//                                    art = track.artUri,
//                                    path = track.path,
//                                    uri = track.uri.toString(),
//                                    albumId = track.albumId,
//                                    size = track.size,
//                                    duration = track.durationMs,
                                    lyrics = lyricsParser.parseLyrics(track.path),
                                    audioSessionAudio = mediaController!!.sessionExtras.getInt("audioSessionId", 0),
                                    mediaIndex = mediaController!!.currentMediaItemIndex
                                )
                            }
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

                        viewModelScope.launch {
                            saveSavedPosition(application, musicState.value.position)
                            saveSavedMediaId(application, musicState.value.track.mediaId)
                        }

                        _musicState.update { MusicState() }
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

//            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
//                super.onTimelineChanged(timeline, reason)
//
//                viewModelScope.launch(Dispatchers.IO) {
//                    val allTracksList = allTracks.first()
//                    val trackMap = allTracksList.associateBy { it.mediaItem.mediaId }
//                    val loadedMedias = (0 until timeline.windowCount).mapNotNull { index ->
//                        val currentMediaItem = timeline.getWindow(index, Timeline.Window())
//
//                        val mediaId = currentMediaItem.mediaItem.mediaId
//                        trackMap[mediaId]
//                    }
//                    _musicState.update {
//                        it.copy(
//                            loadedMedias = loadedMedias
//                        )
//                    }
//                }
//            }

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
                        viewModelScope.launch {
                            allTracks.collectLatest { mediaItems ->
                                mediaController!!.replaceMediaItems(
                                    0,
                                    mediaController!!.mediaItemCount,
                                    mediaItems.fastMap { it.mediaItem }
                                )
                            }
                        }
                    },
                    MoreExecutors.directExecutor()
                )
                loadPlaybackPreferences()
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


    private fun loadPlaybackPreferences() {
        viewModelScope.launch {
            val repeatMode = getRepeatMode(application)
            val shouldShuffle = getShouldShuffle(application)
            val pitch = getPitch(application)
            val speed = getSpeed(application)
            val position = getSavedPosition(application)
            val mediaId = getSavedMediaId(application)

            mediaController!!.changeRepeatMode(repeatMode)
            mediaController!!.applyShuffle(shouldShuffle)
            mediaController!!.applyPlaybackSpeed(speed)
            mediaController!!.applyPlaybackPitch(pitch)

            (0 until mediaController!!.mediaItemCount).firstOrNull { index ->
                mediaController!!.getMediaItemAt(index).mediaId == mediaId
            }?.let { mediaIndex ->
                mediaController!!.prepare()
                mediaController!!.seekTo(mediaIndex, position)
                _musicState.update {
                    it.copy(track = allTracks.value[mediaIndex])
                }
            }
        }
    }

    private fun savePlaybackPreferences() {
        val repeatMode = musicState.value.repeatMode
        val shuffle = musicState.value.shuffle
        val speed = musicState.value.speed
        val pitch = musicState.value.pitch
        val mediaId = musicState.value.track.mediaId
        val position = musicState.value.position


        runBlocking {
            saveRepeatMode(application, repeatMode)
            saveShouldShuffle(application, shuffle)
            saveSpeed(application, speed)
            savePitch(application, pitch)
            saveSavedPosition(application, position)
            saveSavedMediaId(application, mediaId)
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

        savePlaybackPreferences()
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
                if (!action.tracks.equalsIgnoreOrder(musicState.value.loadedMedias)) {
                    _musicState.update {
                        it.copy(
                            loadedMedias = action.tracks
                        )
                    }
                    mediaController!!.clearMediaItems()
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
                val exists = musicState.value.loadedMedias.fastAny { it.mediaId == action.cuteTrack.mediaId }
                if (!exists) {
                    mediaController!!.addMediaItem(MediaItem.fromUri(action.cuteTrack.uri))
                }
            }
        }
    }
}