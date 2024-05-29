@file:Suppress("UNCHECKED_CAST")

package com.sosauce.cutemusic.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.cutemusic.audio.MediaStoreHelper
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.service.PlaybackService
import com.sosauce.cutemusic.logic.navigation.Nav
import com.sosauce.cutemusic.logic.rememberIsLoopEnabled
import com.sosauce.cutemusic.logic.rememberIsShuffleEnabled
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {


    private lateinit var player: Player

    private val _mediaItems = mutableStateOf<List<Music>>(emptyList())
    private val mediaItems: List<Music>
        get() = _mediaItems.value

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                preparePlaylist()
            }
        }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition { !::player.isInitialized }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // delete this when landscape and state bugs are fixed

        val sessionToken =
            SessionToken(applicationContext, ComponentName(this, PlaybackService::class.java))
        val mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            player = mediaControllerFuture.get()
            setContent {
                CuteMusicTheme {
                    val viewModel = viewModel<MusicViewModel>(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return MusicViewModel(player, contentResolver) as T
                            }
                        }
                    )
                    requestPermission()

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        val shouldLoop by rememberIsLoopEnabled()
                        val shouldShuffle by rememberIsShuffleEnabled()

                        LaunchedEffect(shouldLoop) {
                            player.repeatMode =
                                if (shouldLoop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                        }
                        LaunchedEffect(shouldShuffle) {
                            player.shuffleModeEnabled = shouldShuffle
                        }

                        MaterialTheme(
                            content = {
                                Nav(
                                    musics = mediaItems,
                                    viewModel = viewModel
                                )
                            }
                        )
                    }
                }
            }
        }, MoreExecutors.directExecutor())
    }


    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }


    private fun requestPermission() {
        requestPermissionLauncher.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )
    }

    private fun preparePlaylist() {
        lifecycleScope.launch {
            val musics = MediaStoreHelper.getMusics(contentResolver)
            loadSongsInBatches(musics, batchSize = 10, delayMillis = 10)
        }
    }


    private suspend fun loadSongsInBatches(musics: List<Music>, batchSize: Int, delayMillis: Long) {
        for (i in musics.indices step batchSize) {
            val batch = musics.subList(i, minOf(i + batchSize, musics.size))
            _mediaItems.value += batch
            batch.forEach { music ->
                val mediaItem = convertMusicToMedia(music)
                player.addMediaItem(mediaItem)
            }
            player.prepare()
            delay(delayMillis)
        }
    }
}


fun convertMusicToMedia(music: Music): MediaItem {
    return MediaItem.Builder()
        .setUri(music.uri)
        .setMediaId(music.uri.toString())
        .build()
}

