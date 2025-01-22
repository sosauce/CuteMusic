@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.main.quickplay

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.screens.playing.components.ActionsButtonsRowQuickPlay
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

            val viewModel = koinViewModel<MusicViewModel>()
            CuteMusicTheme(musicViewModel = viewModel) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) { _ ->
                    MaterialTheme {
                        var uri by remember { mutableStateOf<Uri?>(null) }
                        val musicState by viewModel.musicState.collectAsStateWithLifecycle()


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

                        LaunchedEffect(Unit) {
                            viewModel.handlePlayerActions(
                                PlayerActions.QuickPlay(uri!!)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CuteText(
                                text = musicState.currentlyPlaying,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 20.sp,
                                modifier = Modifier.basicMarquee()


                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            CuteText(
                                text = musicState.currentArtist,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                                fontSize = 14.sp,
                                modifier = Modifier.basicMarquee()
                            )

                            MusicSlider(
                                viewModel = viewModel,
                                musicState = musicState
                            )
                            Spacer(modifier = Modifier.height(7.dp))
                            ActionsButtonsRowQuickPlay(
                                onClickLoop = { viewModel.handlePlayerActions(PlayerActions.ApplyLoop) },
                                onClickShuffle = { viewModel.handlePlayerActions(PlayerActions.ApplyShuffle) },
                                onEvent = { viewModel.handlePlayerActions(it) },
                                musicState = musicState
                            )
                        }

                    }
                }
            }
        }
    }
}