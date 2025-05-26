@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberShowBackButton
import com.sosauce.cutemusic.data.datastore.rememberShowShuffleButton
import com.sosauce.cutemusic.data.datastore.rememberShowXButton
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.playing.components.PlayPauseButton
import com.sosauce.cutemusic.utils.AnimationDirection
import com.sosauce.cutemusic.utils.CurrentScreen
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding

@Composable
fun SharedTransitionScope.CuteSearchbar(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    currentlyPlaying: String = "",
    onHandlePlayerActions: (PlayerActions) -> Unit,
    isPlaying: Boolean = false,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isPlayerReady: Boolean = true,
    onNavigate: (Screen) -> Unit,
    showSearchField: Boolean = true,
    fab: @Composable (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
) {

    val showXButton by rememberShowXButton()
    val showShuffleButton by rememberShowShuffleButton()
    val showBackButton by rememberShowBackButton()
    val screenToLeadingIcon = remember {
        hashMapOf(
            Screen.Main to R.drawable.music_note_rounded,
            Screen.Albums to androidx.media3.session.R.drawable.media3_icon_album,
            Screen.Artists to R.drawable.artist_rounded,
            Screen.Playlists to R.drawable.queue_music_rounded,
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
            if (showBackButton) {
                navigationIcon?.invoke()
            }
            if (showShuffleButton || CurrentScreen.screen == Screen.Playlists) {
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
                enter = fadeIn() + slideInVertically { it }
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
                                    contentDescription = stringResource(R.string.stop_playback),
                                )
                            }
                        }
                        CuteText(
                            text = currentlyPlaying,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.CURRENTLY_PLAYING),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .basicMarquee()
                        )
                    }
                    Row{
                        AnimatedIconButton(
                            modifier = Modifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_PREVIOUS_BUTTON),
                                    animatedVisibilityScope = animatedVisibilityScope
                                ),
                            onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
                            animationDirection = AnimationDirection.LEFT,
                            icon = Icons.Rounded.SkipPrevious,
                            contentDescription = stringResource(R.string.seek_prev_song)
                        )
                        PlayPauseButton(
                            isPlaying = isPlaying,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                        AnimatedIconButton(
                            modifier = Modifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_NEXT_BUTTON),
                                    animatedVisibilityScope = animatedVisibilityScope
                                ),
                            onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
                            animationDirection = AnimationDirection.RIGHT,
                            icon = Icons.Rounded.SkipNext,
                            contentDescription = stringResource(R.string.seek_next_song)
                        )
                    }
                }
            }
            if (showSearchField) {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {},
                    expanded = true,
                    onExpandedChange = {},
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                            0.5f
                        ),
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                            0.5f
                        ),
                        disabledIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = {
                        var screenSelectionExpanded by remember { mutableStateOf(false) }

                        IconButton(
                            onClick = { screenSelectionExpanded = true }
                        ) {
                            Icon(
                                painter = painterResource(screenToLeadingIcon[CurrentScreen.screen] ?: R.drawable.music_note_rounded),
                                contentDescription = stringResource(R.string.screen_selection),
                            )
                        }
                        ScreenSelection(
                            expanded = screenSelectionExpanded,
                            onDismissRequest = { screenSelectionExpanded = false },
                            onNavigate = onNavigate
                        )
                    },
                    trailingIcon = {
                        Row {
                            trailingIcon?.invoke()
                            IconButton(
                                onClick = { onNavigate(Screen.Settings) }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Settings,
                                    contentDescription = stringResource(R.string.settings),
                                )
                            }
                        }
                    },
                    placeholder = {
                        CuteText(
                            text = stringResource(R.string.search_here),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            maxLines = 1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                )
            }
        }
    }
}
@Composable
fun MockCuteSearchbar(
    modifier: Modifier = Modifier
) {

    val showXButton by rememberShowXButton()
    val showShuffleButton by rememberShowShuffleButton()
    val showBackButton by rememberShowBackButton()

    Column(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            AnimatedVisibility(
                visible = showBackButton,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                CuteNavigationButton {}
            }
            AnimatedVisibility(
                visible = showShuffleButton,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                CuteActionButton {}
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

                    AnimatedVisibility(
                        visible = showXButton,
                        enter = scaleIn(),
                        exit = scaleOut()
                    ) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null,
                            )
                        }
                    }
                    CuteText(
                        text = "Gusty Garden OST",
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .basicMarquee()
                    )
                }
                Row{
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipPrevious,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Pause,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = null
                        )
                    }
                }
            }
            SearchBarDefaults.InputField(
                query = "",
                onQueryChange = {},
                onSearch = {},
                expanded = true,
                onExpandedChange = {},
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                        0.5f
                    )
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.music_note_rounded),
                        contentDescription = null,
                    )
                },
                trailingIcon = {
                    Row {
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Sort,
                                contentDescription = null,
                            )
                        }
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = null,
                            )
                        }
                    }
                },
                placeholder = {
                    CuteText(
                        text = stringResource(R.string.search_here),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        maxLines = 1
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            )
        }
    }
}









