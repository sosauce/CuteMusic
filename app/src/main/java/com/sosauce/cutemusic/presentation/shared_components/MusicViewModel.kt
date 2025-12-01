@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.cutemusic.presentation.shared_components

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.os.CountDownTimer
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.cutemusic.data.LyricsParser
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.data.datastore.getMediaIndexToMediaIdMap
import com.sosauce.cutemusic.data.datastore.getPauseOnMute
import com.sosauce.cutemusic.data.datastore.getPitch
import com.sosauce.cutemusic.data.datastore.getRepeatMode
import com.sosauce.cutemusic.data.datastore.getShouldShuffle
import com.sosauce.cutemusic.data.datastore.getSpeed
import com.sosauce.cutemusic.data.datastore.saveMediaIndexToMediaIdMap
import com.sosauce.cutemusic.data.datastore.savePitch
import com.sosauce.cutemusic.data.datastore.saveRepeatMode
import com.sosauce.cutemusic.data.datastore.saveShouldShuffle
import com.sosauce.cutemusic.data.datastore.saveSpeed
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.models.toCuteTrack
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.PlaybackService
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.domain.repository.SafManager
import com.sosauce.cutemusic.utils.LastPlayed
import com.sosauce.cutemusic.utils.applyPlaybackPitch
import com.sosauce.cutemusic.utils.applyPlaybackSpeed
import com.sosauce.cutemusic.utils.applyShuffle
import com.sosauce.cutemusic.utils.changeRepeatMode
import com.sosauce.cutemusic.utils.playFromAlbum
import com.sosauce.cutemusic.utils.playFromAll
import com.sosauce.cutemusic.utils.playFromArtist
import com.sosauce.cutemusic.utils.playFromFolder
import com.sosauce.cutemusic.utils.playFromPlaylist
import com.sosauce.cutemusic.utils.playRandom
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration

class MusicViewModel(
    private val application: Application,
    private val mediaStoreHelper: MediaStoreHelper,
    private val safManager: SafManager,
    private val lyricsParser: LyricsParser
) : AndroidViewModel(application) {

    private var mediaController: MediaController? = null
    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()


    var sleepCountdownTimer: CountDownTimer? = null


    private val allTracks = combine(
        mediaStoreHelper.fetchLatestMusics(),
        safManager.fetchLatestSafTracks()
    ) { a, b -> a + b }

    val safTracks = safManager.fetchLatestSafTracks()
        .map { it.fastMap { item -> item.toCuteTrack() } }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    private val playerListener =
        object : Player.Listener {

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                viewModelScope.launch {
                    _musicState.update {
                        it.copy(
                            title = mediaMetadata.title?.toString() ?: "Nothing playing!",
                            artist = mediaMetadata.artist?.toString() ?: "No artist!",
                            mediaId = mediaController!!.currentMediaItem?.mediaId ?: "",
                            artistId = mediaMetadata.extras?.getLong("artist_id") ?: 0,
                            art = mediaMetadata.artworkUri,
                            path = mediaMetadata.extras?.getString("path") ?: "No path found!",
                            uri = mediaController!!.currentMediaItem?.localConfiguration?.uri.toString(),
                            album = mediaMetadata.albumTitle.toString(),
                            albumId = mediaMetadata.extras?.getLong("album_id") ?: 0,
                            size = mediaMetadata.extras?.getLong("size") ?: 0,
                            duration = mediaMetadata.durationMs ?: 0,
                            lyrics = lyricsParser.parseLyrics(
                                mediaMetadata.extras?.getString("path") ?: "No path found!",
                            )
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

                        viewModelScope.launch {
                            saveMediaIndexToMediaIdMap(
                                pair = LastPlayed("", 0),
                                context = application
                            )
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

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                _musicState.update {
                    it.copy(
                        mediaIndex = mediaController!!.currentMediaItemIndex
                    )
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

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)

                val list = mutableListOf<CuteTrack>()

                (0 until timeline.windowCount).map { index ->
                    list.add(timeline.getWindow(index, Timeline.Window()).mediaItem.toCuteTrack())
                }

                _musicState.update {
                    it.copy(
                        loadedMedias = list
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

                        _musicState.update {
                            it.copy(
                                audioSessionAudio = mediaController!!.sessionExtras.getInt(
                                    "audioSessionId",
                                    0
                                )
                            )
                        }

                        viewModelScope.launch {
                            allTracks.collectLatest { mediaItems ->
                                mediaController!!.replaceMediaItems(
                                    0,
                                    mediaController!!.mediaItemCount,
                                    mediaItems
                                )
                            }
                        }
                    },
                    MoreExecutors.directExecutor()
                )
                loadPlaybackPreferences()
            }

        viewModelScope.launch {
            getPauseOnMute(application.applicationContext).collectLatest { pauseOnMute ->
                if (pauseOnMute) {
                    observeIsMuted().collectLatest { isMute ->
                        if (isMute) mediaController!!.pause()
                    }
                }
            }

        }
    }


    private fun loadPlaybackPreferences() {
        viewModelScope.launch {
            val repeatMode = getRepeatMode(application.applicationContext).first()
            val shouldShuffle = getShouldShuffle(application.applicationContext).first()
            val speed = getSpeed(application.applicationContext).first()
            val pitch = getPitch(application.applicationContext).first()
            val (id, position) = getMediaIndexToMediaIdMap(application).first()

            mediaController!!.changeRepeatMode(repeatMode)
            mediaController!!.applyShuffle(shouldShuffle)
            mediaController!!.applyPlaybackSpeed(speed)
            mediaController!!.applyPlaybackPitch(pitch)


            val index = (0 until mediaController!!.mediaItemCount).firstOrNull { i ->
                mediaController!!.getMediaItemAt(i).mediaId == id
            } ?: -1

            if (index != -1) {
                mediaController!!.prepare()
                mediaController!!.seekTo(index, position)
            }

        }
    }

    private fun savePlaybackPreferences() {
        val repeatMode = musicState.value.repeatMode
        val shuffle = musicState.value.shuffle
        val speed = musicState.value.speed
        val pitch = musicState.value.pitch
        val mediaId = musicState.value.mediaId
        val position = musicState.value.position

        runBlocking {
            saveRepeatMode(application.applicationContext, repeatMode)
            saveShouldShuffle(application.applicationContext, shuffle)
            saveSpeed(application.applicationContext, speed)
            savePitch(application.applicationContext, pitch)

            saveMediaIndexToMediaIdMap(
                pair = LastPlayed(mediaId, position),
                context = application.applicationContext
            )

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

        application.applicationContext.registerReceiver(
            receiver,
            IntentFilter("android.media.STREAM_MUTE_CHANGED_ACTION")
        )
        awaitClose { application.applicationContext.unregisterReceiver(receiver) }

    }

    override fun onCleared() {
        super.onCleared()

        savePlaybackPreferences()
        mediaController!!.removeListener(playerListener)
        mediaController!!.release()
    }

    fun handleMediaItemActions(action: MediaItemActions) {
        when (action) {
            is MediaItemActions.DeleteMediaItem -> {
                viewModelScope.launch {
                    mediaStoreHelper.deleteMusics(
                        action.uri,
                        action.activityResultLauncher
                    )
                }
            }

            is MediaItemActions.ShareMediaItem -> {

                val shareIntent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, action.uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = "audio/*"
                }

                application.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        null
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            }
        }
    }


    fun handlePlayerActions(action: PlayerActions) {
        when (action) {
            is PlayerActions.RestartSong -> mediaController!!.seekTo(0)
            is PlayerActions.PlayRandom -> mediaController!!.playRandom()
            is PlayerActions.PlayOrPause -> if (mediaController!!.isPlaying) mediaController!!.pause() else mediaController!!.play()
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
            is PlayerActions.StartAlbumPlayback -> {
                viewModelScope.launch {
                    mediaController!!.playFromAlbum(
                        action.albumName,
                        action.mediaId,
                        allTracks.first()
                    )
                }
            }

            is PlayerActions.StartArtistPlayback -> {
                viewModelScope.launch {
                    mediaController!!.playFromArtist(
                        action.artistName,
                        action.mediaId,
                        allTracks.first()
                    )
                }
            }

            is PlayerActions.StartFolderPlayback -> {
                viewModelScope.launch {
                    mediaController!!.playFromFolder(
                        action.folder,
                        allTracks.first()
                    )
                }
            }

            is PlayerActions.StartPlaylistPlayback -> {
                viewModelScope.launch {
                    mediaController!!.playFromPlaylist(
                        action.playlistSongsId,
                        action.mediaId,
                        allTracks.first()
                    )
                }
            }

            is PlayerActions.StartPlayback -> {
                viewModelScope.launch {
                    mediaController!!.playFromAll(
                        mediaId = action.mediaId,
                        tracks = allTracks.first()
                    )
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

            is PlayerActions.ReArrangeQueue -> mediaController!!.moveMediaItem(
                action.from,
                action.to
            )

            is PlayerActions.RemoveFromQueue -> {
                val index = (0 until mediaController!!.mediaItemCount).first {
                    mediaController!!.getMediaItemAt(it).mediaId == action.mediaId
                }
                mediaController!!.removeMediaItem(index)
            }

            is PlayerActions.AddToQueue -> {
                val exists = (0 until mediaController!!.mediaItemCount).any {
                    mediaController!!.getMediaItemAt(it).mediaId == action.cuteTrack.mediaId
                }
                if (!exists) {
                    mediaController!!.addMediaItem(MediaItem.fromUri(action.cuteTrack.uri))
                }
            }


        }
    }

    fun editMusic(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            mediaStoreHelper.editMusic(
                uris,
                intentSenderLauncher
            )
        }
    }
}