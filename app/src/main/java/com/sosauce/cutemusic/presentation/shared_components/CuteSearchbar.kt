@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.shared_components

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.PopupPositionProvider
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberHasSeenTip
import com.sosauce.cutemusic.data.datastore.rememberShowShuffleButton
import com.sosauce.cutemusic.data.datastore.rememberShowXButton
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.playing.components.PlayPauseButton
import com.sosauce.cutemusic.presentation.shared_components.animations.AnimatedIconButton
import com.sosauce.cutemusic.presentation.theme.CuteMusicTheme
import com.sosauce.cutemusic.presentation.theme.nunitoFontFamily
import com.sosauce.cutemusic.utils.LocalScreen
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.rememberInteractionSource
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding
import com.sosauce.cutemusic.utils.showBackButton


@Composable
fun SharedTransitionScope.CuteSearchbar(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState = rememberTextFieldState(),
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onNavigateUp: (() -> Unit)? = null,
    showSearchField: Boolean = true,
    fab: @Composable (() -> Unit)? = null,
    sortingMenu: (@Composable (() -> Unit))? = null,
) {

    val currentScreen = LocalScreen.current
    val showXButton by rememberShowXButton()
    val showShuffleButton by rememberShowShuffleButton()
    var isInScreenSelectionMode by remember { mutableStateOf(false) }
    val screenToLeadingIcon = mapOf(
        Screen.Main to R.drawable.music_note_rounded,
        Screen.Albums to R.drawable.album_filled,
        Screen.Artists to R.drawable.artists_filled,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            if (currentScreen.showBackButton()) {
                CuteNavigationButton(
                    onNavigateUp = { onNavigateUp?.invoke() }
                )
            }
            if (showShuffleButton || currentScreen == Screen.Playlists) {
                Spacer(Modifier.weight(1f))
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
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showXButton) {
                            Icon(
                                painter = painterResource(R.drawable.close),
                                contentDescription = stringResource(R.string.stop_playback),
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = null
                                ) {
                                    onHandlePlayerActions(PlayerActions.StopPlayback)
                                }
                            )
                        }
                        AnimatedContent(
                            targetState = musicState.track.title,
                            transitionSpec = { slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut() },
                            modifier = modifier.fillMaxWidth()
                        ) {
                            Text(
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
                            icon = R.drawable.skip_previous,
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
                            icon = R.drawable.skip_next,
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
                                    Text(
                                        text = stringResource(R.string.search_here),
                                        maxLines = 1
                                    )
                                },
                                leadingIcon = {
                                    var hasSeenTip by rememberHasSeenTip()
                                    TooltipBox(
                                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                                        tooltip = {
                                            RichTooltip(
                                                caretShape = TooltipDefaults.caretShape(),
                                                colors = TooltipDefaults.richTooltipColors(
                                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                    contentColor = contentColorFor(MaterialTheme.colorScheme.primaryContainer)
                                                ),
                                            ) { Text("Click me!") }
                                        },
                                        state = rememberTooltipState(initialIsVisible = !hasSeenTip, isPersistent = !hasSeenTip)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                isInScreenSelectionMode = true
                                                hasSeenTip = true
                                            },
                                            shapes = IconButtonDefaults.shapes()
                                        ) {
                                            Icon(
                                                painter = painterResource(screenToLeadingIcon[currentScreen] ?: R.drawable.search),
                                                contentDescription = null
                                            )
                                        }
                                    }
                                },
                                trailingIcon = {
                                    Row {
                                        sortingMenu?.invoke()
                                        IconButton(
                                            onClick = { onNavigate(Screen.Settings) },
                                            shapes = IconButtonDefaults.shapes()
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.settings_filled),
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
    onNavigate: (Screen) -> Unit,
    dismiss: () -> Unit
) {

    val interactionsSources = List(4) { rememberInteractionSource() }
    val currentScreen = LocalScreen.current
    val screens = listOf(
        ScreenCategory(
            screen = Screen.Main,
            onClick = { onNavigate(Screen.Main) },
            unselectedIcon = R.drawable.music_note_rounded,
            selectedIcon = R.drawable.music_note_rounded
        ),
        ScreenCategory(
            screen = Screen.Albums,
            onClick = { onNavigate(Screen.Albums) },
            unselectedIcon = androidx.media3.session.R.drawable.media3_icon_album,
            selectedIcon = R.drawable.album_filled
        ),
        ScreenCategory(
            screen = Screen.Artists,
            onClick = { onNavigate(Screen.Artists) },
            unselectedIcon = R.drawable.artist_rounded,
            selectedIcon = R.drawable.artists_filled
        ),
        ScreenCategory(
            screen = Screen.Playlists,
            onClick = { onNavigate(Screen.Playlists) },
            unselectedIcon = R.drawable.queue_music_rounded,
            selectedIcon = R.drawable.queue_music_rounded
        )
    )

    ButtonGroup(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        screens.fastForEachIndexed { index, item ->
            ToggleButton(
                checked = currentScreen == item.screen,
                onCheckedChange = {
                    item.onClick()
                    dismiss()
                },
                shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
                interactionSource = interactionsSources[index],
                modifier = Modifier
                    .weight(1f)
                    .animateWidth(interactionsSources[index])
            ) {
                val icon =
                    if (currentScreen == item.screen) item.selectedIcon else item.unselectedIcon

                Icon(
                    painter = painterResource(icon),
                    contentDescription = null
                )
            }
        }
    }
}

private data class ScreenCategory(
    val screen: Screen,
    val onClick: () -> Unit,
    @param:DrawableRes val unselectedIcon: Int,
    @param:DrawableRes val selectedIcon: Int
)

private val screensToShowBack = listOf(
    Screen.ArtistsDetails,
    Screen.AlbumsDetails,
    Screen.PlaylistDetails,
)










