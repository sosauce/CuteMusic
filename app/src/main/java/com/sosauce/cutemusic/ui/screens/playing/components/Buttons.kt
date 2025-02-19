@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)

package com.sosauce.cutemusic.ui.screens.playing.components

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.window.Popup
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberShouldApplyLoop
import com.sosauce.cutemusic.data.datastore.rememberShouldApplyShuffle
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.CuteIconButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoopButton() {

    val rotation = remember { Animatable(0f) }
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
            tint = if (shouldLoop) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.rotate(rotation.value)
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
            tint = if (shouldShuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
fun ActionButtonsRowV2(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val scope = rememberCoroutineScope()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val skipPreviousOffset = remember { Animatable(0f) }
            IconButton(
                onClick = {
                    onHandlePlayerActions(PlayerActions.SeekToPreviousMusic)
                    scope.launch(Dispatchers.Default) {
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
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .offset {
                            IntOffset(
                                x = skipPreviousOffset.value.toInt(),
                                y = 0
                            )
                        }
                )
            }

            val fastRewindOffset = remember { Animatable(0f) }
            IconButton(
                onClick = {
                    onHandlePlayerActions(PlayerActions.RewindTo(5000))
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
                onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) },
                modifier = Modifier.size(60.dp)
            ) {
                Crossfade(
                    targetState = musicState.isCurrentlyPlaying,
                    animationSpec = tween(200)
                ) { targetState ->
                    Icon(
                        imageVector = if (targetState) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            val fastForwardOffset = remember { Animatable(0f) }
            IconButton(
                onClick = {
                    onHandlePlayerActions(PlayerActions.SeekTo(5000))
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
            val skipNextOffset = remember { Animatable(0f) }
            IconButton(
                onClick = {
                    onHandlePlayerActions(PlayerActions.SeekToNextMusic)
                    scope.launch(Dispatchers.Default) {
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

@Composable
fun SharedTransitionScope.ActionsButtonsRow(
    onHandlePlayerActions: (PlayerActions) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    musicState: MusicState
) {
    val leftIconOffsetX = remember { Animatable(0f) }
    val rightIconOffsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val roundedFAB by animateIntAsState(
        targetValue = if (musicState.isCurrentlyPlaying) 30 else 50,
        label = "FAB Shape"
    )
    var showPlusPopup by remember { mutableStateOf(false) }
    var showMinusPopup by remember { mutableStateOf(false) }


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
        ) {
            if (showMinusPopup) {
                Popup(
                    onDismissRequest = { showMinusPopup = false }
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(50.dp)
                            )
                    ) {
                        IconButton(
                            onClick = { onHandlePlayerActions(PlayerActions.RewindTo(10000)) }
                        ) {
                            CuteText("-10")
                        }
                        IconButton(
                            onClick = { onHandlePlayerActions(PlayerActions.RewindTo(5000)) }
                        ) {
                            CuteText("-5")
                        }
                        IconButton(
                            onClick = { onHandlePlayerActions(PlayerActions.RewindTo(3000)) }
                        ) {
                            CuteText("-3")
                        }
                    }
                }
            }
            ShuffleButton()
            IconButton(
                onClick = {
                    if (musicState.currentPosition >= 10000) {
                        onHandlePlayerActions(PlayerActions.RestartSong)
                    } else {
                        onHandlePlayerActions(PlayerActions.SeekToPreviousMusic)
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
            CuteIconButton(
                onClick = { onHandlePlayerActions(PlayerActions.RewindTo(5000)) },
                onLongClick = { showMinusPopup = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.FastRewind,
                    contentDescription = null
                )
            }
        }
        FloatingActionButton(
            onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) },
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
        ) {
            CuteIconButton(
                onClick = { onHandlePlayerActions(PlayerActions.SeekTo(5000)) },
                onLongClick = { showPlusPopup = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.FastForward,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = {
                    onHandlePlayerActions(PlayerActions.SeekToNextMusic)
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
            LoopButton()

            if (showPlusPopup) {
                Popup(
                    onDismissRequest = { showPlusPopup = false }
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(50.dp)
                            )
                    ) {
                        IconButton(
                            onClick = { onHandlePlayerActions(PlayerActions.SeekTo(3000)) }
                        ) {
                            CuteText("+3")
                        }
                        IconButton(
                            onClick = { onHandlePlayerActions(PlayerActions.SeekTo(5000)) }
                        ) {
                            CuteText("+5")
                        }
                        IconButton(
                            onClick = { onHandlePlayerActions(PlayerActions.SeekTo(10000)) }
                        ) {
                            CuteText("+10")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionsButtonsRowQuickPlay(
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
                    ShuffleButton()
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
                    LoopButton()
                }
            }
        }
    }
}