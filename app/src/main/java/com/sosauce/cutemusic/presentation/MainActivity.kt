package com.sosauce.cutemusic.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.sosauce.cutemusic.data.datastore.rememberAppTheme
import com.sosauce.cutemusic.presentation.navigation.Nav
import com.sosauce.cutemusic.presentation.theme.CuteMusicTheme
import com.sosauce.cutemusic.utils.CuteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val theme by rememberAppTheme()
            var artImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
            val isSystemInDarkTheme = isSystemInDarkTheme()

            CuteMusicTheme(artImageBitmap = artImageBitmap) {

                WindowCompat
                    .getInsetsController(window, window.decorView)
                    .apply {

                        val isLight =
                            if (theme == CuteTheme.SYSTEM) !isSystemInDarkTheme else theme == CuteTheme.LIGHT

                        isAppearanceLightStatusBars = isLight
                        isAppearanceLightNavigationBars = isLight
                    }
                Nav { imageBitmap ->
                    artImageBitmap = imageBitmap
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