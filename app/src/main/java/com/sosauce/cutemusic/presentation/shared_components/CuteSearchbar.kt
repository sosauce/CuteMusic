@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.shared_components

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.splineBasedDecay
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberHasSeenTip
import com.sosauce.cutemusic.data.datastore.rememberShowShuffleButton
import com.sosauce.cutemusic.data.datastore.rememberShowXButton
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.playing.NowPlaying
import com.sosauce.cutemusic.presentation.screens.playing.components.PlayPauseButton
import com.sosauce.cutemusic.presentation.shared_components.animations.AnimatedIconButton
import com.sosauce.cutemusic.presentation.theme.nunitoFontFamily
import com.sosauce.cutemusic.utils.LocalScreen
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.rememberInteractionSource
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding
import com.sosauce.cutemusic.utils.showBackButton
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.getScopeName
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


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
    val surfaceContainerHigh = MaterialTheme.colorScheme.surfaceContainerHigh
    val showShuffleButton by rememberShowShuffleButton()
    var isInScreenSelectionMode by remember { mutableStateOf(false) }
    val screenToLeadingIcon = mapOf(
        Screen.Main to R.drawable.music_note_rounded,
        Screen.Albums to R.drawable.album_filled,
        Screen.Artists to R.drawable.artists_filled,
        Screen.Playlists to R.drawable.queue_music_rounded,
    )
    val scope = rememberCoroutineScope()
    var showFullPlayer by rememberSaveable { mutableStateOf(false) }
    val density = LocalDensity.current
    val thresholdY = with(density) { 200.dp.toPx() }
    val yTranslation = remember {
        Animatable(0f).apply {
            if (showFullPlayer) {
                updateBounds(
                    lowerBound = 0f,
                    upperBound = thresholdY
                )
            } else {
                updateBounds(
                    lowerBound = -thresholdY,
                    upperBound = thresholdY
                )
            }
        }
    }
    val dragState = rememberDraggableState { dragAmount ->
        scope.launch {
            yTranslation.snapTo(
                yTranslation.value + (dragAmount * (1 - (yTranslation.value / thresholdY).absoluteValue))
            )
        }
    }
    val searchbarWidth = remember(yTranslation.value) { 0.85f + (1f - 0.85f) * (-yTranslation.value / 400) }
    val searchbarAlpha = remember(yTranslation.value) { 1f + (-yTranslation.value / (thresholdY / 2)) }

    AnimatedContent(
        targetState = showFullPlayer,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = yTranslation.value
            }
            .draggable(
                state = dragState,
                enabled = musicState.isPlayerReady,
                orientation = Orientation.Vertical,
                onDragStopped = {
                    scope.launch {
                        if (yTranslation.value > 0) {
                            onHandlePlayerActions(PlayerActions.StopPlayback)
                            yTranslation.animateTo(0f)
                        } else {
                            showFullPlayer = true
                            yTranslation.snapTo(0f)
                        }
                    }
                }
            )
    ) { fullPlayer ->
        if (fullPlayer) {
            BackHandler { showFullPlayer = false }
            NowPlaying(
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions,
                onNavigate = onNavigate,
                onNavigateUp = { onNavigateUp?.invoke() },
                onShrinkToSearchbar = { showFullPlayer = false }
            )
        } else {
            Column(
                modifier = modifier
                    .navigationBarsPadding()
                    .fillMaxWidth(searchbarWidth)
                    .padding(end = rememberSearchbarRightPadding())
                    .imePadding()
                    .graphicsLayer {
                        alpha = searchbarAlpha
                    }
            ) {
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
                        .clickable(
                            enabled = musicState.isPlayerReady
                        ) { showFullPlayer = true }
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .drawBehind {
                            val fraction = if (musicState.track.durationMs == 0L) {
                                0f
                            } else {
                                musicState.position.toFloat() / musicState.track.durationMs.toFloat()
                            }

                            val drawWidth = size.width * fraction
                            drawRect(
                                color = surfaceContainerHigh,
                                size = Size(drawWidth, size.height)
                            )
                        }
                ) {
                    AnimatedVisibility(
                        visible = musicState.isPlayerReady,
                        enter = fadeIn() + slideInVertically { it }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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












