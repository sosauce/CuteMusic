package com.sosauce.cutemusic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.logic.PlayerActions
import com.sosauce.cutemusic.screens.utils.currentlyPlayingReadable
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun MiniNowPlayingContent(
	state: MusicState,
	onHandlePlayerActions: (PlayerActions) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.padding(horizontal = 15.dp, vertical = 13.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = state.currentlyPlayingReadable,
			fontFamily = GlobalFont,
		)
		Row(
			horizontalArrangement = Arrangement.End,
			modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp)
		) {
			IconButton(
				onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) }
			) {
				Icon(
					imageVector = Icons.Outlined.FastRewind,
					contentDescription = "previous song"
				)
			}
			IconButton(
				onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) }
			) {
				Icon(
					imageVector = if (state.isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
					contentDescription = "play/pause button"
				)
			}
			IconButton(
				onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) }
			) {
				Icon(
					imageVector = Icons.Outlined.FastForward,
					contentDescription = "next song"
				)
			}
		}
	}
}