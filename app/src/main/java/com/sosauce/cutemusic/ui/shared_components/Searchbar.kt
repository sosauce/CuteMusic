@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberShowXButton
import com.sosauce.cutemusic.utils.thenIf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SharedTransitionScope.CuteSearchbar(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    currentlyPlaying: String,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    isPlaying: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isPlayerReady: Boolean,
    onNavigate: () -> Unit = {},
    showSearchField: Boolean = true,
    onClickFAB: () -> Unit
) {

    val focusManager = LocalFocusManager.current
    val roundedShape = remember { 24.dp }
    val leftIconOffsetX = remember { Animatable(0f) }
    val rightIconOffsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var showXButton by rememberShowXButton()

    Column(
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = onClickFAB,
            modifier = Modifier
                .defaultMinSize(
                    minWidth = 45.dp,
                    minHeight = 45.dp
                )
                .align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Rounded.Shuffle,
                contentDescription = null
            )
        }
        Spacer(Modifier.height(5.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(roundedShape))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(roundedShape)
                )
                .thenIf(
                    isPlayerReady,
                    Modifier.clickable {
                        onNavigate()
                    }
                )
        ) {
            AnimatedVisibility(
                visible = isPlayerReady,
                enter = fadeIn() + slideInVertically(
                    animationSpec = tween(500),
                    initialOffsetY = { it }
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (showXButton) {
                            IconButton(
                                onClick = { onHandlePlayerActions(PlayerActions.StopPlayback) },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = null,
                                )
                            }
                        }
                        CuteText(
                            text = currentlyPlaying,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .sharedElement(
                                    state = rememberSharedContentState(key = "currentlyPlaying"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ ->
                                        tween(500)
                                    }
                                )
                                .basicMarquee()

                        )
                    }
                    Row {
                        IconButton(
                            onClick = {
                                onHandlePlayerActions(PlayerActions.SeekToPreviousMusic)
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
                                            tween(500)
                                        }
                                    )
                            )
                        }
                        IconButton(
                            onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.sharedElement(
                                    state = rememberSharedContentState(key = "playPauseIcon"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ ->
                                        tween(500)
                                    }
                                )
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
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        boundsTransform = { _, _ ->
                                            tween(500)
                                        }
                                    )
                            )
                        }
                    }
                }
            }
            if (showSearchField) {
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                            0.5f
                        ),
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                            0.5f
                        ),
                        disabledIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(50.dp),
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    placeholder = placeholder,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )

                )
            }
        }
    }
}






