@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)

package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberShowShuffleButton
import com.sosauce.cutemusic.data.datastore.rememberShowXButton
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.utils.CurrentScreen
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SharedTransitionScope.CuteSearchbar(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    currentlyPlaying: String = "",
    onHandlePlayerActions: (PlayerActions) -> Unit = {},
    isPlaying: Boolean = false,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isPlayerReady: Boolean = true,
    onNavigate: (Screen) -> Unit = {},
    showSearchField: Boolean = true,
    fab: @Composable (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
) {

    val focusManager = LocalFocusManager.current
    val leftIconOffsetX = remember { Animatable(0f) }
    val rightIconOffsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val showXButton by rememberShowXButton()
    val showShuffleButton by rememberShowShuffleButton()
    val screenToPlaceholder = remember {
        hashMapOf(
            Screen.Main.toString() to R.string.search_tracks,
            Screen.Albums.toString() to R.string.search_albums,
            Screen.Artists.toString() to R.string.search_artists,
            Screen.Playlists.toString() to R.string.search_playlists,
        )
    }
    val screenToLeadingIcon = remember {
        hashMapOf(
            Screen.Main.toString() to R.drawable.music_note_rounded,
            Screen.Albums.toString() to androidx.media3.session.R.drawable.media3_icon_album,
            Screen.Artists.toString() to R.drawable.artist_rounded,
            Screen.Playlists.toString() to R.drawable.queue_music_rounded,
        )
    }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth(rememberSearchbarMaxFloatValue())
            .padding(end = rememberSearchbarRightPadding())
            .imePadding()

    ) {
        Row(
            horizontalArrangement = if (navigationIcon != null) Arrangement.SpaceBetween else Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            navigationIcon?.invoke()
            if (showShuffleButton) {
                fab?.invoke()
            }
        }
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable(
                    enabled = isPlayerReady
                ) { onNavigate(Screen.NowPlaying) }
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
                                .basicMarquee()
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "currentlyPlaying"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
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
                                        animatedVisibilityScope = animatedVisibilityScope
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
                                    animatedVisibilityScope = animatedVisibilityScope
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
                                        animatedVisibilityScope = animatedVisibilityScope
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
                    leadingIcon = {
                        var screenSelectionExpanded by remember { mutableStateOf(false) }

                        IconButton(
                            onClick = {
                                screenSelectionExpanded = true
//                                if (!hasSeenTip) {
//                                    hasSeenTip = true
//                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    screenToLeadingIcon[CurrentScreen.screen]
                                        ?: R.drawable.music_note_rounded
                                ),
                                contentDescription = null,
                                //tint = if (!hasSeenTip) color else LocalContentColor.current
                            )
                        }
                        ScreenSelection(
                            expanded = screenSelectionExpanded,
                            onDismissRequest = { screenSelectionExpanded = false },
                            onNavigate = onNavigate,

                            )
                    },
                    trailingIcon = trailingIcon,
                    placeholder = {
                        CuteText(
                            text = stringResource(
                                screenToPlaceholder[CurrentScreen.screen] ?: R.string.nothing
                            ),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            maxLines = 1
                        )
                    },
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









