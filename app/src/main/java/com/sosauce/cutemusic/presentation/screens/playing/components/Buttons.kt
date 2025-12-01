@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.shared_components.animations.AnimatedPlayPauseIcon
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.rememberInteractionSource

@Composable
fun PlayPauseButton(
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    isPlaying: Boolean,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val interactionSource = rememberInteractionSource()
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f
    )


    IconButton(
        onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) },
        modifier = buttonModifier,
        interactionSource = interactionSource
    ) {
        AnimatedPlayPauseIcon(
            isPlaying = isPlaying,
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

@Composable
fun SharedTransitionScope.ActionButtonsRow(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val interactionSources = List(5) { rememberInteractionSource() }


    ButtonGroup(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        overflowIndicator = {}
    ) {
        customItem(
            {
                FilledIconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
                    shapes = IconButtonDefaults.shapes(),
                    interactionSource = interactionSources[0],
                    modifier = Modifier
                        .weight(1f)
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                        .animateWidth(interactionSource = interactionSources[0])
                ) {
                    Icon(
                        painter = painterResource(R.drawable.skip_previous),
                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description),
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_PREVIOUS_BUTTON),
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
                            ),
                    )
                }
            },
            {}
        )
        customItem(
            {
                IconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.RewindTo(5000)) },
                    shapes = IconButtonDefaults.shapes(),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                    ),
                    interactionSource = interactionSources[1],
                    modifier = Modifier
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Narrow))
                        .animateWidth(interactionSource = interactionSources[1])
                ) {
                    Icon(
                        painter = painterResource(R.drawable.fast_rewind),
                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
                    )
                }
            },
            {}
        )
        customItem(
            {
                FilledIconToggleButton(
                    checked = musicState.isPlaying,
                    onCheckedChange = { onHandlePlayerActions(PlayerActions.PlayOrPause) },
                    shapes = IconButtonDefaults.toggleableShapes(),
                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer),
                        checkedContainerColor = MaterialTheme.colorScheme.primary,
                        checkedContentColor = contentColorFor(MaterialTheme.colorScheme.primary)
                    ),
                    interactionSource = interactionSources[2],
                    modifier = Modifier
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                        .animateWidth(interactionSource = interactionSources[2])
                ) {
                    AnimatedPlayPauseIcon(
                        isPlaying = musicState.isPlaying,
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.PLAY_PAUSE_BUTTON),
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
                            )
                    )
                }
            },
            {}
        )
        customItem(
            {
                IconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekTo(5000)) },
                    shapes = IconButtonDefaults.shapes(),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                    ),
                    interactionSource = interactionSources[3],
                    modifier = Modifier
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Narrow))
                        .animateWidth(interactionSource = interactionSources[3])
                ) {
                    Icon(
                        painter = painterResource(R.drawable.fast_forward),
                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
                    )
                }
            },
            {}
        )
        customItem(
            {
                FilledIconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
                    shapes = IconButtonDefaults.shapes(),
                    interactionSource = interactionSources[4],
                    modifier = Modifier
                        .weight(1f)
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                        .animateWidth(interactionSource = interactionSources[4])
                ) {
                    Icon(
                        painter = painterResource(R.drawable.skip_next),
                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description),
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_NEXT_BUTTON),
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
                            )
                    )
                }
            },
            {}
        )

    }
}