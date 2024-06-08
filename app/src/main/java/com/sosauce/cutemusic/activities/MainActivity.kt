package com.sosauce.cutemusic.activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.cutemusic.audio.MediaStoreHelper
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.service.PlaybackService
import com.sosauce.cutemusic.logic.navigation.Nav
import com.sosauce.cutemusic.logic.rememberIsLoopEnabled
import com.sosauce.cutemusic.logic.rememberIsShuffleEnabled
import com.sosauce.cutemusic.screens.utils.rememberHasReadAudioPermissions
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

	private lateinit var player: Player
	private lateinit var controllerFuture: ListenableFuture<MediaController>

	private val _mediaItems = MutableStateFlow<List<Music>>(emptyList())
	private val mediaItems = _mediaItems.map { it.toImmutableList() }
		.stateIn(
			scope = lifecycleScope,
			started = SharingStarted.Eagerly,
			initialValue = persistentListOf()
		)

	private var _isPlaylistLoaded = MutableStateFlow(false)

	@SuppressLint("SourceLockedOrientationActivity")
	override fun onCreate(savedInstanceState: Bundle?) {

		installSplashScreen()
			.setKeepOnScreenCondition { !::player.isInitialized }

		super.onCreate(savedInstanceState)

		enableEdgeToEdge(
			statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
			navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
		)

		requestedOrientation =
			ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // delete this when landscape and state bugs are fixed

		val sessionToken = SessionToken(
			applicationContext,
			ComponentName(this, PlaybackService::class.java)
		)

		controllerFuture = MediaController.Builder(this, sessionToken)
			.buildAsync()

		controllerFuture.addListener({
			player = controllerFuture.get()
			setContent {
				CuteMusicTheme {
					val viewModel = viewModel<MusicViewModel>(
						factory = MusicViewModel.create(player, contentResolver)
					)

					val isLoaded by _isPlaylistLoaded.collectAsStateWithLifecycle()
					val hasAudioPermission = rememberHasReadAudioPermissions()

					LaunchedEffect(hasAudioPermission, isLoaded) {
						if (!hasAudioPermission || isLoaded) return@LaunchedEffect
						if (!isLoaded) {
							preparePlaylist()
							_isPlaylistLoaded.update { true }
						}
					}

					val shouldLoop by rememberIsLoopEnabled()
					val shouldShuffle by rememberIsShuffleEnabled()

					LaunchedEffect(shouldLoop) {
						player.repeatMode = if (shouldLoop) Player.REPEAT_MODE_ONE
						else Player.REPEAT_MODE_OFF
					}
					LaunchedEffect(shouldShuffle) {
						player.shuffleModeEnabled = shouldShuffle
					}

					val musics by mediaItems.collectAsStateWithLifecycle()

					Nav(
						musics = musics,
						viewModel = viewModel
					)
				}
			}
		}, MoreExecutors.directExecutor())
	}


	override fun onDestroy() {
		player.release()
		MediaController.releaseFuture(controllerFuture)
		super.onDestroy()
	}

	private fun preparePlaylist() {
		lifecycleScope.launch {
			val musics = MediaStoreHelper.getMusics(contentResolver)
			_mediaItems.update { (it + musics).distinctBy { it.id } }
			loadSongsInBatchesToPlayer(musics, batchSize = 10, delayMillis = 10)
		}
	}


	private suspend fun loadSongsInBatchesToPlayer(
		musics: List<Music>,
		batchSize: Int,
		delayMillis: Long
	) {
		for (i in musics.indices step batchSize) {
			val batch = musics.subList(i, minOf(i + batchSize, musics.size))
			val mediaItems = batch.map(Music::toMediaItem)
			player.addMediaItems(mediaItems)
			player.prepare()
			delay(delayMillis)
		}
	}
}

fun Music.toMediaItem(): MediaItem = MediaItem.Builder()
	.setUri(uri)
	.setMediaId(uri.toString())
	.build()


