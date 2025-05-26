package com.sosauce.cutemusic.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.sosauce.cutemusic.data.datastore.rememberAppTheme
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.ui.navigation.Nav
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import com.sosauce.cutemusic.utils.CuteTheme

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
        try {
            val c = Playlist::class.java
            Log.d("FIELDS_DEBUG", "Fields in Playlist: ${c.declaredFields.joinToString { it.name }}")
        } catch (e: Exception) {
            Log.e("FIELDS_DEBUG", "Error getting fields", e)
        }
        setContent {
            val theme by rememberAppTheme()
            var artImageBitmap by remember { mutableStateOf<ImageBitmap>(ImageBitmap(1, 1)) }
            val isSystemInDarkTheme = isSystemInDarkTheme()

            CuteMusicTheme(artImageBitmap = artImageBitmap) {

                WindowCompat
                    .getInsetsController(window, window.decorView)
                    .apply {

                        val isLight = if (theme == CuteTheme.SYSTEM) !isSystemInDarkTheme else theme == CuteTheme.LIGHT

                        isAppearanceLightStatusBars = isLight
                        isAppearanceLightNavigationBars = isLight
                    }

                Scaffold { _ ->
                    Nav { imageBitmap ->
                        artImageBitmap = imageBitmap
                    }
                }
            }
        }
    }


//    override fun onDestroy() {
//        super.onDestroy()
//        sendBroadcast(
//            Intent(
//                "CM_CUR_PLAY_CHANGED"
//            ).apply {
//                putExtra("currentlyPlaying", "")
//            }
//        )
//    }
}

