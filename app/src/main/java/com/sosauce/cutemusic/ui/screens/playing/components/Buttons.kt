@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import android.renderscript.RenderScript
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.utils.CuteIconButton
import com.sosauce.cutemusic.utils.thenIf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoopButton(
    onClick: (Boolean) -> Unit,
    isLooping: Boolean
) {
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    IconButton(
        onClick = {
            onClick(!isLooping)
            scope.launch(Dispatchers.IO) {
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
        }
    ) {
        Icon(
            imageVector = Icons.Rounded.Loop,
            contentDescription = "loop button",
            tint = if (isLooping) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.rotate(rotation.value)
        )
    }
}

@Composable
fun ShuffleButton(
    onClick: (Boolean) -> Unit,
    isShuffling: Boolean
) {


    IconButton(
        onClick = {
            onClick(!isShuffling)
        }
    ) {
        Icon(
            imageVector = Icons.Rounded.Shuffle,
            contentDescription = "shuffle button",
            tint = if (isShuffling) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SharedTransitionScope.ActionsButtonsRow(
    onClickLoop: (Boolean) -> Unit,
    onClickShuffle: (Boolean) -> Unit,
    viewModel: MusicViewModel,
    onEvent: (PlayerActions) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {


    val leftIconOffsetX = remember { Animatable(0f) }
    val rightIconOffsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var showLongPressMenuPlus by remember { mutableStateOf(false) }
    var showLongPressMenuMinus by remember { mutableStateOf(false) }
    val colorMinus by animateColorAsState(
        targetValue = if (showLongPressMenuMinus) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.background,
        label = "",
        animationSpec = tween(300)
    )
    val colorPlus by animateColorAsState(
        targetValue = if (showLongPressMenuPlus) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.background,
        label = "",
        animationSpec = tween(300)
    )

    val roundedFAB by animateIntAsState(
        targetValue = if (viewModel.isCurrentlyPlaying) 30 else 50,
        label = "FAB Shape"
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(end = 5.dp)
                .background(
                    color = colorMinus,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Crossfade(
                targetState = showLongPressMenuMinus,
                label = ""
            ) {
                if (it) {
                    IconButton(
                        onClick = {
                            onEvent(PlayerActions.RewindTo(3000))
                            showLongPressMenuMinus = false
                        }
                    ) { CuteText("-3") }
                } else {
                    ShuffleButton(
                        onClick = onClickShuffle,
                        isShuffling = viewModel.isShuffling
                    )
                }
            }

            Crossfade(
                targetState = showLongPressMenuMinus,
                label = ""
            ) {
                if (it) {
                    IconButton(
                        onClick = {
                            onEvent(PlayerActions.RewindTo(5000))
                            showLongPressMenuMinus = false
                        }
                    ) { CuteText("-5") }
                } else {
                    IconButton(
                        onClick = {
                            if (viewModel.currentPosition >= 10000) {
                                onEvent(PlayerActions.RestartSong)
                            } else {
                                onEvent(PlayerActions.SeekToPreviousMusic)
                            }
                            scope.launch(Dispatchers.IO) {
                                leftIconOffsetX.animateTo(
                                    targetValue = -20f,
                                    animationSpec = tween(250)
                                )
                                leftIconOffsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(250)
                                )
                            }
                        }
                    ) {
                        Crossfade(
                            targetState = viewModel.currentPosition >= 10000,
                            label = ""
                        ) {
                            if (!it) {
                                Icon(
                                    imageVector = Icons.Rounded.SkipPrevious,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .offset {
                                            IntOffset(
                                                x = leftIconOffsetX.value.toInt(),
                                                y = 0
                                            )
                                        }
                                        .sharedElement(
                                            state = rememberSharedContentState(key = "skipPreviousButton"),
                                            animatedVisibilityScope = animatedVisibilityScope,
                                            boundsTransform = { _, _ ->
                                                tween(durationMillis = 500)
                                            }
                                        )
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.RestartAlt,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }

            Crossfade(
                targetState = showLongPressMenuMinus,
                label = ""
            ) {
                if (it) {
                    IconButton(
                        onClick = {
                            onEvent(PlayerActions.RewindTo(10000))
                            showLongPressMenuMinus = false
                        }
                    ) { CuteText("-10") }
                } else {
                    CuteIconButton (
                        onClick = { onEvent(PlayerActions.RewindTo(5000)) },
                        onLongClick = { showLongPressMenuMinus = true }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FastRewind,
                            contentDescription = null
                        )
                    }
                }
            }
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
        Row(
            modifier = Modifier
                .padding(start = 5.dp)
                .background(
                    color = colorPlus,
                    shape = RoundedCornerShape(24.dp)

                )
        ) {

            Crossfade(
                targetState = showLongPressMenuPlus,
                label = ""
            ) {
                if (it) {
                    IconButton(
                        onClick = {
                            onEvent(PlayerActions.SeekTo(3000))
                            showLongPressMenuPlus = false
                        }
                    ) { CuteText("+3") }
                } else {
                    CuteIconButton(
                        onClick = { onEvent(PlayerActions.SeekTo(5000)) },
                        onLongClick = { showLongPressMenuPlus = true }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FastForward,
                            contentDescription = null
                        )
                    }
                }
            }

            Crossfade(
                targetState = showLongPressMenuPlus,
                label = ""
            ) {
                if (it) {
                    IconButton(
                        onClick = {
                            onEvent(PlayerActions.SeekTo(5000))
                            showLongPressMenuPlus = false
                        }
                    ) { CuteText("+5") }
                } else {
                    IconButton(
                        onClick = {
                            onEvent(PlayerActions.SeekToNextMusic)
                            scope.launch(Dispatchers.IO) {
                                rightIconOffsetX.animateTo(
                                    targetValue = 20f,
                                    animationSpec = tween(250)
                                )
                                rightIconOffsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(250)
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = null,
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        x = rightIconOffsetX.value.toInt(),
                                        y = 0
                                    )
                                }
                                .sharedElement(
                                    state = rememberSharedContentState(key = "skipNextButton"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ ->
                                        tween(durationMillis = 500)
                                    }
                                )
                        )
                    }
                }
            }

            Crossfade(
                targetState = showLongPressMenuPlus,
                label = ""
            ) {
                if (it) {
                    IconButton(
                        onClick = {
                            onEvent(PlayerActions.SeekTo(10000))
                            showLongPressMenuPlus = false
                        }
                    ) { CuteText("+10") }
                } else {
                    LoopButton(
                        onClick = onClickLoop,
                        isLooping = viewModel.isLooping
                    )
                }
            }
        }
    }
}