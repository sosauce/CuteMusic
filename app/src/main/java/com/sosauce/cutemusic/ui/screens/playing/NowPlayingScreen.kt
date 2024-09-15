@file:kotlin.OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.playing


import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.KeyboardArrowDown
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.ui.screens.playing.components.LoopButton
import com.sosauce.cutemusic.ui.screens.playing.components.MusicSlider
import com.sosauce.cutemusic.ui.screens.playing.components.ShuffleButton
import com.sosauce.cutemusic.ui.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.utils.ImageUtils


@OptIn(UnstableApi::class)
@Composable
fun SharedTransitionScope.NowPlayingScreen(
    navController: NavController,
    viewModel: MusicViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    if (rememberIsLandscape()) {
        NowPlayingLandscape(
            viewModel = viewModel,
            navController = navController,
            animatedVisibilityScope = animatedVisibilityScope
        )
    } else {
        NowPlayingContent(
            viewModel = viewModel,
            onEvent = viewModel::handlePlayerActions,
            onNavigateUp = navController::navigateUp,
            onClickLoop = { viewModel.setLoop(it) },
            onClickShuffle = { viewModel.setShuffle(it) },
            animatedVisibilityScope = animatedVisibilityScope
        )
    }

}

@Composable
private fun SharedTransitionScope.NowPlayingContent(
    viewModel: MusicViewModel,
    onEvent: (PlayerActions) -> Unit,
    onNavigateUp: () -> Unit,
    onClickLoop: (Boolean) -> Unit,
    onClickShuffle: (Boolean) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    var showSpeedCard by remember { mutableStateOf(false) }
    val roundedFAB by animateIntAsState(
        targetValue = if (viewModel.isCurrentlyPlaying) 30 else 50, label = "FAB Shape"
    )



    if (showSpeedCard) {
        SpeedCard(
            viewModel = viewModel,
            onDismiss = { showSpeedCard = false }
        )
    }
    Scaffold(
        modifier = Modifier.padding(15.dp)
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { onNavigateUp() }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(key = "arrow"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ ->
                                    tween(durationMillis = 500)
                                }
                            )
                            .size(28.dp)
                    )
                }
            }
            AsyncImage(
                model = ImageUtils.imageRequester(
                    img = viewModel.currentArt,
                    context = context
                ),
                contentDescription = stringResource(R.string.artwork),
                modifier = Modifier
                    .size(340.dp)
                    .clip(RoundedCornerShape(5)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    CuteText(
                        text = viewModel.currentlyPlaying,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(key = "currentlyPlaying"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ ->
                                    tween(durationMillis = 500)
                                }
                            )
                            .basicMarquee()


                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    CuteText(
                        text = viewModel.currentArtist,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                        fontSize = 14.sp,
                        modifier = Modifier.basicMarquee()
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            MusicSlider(
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(7.dp))
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
                    onClick = { onEvent(PlayerActions.PlayOrPause) },
                    shape = RoundedCornerShape(roundedFAB)
                ) {
                    Icon(
                        imageVector = if (viewModel.isCurrentlyPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "pause/play button",
                        modifier = Modifier.sharedElement(
                            state = rememberSharedContentState(key = "playPauseIcon"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                tween(durationMillis = 500)
                            }
                        )
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
                    .navigationBarsPadding()
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







