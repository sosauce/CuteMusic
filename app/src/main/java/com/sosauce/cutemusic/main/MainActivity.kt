package com.sosauce.cutemusic.main

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.ui.navigation.Nav
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val app by lazy { application as App }
    private val mediaStoreHelper by inject<MediaStoreHelper>()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            //if (it) {  }
        }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            CuteMusicTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
//                        val shouldLoop by rememberIsLoopEnabled()
//                        val shouldShuffle by rememberIsShuffleEnabled()
//
//                        LaunchedEffect(shouldLoop) {
//                            mediaController!!.repeatMode =
//                                if (shouldLoop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
//                        }
//                        LaunchedEffect(shouldShuffle) {
//                            mediaController!!.shuffleModeEnabled = shouldShuffle
//                        }

                    MaterialTheme(
                        content = {
                            Nav(app = app, mediaStoreHelper = mediaStoreHelper)
                            requestPermission()
                        }
                    )
                }
            }
        }
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
}