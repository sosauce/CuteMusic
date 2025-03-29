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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastFilter
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
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.getPitch
import com.sosauce.cutemusic.data.datastore.getShouldLoop
import com.sosauce.cutemusic.data.datastore.getShouldShuffle
import com.sosauce.cutemusic.data.datastore.getSpeed
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.domain.repository.SafManager
import com.sosauce.cutemusic.main.PlaybackService
import com.sosauce.cutemusic.utils.applyLoop
import com.sosauce.cutemusic.utils.applyPlaybackPitch
import com.sosauce.cutemusic.utils.applyPlaybackSpeed
import com.sosauce.cutemusic.utils.applyShuffle
import com.sosauce.cutemusic.utils.playAtIndex
import com.sosauce.cutemusic.utils.playFromAlbum
import com.sosauce.cutemusic.utils.playFromArtist
import com.sosauce.cutemusic.utils.playRandom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException

class MusicViewModel(
    private val application: Application,
    private val mediaStoreHelper: MediaStoreHelper,
    private val safManager: SafManager
) : AndroidViewModel(application) {

    private var mediaController: MediaController? by mutableStateOf(null)

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    var sleepCountdownTimer: CountDownTimer? = null


    val allTracks = mediaStoreHelper.fetchLatestMusics()
        .combine(safManager.fetchLatestSafTracks()) { localMusics, safMusics ->
            localMusics + safMusics
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            mediaStoreHelper.musics
        )

    val safTracks = safManager.fetchLatestSafTracks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val albums = mediaStoreHelper.fetchLatestAlbums().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val artists = mediaStoreHelper.fetchLatestArtists().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val folders = mediaStoreHelper.fetchLatestFoldersWithMusics().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _loadedMedias = MutableStateFlow<List<MediaItem>>(emptyList())
    val loadedMedias = _loadedMedias.asStateFlow()

    private val _albumSongs = MutableStateFlow<List<MediaItem>>(emptyList())
    val albumSongs: StateFlow<List<MediaItem>> = _albumSongs.asStateFlow()

    private val _artistAlbums = MutableStateFlow<List<Album>>(emptyList())
    val artistAlbums: StateFlow<List<Album>> = _artistAlbums.asStateFlow()

    private val _artistSongs = MutableStateFlow<List<MediaItem>>(emptyList())
    val artistSongs: StateFlow<List<MediaItem>> = _artistSongs.asStateFlow()


    private val playerListener = @UnstableApi
    object : Player.Listener {
        @UnstableApi
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            _musicState.update {
                it.copy(
                    title = mediaMetadata.title.toString(),
                    artist = mediaMetadata.artist.toString(),
                    mediaId = mediaMetadata.extras?.getString("mediaId")
                        ?: (System.currentTimeMillis().toString()),
                    artistId = mediaMetadata.extras?.getLong("artist_id") ?: 0,
                    art = mediaMetadata.artworkUri,
                    path = mediaMetadata.extras?.getString("path") ?: "No path found!",
                    uri = mediaMetadata.extras?.getString("uri") ?: "No uri found!",
                    album = mediaMetadata.albumTitle.toString(),
                    albumId = mediaMetadata.extras?.getLong("album_id") ?: 0,
                    size = mediaMetadata.extras?.getLong("size") ?: 0,
                    duration = mediaMetadata.durationMs ?: 0,
                )
            }
            parseLyrics()

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

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            viewModelScope.launch {
                while (player.isPlaying) {
                    _musicState.update {
                        it.copy(
                            duration = player.duration,
                            position = player.currentPosition
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

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            _musicState.update {
                it.copy(
                    mediaIndex = mediaController!!.currentMediaItemIndex
                )
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
                        if (mediaController!!.mediaItemCount == 0) {
                            viewModelScope.launch {
                                allTracks.collectLatest { mediaItems ->
                                    mediaController!!.setMediaItems(mediaItems)


                                    val list = mutableListOf<MediaItem>()

                                    for (i in 0 until mediaController!!.mediaItemCount) {
                                        list.add(mediaController!!.getMediaItemAt(i))
                                    }
                                    _loadedMedias.update { list }
                                }
                            }
                        }

                        viewModelScope.launch {
                            getShouldLoop(application).collectLatest { shouldLoop ->
                                mediaController!!.applyLoop(shouldLoop)
                            }
                        }
                        viewModelScope.launch {
                            getShouldShuffle(application).collectLatest { shouldShuffle ->
                                mediaController!!.applyShuffle(shouldShuffle)
                            }
                        }
                        viewModelScope.launch {
                            getSpeed(application).collectLatest { speed ->
                                mediaController!!.applyPlaybackSpeed(speed)
                            }
                        }
                        viewModelScope.launch {
                            getPitch(application).collectLatest { pitch ->
                                mediaController!!.applyPlaybackPitch(pitch)
                            }
                        }
                    },
                    MoreExecutors.directExecutor()
                )

            }
    }


    private fun getLrcFile(): File? {
        val lrcFilePath = musicState.value.path.replaceAfterLast('.', "lrc")
        val lrcFile = File(lrcFilePath)
        return if (lrcFile.exists()) lrcFile else null
    }

    private val _lyrics = MutableStateFlow<List<Lyrics>>(emptyList())
    val lyrics: StateFlow<List<Lyrics>> = _lyrics.asStateFlow()

    fun parseLyrics() {
        val file = getLrcFile()
        val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2})]""")

        viewModelScope.launch(Dispatchers.IO) {
            val newLyrics = file?.bufferedReader()?.useLines { lines ->
                lines.mapNotNull { line ->
                    regex.find(line)?.let { matchResult ->
                        val (minutes, seconds, hundredths) = matchResult.destructured
                        val millis =
                            minutes.toLong() * 60_000 + seconds.toLong() * 1000 + hundredths.toLong() * 10
                        val lyric = line.substring(matchResult.range.last + 1).trim()
                        Lyrics(millis, lyric)
                    }
                }.toList()
            } ?: loadEmbeddedLyrics().lineSequence().map { line ->
                regex.find(line).let { matchResult ->
                    if (matchResult != null) {
                        val (minutes, seconds, hundredths) = matchResult.destructured
                        val millis =
                            minutes.toLong() * 60_000 + seconds.toLong() * 1000 + hundredths.toLong() * 10
                        val lyric = line.substring(matchResult.range.last + 1).trim()
                        Lyrics(millis, lyric)
                    } else {
                        // Since there's no ambiguity to what embedded lyrics could contain, lines not following the .lrc format will be positioned at the beginning
                        Lyrics(0, line)
                    }
                }
            }.toList()
            _lyrics.value = newLyrics
        }
    }

    private fun loadEmbeddedLyrics(): String {
        val fd = getFileDescriptorFromPath(application, musicState.value.path)
        return fd?.dup()?.detachFd()?.let {
            TagLib.getMetadata(it)?.propertyMap?.get("LYRICS")?.getOrNull(0)
                ?: ""
        } ?: ""

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
            is PlayerActions.PlayOrPause -> if (mediaController!!.isPlaying) mediaController!!.pause() else mediaController!!.play()
            is PlayerActions.SeekToNextMusic -> mediaController!!.seekToNextMediaItem()
            is PlayerActions.SeekToPreviousMusic -> mediaController!!.seekToPreviousMediaItem()
            is PlayerActions.SeekTo -> mediaController!!.seekTo(mediaController!!.currentPosition + action.position)
            is PlayerActions.SeekToSlider -> mediaController!!.seekTo(action.position)
            is PlayerActions.RewindTo -> mediaController!!.seekTo(mediaController!!.currentPosition - action.position)
            is PlayerActions.StopPlayback -> mediaController!!.stop()
            is PlayerActions.SeekToMusicIndex -> mediaController!!.seekTo(action.index, 0)
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
                viewModelScope.launch {
                    allTracks.collectLatest { mediaItems ->
                        if (mediaController!!.mediaItemCount != mediaItems.size) {
                            mediaController!!.setMediaItems(mediaItems)
                            mediaController!!.playAtIndex(action.mediaId)
                        } else {
                            mediaController!!.playAtIndex(action.mediaId)
                        }
                    }
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
                    (action.hours * 60 * 60 * 1000L) + (action.minutes * 60 * 1000L)

                // Cancel any active timer before setting a new one
                sleepCountdownTimer?.cancel()
                sleepCountdownTimer = null

                sleepCountdownTimer = object : CountDownTimer(totalTimeMillis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {

                        // Only set it once, no need to use resources just to set it to true again and again
                        if (!musicState.value.sleepTimerActive) {
                            _musicState.update {
                                it.copy(
                                    sleepTimerActive = true
                                )
                            }
                        }
                    }

                    override fun onFinish() {
                        mediaController!!.pause()
                        cancel()
                        sleepCountdownTimer = null
                        _musicState.update {
                            it.copy(
                                sleepTimerActive = false
                            )
                        }
                    }
                }
                sleepCountdownTimer?.start()
            }
        }
    }

    fun loadAlbumSongs(album: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _albumSongs.update {
                allTracks.value.fastFilter { it.mediaMetadata.albumTitle.toString() == album }
            }
        }
    }

    fun loadArtistSongs(artistName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _artistSongs.update {
                allTracks.value.fastFilter { it.mediaMetadata.artist == artistName }
            }
        }

    }

    fun loadArtistAlbums(artistName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _artistAlbums.update {
                albums.value.fastFilter { it.artist == artistName }
            }
        }
    }

    fun deleteMusic(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        viewModelScope.launch {
            mediaStoreHelper.deleteMusics(
                uris,
                intentSenderLauncher
            )
        }
    }


    fun editMusic(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        viewModelScope.launch {
            mediaStoreHelper.editMusic(
                uris,
                intentSenderLauncher
            )
        }
    }


}
