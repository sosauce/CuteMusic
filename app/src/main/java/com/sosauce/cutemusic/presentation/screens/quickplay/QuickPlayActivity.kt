@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.presentation.screens.quickplay

import android.net.Uri
import android.os.Bundle
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import coil3.toBitmap
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberNpArtShape
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.screens.playing.components.CuteSlider
import com.sosauce.cutemusic.presentation.screens.playing.components.TitleAndArtist
import com.sosauce.cutemusic.presentation.theme.CuteMusicTheme
import com.sosauce.cutemusic.utils.rememberInteractionSource
import com.sosauce.cutemusic.utils.toShape
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
class QuickPlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        val uri = intent?.data ?: return

        setContent {
            val viewModel = koinViewModel<QuickPlayViewModel>(
                parameters = { parametersOf(uri) }
            )
            var artImageBitmap by remember { mutableStateOf(ImageBitmap(1, 1)) }


            CuteMusicTheme(artImageBitmap = artImageBitmap) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->

                    val state by viewModel.musicState.collectAsStateWithLifecycle()
                    val context = LocalContext.current
                    val interactionSources = List(5) { rememberInteractionSource() }
                    val artShape by rememberNpArtShape()



                    if (!viewModel.isSongLoaded) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ContainedLoadingIndicator()
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
                                    shapes = IconButtonDefaults.shapes(),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                                    ),
                                    modifier = Modifier
                                        .size(
                                            IconButtonDefaults.smallContainerSize(
                                                IconButtonDefaults.IconButtonWidthOption.Wide
                                            )
                                        )
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.close),
                                        contentDescription = null
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
                                        .clip(artShape.toShape()),
                                    contentScale = ContentScale.Crop,
                                    onSuccess = { state ->
                                        artImageBitmap =
                                            state.result.image.toBitmap().asImageBitmap()
                                    }
                                )
                            }
                            TitleAndArtist(musicState = state)
                            Spacer(Modifier.height(24.dp))
                            CuteSlider(
                                musicState = state,
                                onHandlePlayerActions = viewModel::handlePlayerAction
                            )
                            Spacer(Modifier.height(10.dp))
                            ButtonGroup {
                                FilledIconButton(
                                    onClick = {
                                        viewModel.handlePlayerAction(
                                            PlayerActions.RewindTo(5000)
                                        )
                                    },
                                    shapes = IconButtonDefaults.shapes(),
                                    interactionSource = interactionSources[1],
                                    modifier = Modifier
                                        .weight(1f)
                                        .size(
                                            IconButtonDefaults.mediumContainerSize(
                                                IconButtonDefaults.IconButtonWidthOption.Wide
                                            )
                                        )
                                        .animateWidth(interactionSource = interactionSources[1])
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.fast_rewind),
                                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
                                    )
                                }
                                FilledIconToggleButton(
                                    checked = state.isPlaying,
                                    onCheckedChange = {
                                        viewModel.handlePlayerAction(
                                            PlayerActions.PlayOrPause
                                        )
                                    },
                                    shapes = IconButtonDefaults.toggleableShapes(),
                                    interactionSource = interactionSources[2],
                                    modifier = Modifier
                                        .weight(1f)
                                        .size(
                                            IconButtonDefaults.mediumContainerSize(
                                                IconButtonDefaults.IconButtonWidthOption.Wide
                                            )
                                        )
                                        .animateWidth(interactionSource = interactionSources[2])
                                ) {
                                    Icon(
                                        painter = if (state.isPlaying) painterResource(R.drawable.widget_pause) else painterResource(
                                            R.drawable.widget_play
                                        ),
                                        contentDescription = if (state.isPlaying) stringResource(
                                            androidx.media3.session.R.string.media3_controls_pause_description
                                        ) else stringResource(androidx.media3.session.R.string.media3_controls_play_description),
                                    )
                                }
                                FilledIconButton(
                                    onClick = {
                                        viewModel.handlePlayerAction(
                                            PlayerActions.SeekTo(5000)
                                        )
                                    },
                                    shapes = IconButtonDefaults.shapes(),
                                    interactionSource = interactionSources[3],
                                    modifier = Modifier
                                        .weight(1f)
                                        .size(
                                            IconButtonDefaults.mediumContainerSize(
                                                IconButtonDefaults.IconButtonWidthOption.Narrow
                                            )
                                        )
                                        .animateWidth(interactionSource = interactionSources[3])
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.fast_forward),
                                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
                                    )
                                }
                            }
                            Spacer(Modifier.weight(1f))
                            ToggleButton(
                                checked = state.repeatMode != Player.REPEAT_MODE_OFF,
                                onCheckedChange = {
                                    viewModel.handlePlayerAction(
                                        PlayerActions.ChangeRepeatMode
                                    )
                                },
                                colors = ToggleButtonDefaults.toggleButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {

                                val icon = when (state.repeatMode) {
                                    Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ALL -> R.drawable.repeat
                                    else -> R.drawable.repeat_one
                                }

                                Icon(
                                    painter = painterResource(icon),
                                    contentDescription = "repeat mode"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}