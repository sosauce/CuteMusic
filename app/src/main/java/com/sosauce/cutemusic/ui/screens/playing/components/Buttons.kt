@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.MusicState
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberShouldApplyLoop
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.CuteIconButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoopButton(
    onClick: () -> Unit,
    isLooping: Boolean
) {

    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var shouldApplyLoop by rememberShouldApplyLoop()

    IconButton(
        onClick = {
            shouldApplyLoop = !isLooping
            onClick()
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
            Log.d("Looping2", shouldApplyLoop.toString())

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
    onClick: () -> Unit,
    isShuffling: Boolean
) {


    IconButton(
        onClick = onClick
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
    onClickLoop: () -> Unit,
    onClickShuffle: () -> Unit,
    onEvent: (PlayerActions) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    musicState: MusicState
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
        targetValue = if (musicState.isCurrentlyPlaying) 30 else 50,
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
                        isShuffling = musicState.isShuffling
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
                            if (musicState.currentPosition >= 10000) {
                                onEvent(PlayerActions.RestartSong)
                            } else {
                                onEvent(PlayerActions.SeekToPreviousMusic)
                            }
                            scope.launch(Dispatchers.Main) {
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
                            targetState = musicState.currentPosition >= 10000,
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
                                            animatedVisibilityScope = animatedVisibilityScope
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
                    CuteIconButton(
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
                imageVector = if (musicState.isCurrentlyPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = "pause/play button",
                modifier = Modifier.sharedElement(
                    state = rememberSharedContentState(key = "playPauseIcon"),
                    animatedVisibilityScope = animatedVisibilityScope
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
                            scope.launch(Dispatchers.Main) {
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
                                    animatedVisibilityScope = animatedVisibilityScope
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
                        isLooping = musicState.isLooping
                    )
                }
            }
        }
    }
}

@Composable
fun ActionsButtonsRowQuickPlay(
    onClickLoop: () -> Unit,
    onClickShuffle: () -> Unit,
    onEvent: (PlayerActions) -> Unit,
    musicState: MusicState
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
        targetValue = if (musicState.isCurrentlyPlaying) 30 else 50,
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
                        isShuffling = musicState.isShuffling
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
                            if (musicState.currentPosition >= 10000) {
                                onEvent(PlayerActions.RestartSong)
                            } else {
                                onEvent(PlayerActions.SeekToPreviousMusic)
                            }
                            scope.launch(Dispatchers.Main) {
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
                            targetState = musicState.currentPosition >= 10000,
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
                    CuteIconButton(
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
                imageVector = if (musicState.isCurrentlyPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = "pause/play button",
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
                            scope.launch(Dispatchers.Main) {
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
                        isLooping = musicState.isLooping
                    )
                }
            }
        }
    }
}