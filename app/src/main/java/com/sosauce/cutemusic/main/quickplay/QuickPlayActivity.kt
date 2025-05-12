@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.main.quickplay

import android.net.Uri
import android.os.Bundle
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.toBitmap
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.screens.playing.components.CuteSlider
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import com.sosauce.cutemusic.utils.formatToReadableTime
import com.sosauce.cutemusic.utils.rememberAnimatable
import com.sosauce.cutemusic.utils.rememberInteractionSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.saket.squiggles.SquigglySlider
import org.koin.androidx.compose.koinViewModel

class QuickPlayActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        val uri = intent?.data ?: Uri.EMPTY

        setContent {
            val viewModel = koinViewModel<QuickPlayViewModel>()
            var artImageBitmap by remember { mutableStateOf<ImageBitmap>(ImageBitmap(1, 1)) }

            LaunchedEffect(uri) {
                viewModel.loadSong(uri)
            }

            CuteMusicTheme(artImageBitmap = artImageBitmap) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->

                    val state by viewModel.musicState.collectAsStateWithLifecycle()
                    val context = LocalContext.current

                    if (!viewModel.isSongLoaded) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            CuteText(stringResource(R.string.song_loading))
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(paddingValues)
                                .padding(horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 10.dp,
                                        top = 10.dp
                                    ),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                IconButton(
                                    onClick = { Process.killProcess(Process.myPid()) },
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .wrapContentSize()
                            ) {
                                AsyncImage(
                                    model = remember { viewModel.loadAlbumArt(context, uri) },
                                    contentDescription = stringResource(R.string.artwork),
                                    modifier = Modifier
                                        .fillMaxSize(0.9f)
                                        .clip(RoundedCornerShape(5)),
                                    contentScale = ContentScale.Crop,
                                    onSuccess = { state ->
                                        artImageBitmap = state.result.image.toBitmap().asImageBitmap()
                                    }
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                CuteText(
                                    text = state.title,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 25.sp,
                                    modifier = Modifier.basicMarquee()
                                )
                                CuteText(
                                    text = state.artist,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 20.sp,
                                    modifier = Modifier.basicMarquee()
                                )
                            }
                            Spacer(Modifier.height(24.dp))
                            CuteSlider(
                                musicState = state,
                                onHandlePlayerActions = viewModel::handlePlayerAction
                            )
                            Spacer(Modifier.height(10.dp))
                            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer) {
                                val scope = rememberCoroutineScope()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val fastRewindOffset = rememberAnimatable()
                                    IconButton(
                                        onClick = {
                                            viewModel.handlePlayerAction(PlayerActions.RewindTo(5000))
                                            scope.launch(Dispatchers.Default) {
                                                fastRewindOffset.animateTo(
                                                    targetValue = -25f,
                                                    animationSpec = tween(400)
                                                )
                                                fastRewindOffset.animateTo(
                                                    targetValue = 0f,
                                                    animationSpec = tween(400)
                                                )
                                            }
                                        },
                                        modifier = Modifier.size(60.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.FastRewind,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(35.dp)
                                                .offset {
                                                    IntOffset(
                                                        x = fastRewindOffset.value.toInt(),
                                                        y = 0
                                                    )
                                                }
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.handlePlayerAction(PlayerActions.PlayOrPause) },
                                        modifier = Modifier.size(60.dp)
                                    ) {
                                        Crossfade(
                                            targetState = state.isPlaying,
                                            animationSpec = tween(200)
                                        ) { targetState ->
                                            Icon(
                                                imageVector = if (targetState) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }
                                    val fastForwardOffset = rememberAnimatable()
                                    IconButton(
                                        onClick = {
                                            viewModel.handlePlayerAction(PlayerActions.SeekTo(5000))
                                            scope.launch(Dispatchers.Default) {
                                                fastForwardOffset.animateTo(
                                                    targetValue = 25f,
                                                    animationSpec = tween(400)
                                                )
                                                fastForwardOffset.animateTo(
                                                    targetValue = 0f,
                                                    animationSpec = tween(400)
                                                )
                                            }
                                        },
                                        modifier = Modifier.size(60.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.FastForward,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(35.dp)
                                                .offset {
                                                    IntOffset(
                                                        x = fastForwardOffset.value.toInt(),
                                                        y = 0
                                                    )
                                                }
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
}