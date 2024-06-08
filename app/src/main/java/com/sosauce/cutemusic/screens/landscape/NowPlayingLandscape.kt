package com.sosauce.cutemusic.screens.landscape

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
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.components.LoopButton
import com.sosauce.cutemusic.components.MusicSlider
import com.sosauce.cutemusic.components.ShuffleButton
import com.sosauce.cutemusic.logic.NowPlayingState
import com.sosauce.cutemusic.logic.PlayerActions
import com.sosauce.cutemusic.logic.rememberIsSwipeEnabled
import com.sosauce.cutemusic.screens.CommonArtwork
import com.sosauce.cutemusic.screens.utils.PreviewSamples
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlin.math.abs

@Composable
fun NowPlayingLandscape(
	state: NowPlayingState,
	onPlayerActions: (PlayerActions) -> Unit,
	onNavigateUp: () -> Unit
) {

	NPLContent(
		onEvent = onPlayerActions,
		onNavigateUp = onNavigateUp,
		state = state
	)
}

@Composable
private fun NPLContent(
	state: NowPlayingState,
	onNavigateUp: () -> Unit,
	onEvent: (PlayerActions) -> Unit,
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

	Box(
		modifier = modifier
			.fillMaxSize()
			.padding(45.dp)
			.then(secondaryModifier)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(32.dp),
			modifier = Modifier
				.fillMaxSize()
		) {
			CommonArtwork(
				bitmap = state.artwork,
				contentDescription = "Artwork",
				modifier = Modifier
					.size(340.dp)
					.clip(RoundedCornerShape(5))
			)
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = state.currentlyPlaying,
					fontFamily = GlobalFont,
					color = MaterialTheme.colorScheme.onBackground,
					fontSize = 30.sp
				)
				Text(
					text = state.currentlyArtist,
					fontFamily = GlobalFont,
					color = MaterialTheme.colorScheme.onBackground,
					fontSize = 16.sp
				)
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
							contentDescription = null,
							tint = MaterialTheme.colorScheme.onBackground
						)
					}

					FloatingActionButton(
						onClick = { onEvent(PlayerActions.PlayOrPause) },
						modifier = Modifier.padding(horizontal = 12.dp)
					) {
						Icon(
							imageVector = if (state.isPlaying)
								Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
							contentDescription = "play/pause button"
						)
					}
					IconButton(
						onClick = { onEvent(PlayerActions.SeekToNextMusic) }
					) {
						Icon(
							imageVector = Icons.Outlined.FastForward,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.onBackground
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
						TextButton(onClick = onNavigateUp) {
							Text(text = "Home Screen", fontFamily = GlobalFont)
						}
					}
				}
			}

		}
	}
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun NPLContentPreview() = CuteMusicTheme {
	Surface {
		NPLContent(
			state = PreviewSamples.FAKE_NOW_PLAYING_STATE,
			onNavigateUp = { },
			onEvent = {},
		)
	}
}