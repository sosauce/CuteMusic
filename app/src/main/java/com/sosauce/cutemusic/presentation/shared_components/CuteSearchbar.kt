@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberShowBackButton
import com.sosauce.cutemusic.data.datastore.rememberShowShuffleButton
import com.sosauce.cutemusic.data.datastore.rememberShowXButton
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.playing.components.PlayPauseButton
import com.sosauce.cutemusic.presentation.theme.nunitoFontFamily
import com.sosauce.cutemusic.utils.LocalScreen
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.rememberInteractionSource
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding


@Composable
fun SharedTransitionScope.CuteSearchbar(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState = rememberTextFieldState(),
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    showSearchField: Boolean = true,
    fab: @Composable (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    sortingMenu: (@Composable (() -> Unit))? = null,
) {

    val currentScreen = LocalScreen.current
    val showXButton by rememberShowXButton()
    val showShuffleButton by rememberShowShuffleButton()
    val showBackButton by rememberShowBackButton()
    var isInScreenSelectionMode by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    val screenToLeadingIcon = mapOf(
        Screen.Main to R.drawable.music_note_rounded,
        Screen.Albums to androidx.media3.session.R.drawable.media3_icon_album,
        Screen.Artists to R.drawable.artist_rounded,
        Screen.Playlists to R.drawable.queue_music_rounded,
    )


    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth(rememberSearchbarMaxFloatValue())
            .padding(end = rememberSearchbarRightPadding())
            .imePadding()

    ) {

//        AnimatedVisibility(
//            visible = query.isEmpty() && hasFocus
//        ) {
//            SearchHistory(
//                onInsertToSearch = { onQueryChange(it) }
//            )
//            Spacer(Modifier.height(10.dp))
//        }

        Row(
            horizontalArrangement = if (navigationIcon != null) Arrangement.SpaceBetween else Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (showBackButton) {
                navigationIcon?.invoke()
            }
            if (showShuffleButton || currentScreen == Screen.Playlists) {
                fab?.invoke()
            }
        }
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clickable(
                    enabled = musicState.isPlayerReady
                ) { onNavigate(Screen.NowPlaying) }
        ) {
            AnimatedVisibility(
                visible = musicState.isPlayerReady,
                enter = fadeIn() + slideInVertically { it }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = 10.dp)
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
                        AnimatedContent(
                            targetState = musicState.title,
                            transitionSpec = { slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut() },
                            modifier = modifier.fillMaxWidth()
                        ) {
                            CuteText(
                                text = it,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.CURRENTLY_PLAYING),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                    )
                                    .basicMarquee()
                            )
                        }
                    }
                    Row {
                        AnimatedIconButton(
                            modifier = Modifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_PREVIOUS_BUTTON),
                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                ),
                            onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
                            icon = Icons.Rounded.SkipPrevious,
                            contentDescription = stringResource(R.string.seek_prev_song)
                        )
                        PlayPauseButton(
                            isPlaying = musicState.isPlaying,
                            onHandlePlayerActions = onHandlePlayerActions,
                            modifier = Modifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.PLAY_PAUSE_BUTTON),
                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                )
                        )
                        AnimatedIconButton(
                            modifier = Modifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_NEXT_BUTTON),
                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                ),
                            onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
                            icon = Icons.Rounded.SkipNext,
                            contentDescription = stringResource(R.string.seek_next_song)
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = showSearchField
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(6.dp)
                ) {
                    AnimatedContent(
                        targetState = isInScreenSelectionMode,
                        transitionSpec = { scaleIn() togetherWith scaleOut() }
                    ) {
                        if (it) {
                            ScreenSelection(
                                screenToLeadingIcon = screenToLeadingIcon,
                                onNavigate = onNavigate,
                                dismiss = { isInScreenSelectionMode = false }
                            )
                        } else {
                            TextField(
                                state = textFieldState,
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                placeholder = {
                                    CuteText(
                                        text = stringResource(R.string.search_here),
                                        maxLines = 1
                                    )
                                },
                                leadingIcon = {
                                    IconButton(
                                        onClick = { isInScreenSelectionMode = true }
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                screenToLeadingIcon[currentScreen]
                                                    ?: R.drawable.search
                                            ),
                                            contentDescription = null
                                        )
                                    }
                                },
                                trailingIcon = {
                                    Row {

                                        DropdownMenu(
                                            expanded = showSortMenu,
                                            onDismissRequest = { showSortMenu = false },
                                            shape = RoundedCornerShape(24.dp)
                                        ) {
                                            sortingMenu?.invoke()
                                        }
                                        IconButton(
                                            onClick = { showSortMenu = !showSortMenu }
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.Sort,
                                                contentDescription = stringResource(R.string.sort)
                                            )
                                        }
                                        IconButton(
                                            onClick = { onNavigate(Screen.Settings) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Settings,
                                                contentDescription = stringResource(R.string.settings)
                                            )
                                        }
                                    }
                                },
                                textStyle = TextStyle.Default.copy(
                                    fontFamily = nunitoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                lineLimits = TextFieldLineLimits.SingleLine,
                                shape = FloatingToolbarDefaults.ContainerShape,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenSelection(
    screenToLeadingIcon: Map<Screen, Int>,
    onNavigate: (Screen) -> Unit,
    dismiss: () -> Unit
) {

    val interactionsSources = List(4) { rememberInteractionSource() }
    val currentScreen = LocalScreen.current

    ButtonGroup(
        overflowIndicator = {},
        modifier = Modifier.fillMaxWidth()
    ) {
        screenToLeadingIcon.onEachIndexed { index, (screen, icon) ->
            customItem(
                {
                    ToggleButton(
                        checked = currentScreen == screen,
                        onCheckedChange = {
                            onNavigate(screen)
                            dismiss()
                        },
                        shapes = ToggleButtonDefaults.shapes(),
                        interactionSource = interactionsSources[index],
                        modifier = Modifier
                            .weight(1f)
                            .animateWidth(interactionsSources[index])
                    ) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null
                        )
                    }
                },
                {}
            )
        }
    }
}








