@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberShouldApplyLoop
import com.sosauce.cutemusic.data.datastore.rememberShouldApplyShuffle
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.rememberAnimatable
import com.sosauce.cutemusic.utils.thenIf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoopButton() {

    val rotation = rememberAnimatable()
    val scope = rememberCoroutineScope()
    var shouldLoop by rememberShouldApplyLoop()


    IconButton(
        onClick = {
            shouldLoop = !shouldLoop
            scope.launch(Dispatchers.Main) {
                rotation.animateTo(
                    targetValue = -360f,
                    animationSpec = tween(1000)
                )
                // TODO : can I make the icon always rotate without this ???
                rotation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(0)
                )
            }
        },
    ) {
        Icon(
            imageVector = Icons.Rounded.Loop,
            contentDescription = "loop button",
            modifier = Modifier.rotate(rotation.value),
            tint = if (shouldLoop) MaterialTheme.colorScheme.primary else LocalContentColor.current,
        )
    }
}

@Composable
fun ShuffleButton() {
    var shouldShuffle by rememberShouldApplyShuffle()

    IconButton(
        onClick = { shouldShuffle = !shouldShuffle }
    ) {
        Icon(
            imageVector = Icons.Rounded.Shuffle,
            contentDescription = "shuffle button",
            tint = if (shouldShuffle) MaterialTheme.colorScheme.primary else LocalContentColor.current,
        )
    }
}

@Composable
fun SharedTransitionScope.ActionButtonsRow(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    val scope = rememberCoroutineScope()
    val previousButtonSharedState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_PREVIOUS_BUTTON)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val skipPreviousOffset = rememberAnimatable()
        IconButton(
            onClick = {
                if (musicState.position >= 10000) {
                    onHandlePlayerActions(PlayerActions.RestartSong)
                } else onHandlePlayerActions(PlayerActions.SeekToPreviousMusic)
                scope.launch {
                    skipPreviousOffset.animateTo(
                        targetValue = -25f,
                        animationSpec = tween(400)
                    )
                    skipPreviousOffset.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(400)
                    )
                }
            },
            modifier = Modifier.size(60.dp)
        ) {
            AnimatedContent(musicState.position >= 10000) {
                Icon(
                    imageVector = if (it) Icons.Rounded.Replay else Icons.Rounded.SkipPrevious,
                    contentDescription = null,
                    modifier = Modifier
                        .thenIf(!it) {
                            sharedElement(
                                sharedContentState = previousButtonSharedState,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }
                        .size(40.dp)
                        .offset {
                            IntOffset(
                                x = skipPreviousOffset.value.toInt(),
                                y = 0
                            )
                        }
                )
            }
        }

        val fastRewindOffset = rememberAnimatable()
        IconButton(
            onClick = {
                onHandlePlayerActions(PlayerActions.RewindTo(5000))
                scope.launch {
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
            onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) },
            modifier = Modifier.size(60.dp)
        ) {
            Crossfade(
                targetState = musicState.isPlaying,
                animationSpec = tween(200)
            ) { targetState ->
                Icon(
                    imageVector = if (targetState) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.PLAY_PAUSE_BUTTON),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .size(40.dp)

                )
            }
        }
        val fastForwardOffset = rememberAnimatable()
        IconButton(
            onClick = {
                onHandlePlayerActions(PlayerActions.SeekTo(5000))
                scope.launch {
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
        val skipNextOffset = rememberAnimatable()
        IconButton(
            onClick = {
                onHandlePlayerActions(PlayerActions.SeekToNextMusic)
                scope.launch {
                    skipNextOffset.animateTo(
                        targetValue = 25f,
                        animationSpec = tween(400)
                    )
                    skipNextOffset.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(400)
                    )
                }
            },
            modifier = Modifier.size(60.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.SkipNext,
                contentDescription = null,
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_NEXT_BUTTON),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .size(40.dp)
                    .offset {
                        IntOffset(
                            x = skipNextOffset.value.toInt(),
                            y = 0
                        )
                    }
            )
        }
    }
}