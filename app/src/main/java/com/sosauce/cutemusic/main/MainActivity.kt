package com.sosauce.cutemusic.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sosauce.cutemusic.ui.navigation.Nav
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        ActivityCompat.requestPermissions(
            this,
            permission,
            0
        )

        setContent {
            CuteMusicTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { _ ->
                        Nav()
                }
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        sendBroadcast(
            Intent(
                "CM_CUR_PLAY_CHANGED"
            ).apply {
                putExtra("currentlyPlaying", "")
            }
        )
    }
}

