package com.sosauce.cutemusic

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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.getAlbums
import com.sosauce.cutemusic.audio.getMusics
import com.sosauce.cutemusic.audio.service.PlayerService
import com.sosauce.cutemusic.logic.Nav
import com.sosauce.cutemusic.logic.PreferencesKeys
import com.sosauce.cutemusic.logic.dataStore
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import com.sosauce.cutemusic.ui.theme.DarkAmoledColorPalette
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {


    private lateinit var player: Player

    private val _mediaItems = mutableStateOf<List<Music>>(emptyList())
    private val mediaItems: List<Music>
        get() = _mediaItems.value

    private val _albums = mutableStateOf<List<Album>>(emptyList())
    private val albums: List<Album>
        get() = _albums.value

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            preparePlaylist()
        }
    }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val showSplash = mutableStateOf(true)

        installSplashScreen().setKeepOnScreenCondition{ showSplash.value }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // delete this when landscape and state bugs are fixed

        val sessionToken = SessionToken(applicationContext, ComponentName(this, PlayerService::class.java))
        val mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            player = mediaControllerFuture.get()
            showSplash.value = false
            setContent {
                CuteMusicTheme {
                    requestPermission()
                    val context = LocalContext.current
                    val isSysDark = isSystemInDarkTheme()
                    val dataStore = context.dataStore
                    val _useDarkMode = dataStore.data.map { preferences ->
                        preferences[PreferencesKeys.USE_DARK_MODE] ?: isSysDark
                    }

                    val useDarkMode by _useDarkMode.collectAsState(initial = false)
                    val _useAmoledMode = dataStore.data.map { preferences ->
                        preferences[PreferencesKeys.USE_AMOLED_MODE]
                    }

                    val useAmoledMode by _useAmoledMode.collectAsState(initial = false)

                    val colors = when {
                        useAmoledMode == true-> DarkAmoledColorPalette
                        useDarkMode == true  -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                dynamicDarkColorScheme(context)
                            } else {
                                darkColorScheme()
                            }
                        }
                        else -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                dynamicLightColorScheme(context)
                            } else {
                                lightColorScheme()
                            }
                        }
                    }



                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentColor = MaterialTheme.colorScheme.background
                    ) {
                        MaterialTheme(
                            colorScheme = colors,
                            content = {
                                Nav(player, mediaItems, MusicViewModel(player), albums)
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
        requestPermissionLauncher.launch(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        })
    }

    private fun preparePlaylist() {
        lifecycleScope.launch {
            _mediaItems.value = getMusics(contentResolver)
            _mediaItems.value.forEach { music ->
                val mediaItem = convertMusicToMedia(music)
                player.addMediaItem(mediaItem)
            }
            _albums.value = getAlbums(contentResolver)
            }
            player.prepare()
        }
    }



fun convertMusicToMedia(music: Music): MediaItem {
    return MediaItem.Builder()
        .setUri(music.uri)
        .setMediaId(music.uri.toString())
        .build()
}

