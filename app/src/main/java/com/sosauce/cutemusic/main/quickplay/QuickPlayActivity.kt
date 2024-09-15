package com.sosauce.cutemusic.main.quickplay

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.screens.playing.components.MusicSlider
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import org.koin.androidx.compose.koinViewModel

class QuickPlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            CuteMusicTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) { _ ->
                    MaterialTheme {
                        var uri by remember { mutableStateOf<Uri?>(null) }
                        val vm = koinViewModel<MusicViewModel>()

                        when {
                            intent?.action == Intent.ACTION_SEND -> {
                                if (intent?.type?.startsWith("audio/") == true) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        (intent.getParcelableExtra(
                                            Intent.EXTRA_STREAM,
                                            Uri::class.java
                                        )).let { intentUri ->
                                            uri = intentUri
                                        }
                                    } else (intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri).let { intentUri ->
                                        uri = intentUri
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CuteText(
                                text = vm.currentlyPlaying,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 20.sp,
                                modifier = Modifier.basicMarquee()


                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            CuteText(
                                text = vm.currentArtist,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                                fontSize = 14.sp,
                                modifier = Modifier.basicMarquee()
                            )

                            MusicSlider(vm)
                            Spacer(modifier = Modifier.height(7.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FloatingActionButton(
                                    onClick = {
                                        if (!vm.isPlaylistEmpty()) vm.quickPlay(uri) else vm.handlePlayerActions(
                                            PlayerActions.PlayOrPause
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (vm.isCurrentlyPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                        contentDescription = "pause/play button"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}