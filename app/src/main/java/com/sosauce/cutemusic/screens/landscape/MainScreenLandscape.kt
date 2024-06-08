package com.sosauce.cutemusic.screens.landscape

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.logic.PlayerActions
import com.sosauce.cutemusic.screens.MusicListItem
import com.sosauce.cutemusic.screens.utils.PreviewSamples
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MainScreenLandscape(
	state: MusicState,
	musics: ImmutableList<Music>,
	onMusicItemSelect: (Uri) -> Unit,
	onPlayerAction: (PlayerActions) -> Unit,
	onNavigateToNowPlaying: () -> Unit,
	onNavigate: () -> Unit
) {

	val isLocalInspectionMode = LocalInspectionMode.current

	Scaffold(
		topBar = {
			AppBar(
				title = "landscape",
				showBackArrow = false,
				showMenuIcon = true,
				onNavigate = onNavigate
			)
		}
	) { values ->

		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(values),
		) {
			LazyColumn(
				modifier = Modifier.fillMaxWidth(),
				verticalArrangement = Arrangement.Top
			) {
				itemsIndexed(
					items = musics,
					key = if (!isLocalInspectionMode) { _, music -> music.id } else null,
				) { _, music ->
					MusicListItem(
						music = music,
						onShortClick = onMusicItemSelect,
						modifier = Modifier
							.fillMaxWidth()
							.animateItem()
					)
				}
			}

			Row(
				modifier = Modifier
					.align(Alignment.BottomEnd)
					.background(MaterialTheme.colorScheme.background)
					.clickable(onClick = onNavigateToNowPlaying)
					.padding(5.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = if (state.currentlyPlaying == "null") "" else state.currentlyPlaying,
					fontFamily = GlobalFont,
					maxLines = 1,
					modifier = Modifier.padding(start = 10.dp)
				)
				Row(horizontalArrangement = Arrangement.End) {
					IconButton(
						onClick = { onPlayerAction(PlayerActions.SeekToPreviousMusic) }
					) {
						Icon(
							imageVector = Icons.Outlined.FastRewind,
							contentDescription = "previous song"
						)
					}
					IconButton(
						onClick = { onPlayerAction(PlayerActions.PlayOrPause) }
					) {
						Icon(
							imageVector = if (state.isPlaying) Icons.Outlined.Pause
							else Icons.Outlined.PlayArrow,
							contentDescription = "play/pause button"
						)
					}
					IconButton(
						onClick = { onPlayerAction(PlayerActions.SeekToNextMusic) }
					) {
						Icon(
							imageVector = Icons.Outlined.FastForward,
							contentDescription = "next song"
						)
					}
				}
			}
		}
	}
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun MainScreenLandscapePreview() = CuteMusicTheme {
	MainScreenLandscape(
		musics = PreviewSamples.FAKE_MUSICS_MODELS,
		state = PreviewSamples.FAKE_MUSIC_STATE,
		onMusicItemSelect = {},
		onPlayerAction = {},
		onNavigateToNowPlaying = { }, onNavigate = {}
	)
}