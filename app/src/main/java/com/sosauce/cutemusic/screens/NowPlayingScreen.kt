package com.sosauce.cutemusic.screens


import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.components.LoopButton
import com.sosauce.cutemusic.components.MusicSlider
import com.sosauce.cutemusic.components.ShuffleButton
import com.sosauce.cutemusic.logic.NowPlayingState
import com.sosauce.cutemusic.logic.PlayerActions
import com.sosauce.cutemusic.logic.rememberIsSwipeEnabled
import com.sosauce.cutemusic.screens.landscape.NowPlayingLandscape
import com.sosauce.cutemusic.screens.utils.PreviewSamples
import com.sosauce.cutemusic.screens.utils.currentArtisitReadableName
import com.sosauce.cutemusic.screens.utils.currentPlayingReadable
import com.sosauce.cutemusic.screens.utils.rememberIsLandscape
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlin.math.abs


@Composable
fun NowPlayingScreen(
	navController: NavController,
	viewModel: MusicViewModel,
	state: NowPlayingState
) {
	val isLandscape = rememberIsLandscape()

	if (isLandscape) {
		NowPlayingLandscape(
			state = state,
			onPlayerActions = viewModel::handlePlayerActions,
			onNavigateUp = dropUnlessResumed { navController.navigateUp() },
		)
	} else {
		NowPlayingContent(
			state = state,
			onEvent = viewModel::handlePlayerActions,
			onNavigateUp = dropUnlessResumed { navController.navigate(navController.graph.startDestinationId) },
		)
	}

}

@Composable
private fun NowPlayingContent(
	state: NowPlayingState,
	onEvent: (PlayerActions) -> Unit,
	onNavigateUp: () -> Unit,
	modifier: Modifier = Modifier,
) {

	val swipeGesturesEnabled by rememberIsSwipeEnabled()

	val secondaryModifier = remember(swipeGesturesEnabled) {
		if (swipeGesturesEnabled) Modifier.pointerInput(Unit) {
			detectDragGestures { change, dragAmount ->
				change.consume()
				val (x, y) = dragAmount
				if (abs(x) > abs(y)) {
					if (x > 0) onEvent(PlayerActions.SeekToPreviousMusic)
					else onEvent(PlayerActions.SeekToNextMusic)
				} else {
					onNavigateUp()
				}
			}
		}
		else Modifier
	}

	Scaffold(modifier = modifier) { value ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(value)
				.padding(15.dp)
				.then(secondaryModifier)
		) {
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center
			) {
				Spacer(modifier = Modifier.height(45.dp))
				CommonArtwork(
					bitmap = state.artwork,
					contentDescription = "Artwork",
					modifier = Modifier
						.size(340.dp)
						.clip(RoundedCornerShape(5)),
				)
				Spacer(modifier = Modifier.height(20.dp))
				Text(
					text = state.currentPlayingReadable,
					fontFamily = GlobalFont,
					color = MaterialTheme.colorScheme.onBackground,
					fontSize = 20.sp,
				)
				Spacer(modifier = Modifier.height(5.dp))
				Text(
					text = state.currentArtisitReadableName,
					fontFamily = GlobalFont,
					color = MaterialTheme.colorScheme.onBackground,
					fontSize = 14.sp,
					overflow = TextOverflow.Ellipsis
				)
				Spacer(modifier = Modifier.height(10.dp))
				MusicSlider(
					state = state,
					onSeekSlider = { amount -> onEvent(PlayerActions.SeekTo(amount)) },
				)
				Row(
					horizontalArrangement = Arrangement.Center,
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.fillMaxWidth()
				) {
					ShuffleButton()
					IconButton(
						onClick = { onEvent(PlayerActions.SeekToPreviousMusic) }
					) {
						Icon(
							imageVector = Icons.Outlined.FastRewind,
							contentDescription = "previous"
						)
					}

					FloatingActionButton(
						onClick = { onEvent(PlayerActions.PlayOrPause) },
						modifier = Modifier.padding(horizontal = 12.dp)
					) {
						Icon(
							imageVector = if (state.isPlaying) Icons.Outlined.Pause
							else Icons.Outlined.PlayArrow,
							contentDescription = "pause/play button"
						)
					}
					IconButton(
						onClick = { onEvent(PlayerActions.SeekToNextMusic) }
					) {
						Icon(
							imageVector = Icons.Outlined.FastForward,
							contentDescription = "next"
						)
					}
					LoopButton()
				}
				if (!swipeGesturesEnabled) {
					Spacer(modifier = Modifier.height(30.dp))
					Row(
						modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.Center
					) {
						IconButton(onClick = onNavigateUp) {
							Icon(
								imageVector = Icons.Outlined.Home,
								contentDescription = "Home"
							)
						}
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun NowPlayingScreenPreviews() = CuteMusicTheme {
	NowPlayingContent(
		state = PreviewSamples.FAKE_NOW_PLAYING_STATE,
		onEvent = {},
		onNavigateUp = {},
	)
}






