@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.utils.thenIf

@Composable
fun SharedTransitionScope.CuteSearchbar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit = {},
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    currentlyPlaying: String,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    isPlaying: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isPlaylistEmpty: Boolean,
    onNavigate: () -> Unit
) = CustomSearchbar(
    query = query,
    onQueryChange = onQueryChange,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    placeholder = placeholder,
    modifier = modifier,
    currentlyPlaying = currentlyPlaying,
    onHandlePlayerActions = onHandlePlayerActions,
    isPlaying = isPlaying,
    animatedVisibilityScope = animatedVisibilityScope,
    isPlaylistEmpty = isPlaylistEmpty,
    onNavigate = onNavigate
)


@Composable
private fun SharedTransitionScope.CustomSearchbar(
    query: String,
    onQueryChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    modifier: Modifier,
    currentlyPlaying: String,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    isPlaying: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isPlaylistEmpty: Boolean,
    onNavigate: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val roundedShape = 24.dp

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(roundedShape)
            )
            .clip(RoundedCornerShape(roundedShape))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(roundedShape)
            )
            .thenIf(
                isPlaylistEmpty,
                Modifier.clickable {
                    onNavigate()
                }
            )
    ) {
        AnimatedVisibility(
            visible = isPlaylistEmpty,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it })
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(key = "arrow"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ ->
                                    tween(durationMillis = 500)
                                }
                            )
                    )
                    CuteText(
                        text = currentlyPlaying,
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .sharedElement(
                                state = rememberSharedContentState(key = "currentlyPlaying"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ ->
                                    tween(durationMillis = 500)
                                }
                            )
                            .basicMarquee()
                    )
                }
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) }) {
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
                    IconButton(onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.sharedElement(
                                state = rememberSharedContentState(key = "playPauseIcon"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ ->
                                    tween(durationMillis = 500)
                                }
                            )
                        )
                    }
                    IconButton(onClick = {
                        onHandlePlayerActions(PlayerActions.SeekToNextMusic)
                    }) {
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
                }
            }
        }
        TextField(
            value = query,
            onValueChange = onQueryChange,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
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





