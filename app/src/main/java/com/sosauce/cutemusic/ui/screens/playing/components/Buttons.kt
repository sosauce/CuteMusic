@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberShouldApplyLoop
import com.sosauce.cutemusic.data.datastore.rememberShouldApplyShuffle
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.ui.shared_components.AnimatedIconButton
import com.sosauce.cutemusic.utils.AnimationDirection
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.rememberAnimatable
import com.sosauce.cutemusic.utils.thenIf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoopButton() {

    val rotation = rememberAnimatable()
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
            modifier = Modifier.rotate(rotation.value),
            tint = if (shouldLoop) MaterialTheme.colorScheme.primary else LocalContentColor.current,
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
            tint = if (shouldShuffle) MaterialTheme.colorScheme.primary else LocalContentColor.current,
        )
    }
}

@Composable
fun PlayPauseButton(
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    isPlaying: Boolean,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    IconButton(
        onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) },
        modifier = buttonModifier
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = if (isPlaying) stringResource(androidx.media3.session.R.string.media3_controls_pause_description) else stringResource(
                androidx.media3.session.R.string.media3_controls_play_description),
            modifier = modifier
        )
    }
}

@Composable
fun SharedTransitionScope.ActionButtonsRow(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    val previousButtonSharedState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_PREVIOUS_BUTTON)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedIconButton(
            modifier = Modifier
                .thenIf(musicState.position <= 10000) {
                    sharedElement(
                        sharedContentState = previousButtonSharedState,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
                .size(40.dp),
            buttonModifier = Modifier.size(60.dp),
            onClick = {
                if (musicState.position >= 10000) {
                    onHandlePlayerActions(PlayerActions.RestartSong)
                } else {
                    onHandlePlayerActions(PlayerActions.SeekToPreviousMusic)
                }
            },
            animationDirection = AnimationDirection.LEFT,
            icon = if (musicState.position <= 10000) Icons.Rounded.SkipPrevious else Icons.Rounded.Replay,
            contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_to_previous_description)
        )
        AnimatedIconButton(
            modifier = Modifier.size(35.dp),
            buttonModifier = Modifier.size(60.dp),
            onClick = { onHandlePlayerActions(PlayerActions.RewindTo(5000)) },
            animationDirection = AnimationDirection.LEFT,
            icon = Icons.Rounded.FastRewind,
            contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_back_description)
        )
        PlayPauseButton(
            modifier = Modifier.size(40.dp),
            buttonModifier = Modifier.size(60.dp),
            isPlaying = musicState.isPlaying,
            onHandlePlayerActions = onHandlePlayerActions
        )
        AnimatedIconButton(
            modifier = Modifier.size(35.dp),
            buttonModifier = Modifier.size(60.dp),
            onClick = { onHandlePlayerActions(PlayerActions.SeekTo(5000)) },
            animationDirection = AnimationDirection.RIGHT,
            icon = Icons.Rounded.FastForward,
            contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
        )
        AnimatedIconButton(
            modifier = Modifier
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_NEXT_BUTTON),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .size(40.dp),
            buttonModifier = Modifier.size(60.dp),
            onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
            animationDirection = AnimationDirection.RIGHT,
            icon = Icons.Rounded.SkipNext,
            contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_to_next_description)
        )
    }
}