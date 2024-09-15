@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.playing

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.screens.playing.components.LoopButton
import com.sosauce.cutemusic.ui.screens.playing.components.MusicSlider
import com.sosauce.cutemusic.ui.screens.playing.components.ShuffleButton
import com.sosauce.cutemusic.ui.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel

@Composable
fun SharedTransitionScope.NowPlayingLandscape(
    viewModel: MusicViewModel,
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    NPLContent(
        viewModel = viewModel,
        onEvent = viewModel::handlePlayerActions,
        onNavigateUp = navController::navigateUp,
        onClickLoop = { viewModel.setLoop(it) },
        onClickShuffle = { viewModel.setShuffle(it) },
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@Composable
private fun SharedTransitionScope.NPLContent(
    viewModel: MusicViewModel,
    onNavigateUp: () -> Unit,
    onEvent: (PlayerActions) -> Unit,
    onClickLoop: (Boolean) -> Unit,
    onClickShuffle: (Boolean) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var showSpeedCard by remember { mutableStateOf(false) }

    if (showSpeedCard) SpeedCard(
        viewModel = viewModel,
        onDismiss = { showSpeedCard = false }
    )

    Scaffold { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(45.dp)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = viewModel.currentArt,
                    stringResource(R.string.artwork),
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(10)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onNavigateUp() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )
                        {
                            CuteText(
                                text = viewModel.currentlyPlaying,

                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 30.sp
                            )
                        }
                    }
                    CuteText(
                        text = viewModel.currentArtist,

                        color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                        fontSize = 16.sp
                    )
                    MusicSlider(
                        viewModel = viewModel
                    )
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        ShuffleButton(
                            onClick = { onClickShuffle(it) }
                        )
                        IconButton(
                            onClick = { onEvent(PlayerActions.SeekToPreviousMusic) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SkipPrevious,
                                contentDescription = null,
                                modifier = Modifier.sharedElement(
                                    state = rememberSharedContentState(key = "skipPreviousButton"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ ->
                                        tween(durationMillis = 500)
                                    }
                                )
                            )
                        }
                        IconButton(
                            onClick = { onEvent(PlayerActions.RewindTo(5000)) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FastRewind,
                                contentDescription = null
                            )
                        }

                        FloatingActionButton(
                            onClick = { onEvent(PlayerActions.PlayOrPause) }
                        ) {
                            Icon(
                                imageVector = if (viewModel.isCurrentlyPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = "play/pause button"
                            )
                        }
                        IconButton(
                            onClick = { onEvent(PlayerActions.SeekTo(5000)) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FastForward,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = { onEvent(PlayerActions.SeekToNextMusic) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SkipNext,
                                contentDescription = null,
                                modifier = Modifier.sharedElement(
                                    state = rememberSharedContentState(key = "skipNextButton"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ ->
                                        tween(durationMillis = 500)
                                    }
                                )
                            )
                        }
                        LoopButton(
                            onClick = { onClickLoop(it) }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        IconButton(onClick = { showSpeedCard = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Speed,
                                contentDescription = "change speed"
                            )
                        }
                    }
                }

            }
        }
    }
}