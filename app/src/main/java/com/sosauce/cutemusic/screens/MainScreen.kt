@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.getMusicArt
import com.sosauce.cutemusic.components.BottomSheetContent
import com.sosauce.cutemusic.components.CuteSearchbar
import com.sosauce.cutemusic.components.MiniNowPlayingContent
import com.sosauce.cutemusic.logic.BottomBar
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.logic.NavigationItem
import com.sosauce.cutemusic.logic.PlayerActions
import com.sosauce.cutemusic.logic.PlayerState
import com.sosauce.cutemusic.logic.navigation.Screen
import com.sosauce.cutemusic.logic.rememberIsSwipeEnabled
import com.sosauce.cutemusic.logic.rememberSortASC
import com.sosauce.cutemusic.screens.landscape.MainScreenLandscape
import com.sosauce.cutemusic.screens.utils.PreviewSamples
import com.sosauce.cutemusic.screens.utils.readableTitle
import com.sosauce.cutemusic.screens.utils.rememberIsLandscape
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MainScreen(
	navController: NavController,
	musics: ImmutableList<Music>,
	viewModel: MusicViewModel,
	state: MusicState,
	onNavigate: () -> Unit
) {
	val isLandscape = rememberIsLandscape()

	if (isLandscape) {
		MainScreenLandscape(
			musics = musics,
			state = state,
			onPlayerAction = viewModel::handlePlayerActions,
			onMusicItemSelect = viewModel::play,
			onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying) },
			onNavigate = onNavigate,
		)
	} else {
		MainScreenContent(
			musics = musics,
			state = state,
			playerState = viewModel.playerState.value,
			bottomBarIndex = viewModel.selectedItem,
			onMusicItemSelect = viewModel::play,
			onPlayerActions = viewModel::handlePlayerActions,
			onBottomBarNavigation = { idx, item ->
				navController.navigate(item.navigateTo) {
					viewModel.selectedItem = idx
					launchSingleTop = true
					restoreState = true
				}
			},
			onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying) },
			onNavigateToSettings = { navController.navigate(Screen.Settings) }
		)
	}
}


@Composable
private fun MainScreenContent(
	state: MusicState,
	playerState: PlayerState,
	bottomBarIndex: Int,
	musics: ImmutableList<Music>,
	onMusicItemSelect: (Uri) -> Unit,
	onNavigateToSettings: () -> Unit,
	onNavigateToNowPlaying: () -> Unit,
	onPlayerActions: (PlayerActions) -> Unit,
	onBottomBarNavigation: (Int, NavigationItem) -> Unit,
	modifier: Modifier = Modifier
) {

	val isInspectionMode = LocalInspectionMode.current

	val sort by rememberSortASC()
	val swipeGesturesEnabled by rememberIsSwipeEnabled()


	val displayMusics by remember(sort, musics) {
		derivedStateOf {
			if (sort) musics
			else musics.sortedByDescending(Music::title)
		}
	}

	val showMiniPlayer by remember(musics, playerState) {
		derivedStateOf {
			displayMusics.isNotEmpty() && playerState != PlayerState.STOPPED
		}
	}

	Scaffold(
		topBar = {
			CuteSearchbar(
				musics = musics,
				onNavigate = onNavigateToSettings,
				onMusicItemClicked = onMusicItemSelect
			)
		},
		bottomBar = {
			BottomBar(
				selectedIndex = bottomBarIndex,
				onNavigationItemClicked = onBottomBarNavigation
			)
		},
		modifier = modifier,
	) { values ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(values),
		) {
			LazyColumn(modifier = Modifier.fillMaxSize()) {
				if (displayMusics.isEmpty()) {
					item {
						Text(
							text = "No music found !",
							modifier = Modifier
								.padding(16.dp)
								.fillMaxWidth(),
							textAlign = TextAlign.Center,
							fontFamily = GlobalFont
						)
					}
				} else {
					items(
						items = displayMusics,
						key = if (!isInspectionMode) { music -> music.id } else null,
					) { music ->
						MusicListItem(
							music,
							onShortClick = onMusicItemSelect,
							modifier = Modifier
								.fillMaxWidth()
								.animateItem()
						)
					}
				}
			}

			if (showMiniPlayer) {
				val secondaryModifier = if (swipeGesturesEnabled)
					Modifier.pointerInput(Unit) {
						detectDragGestures { change, _ ->
							change.consume()
							onNavigateToNowPlaying()
						}
					}
				else Modifier

				Surface(
					modifier = Modifier
						.align(Alignment.BottomCenter)
						.padding(bottom = 5.dp)
						.clip(RoundedCornerShape(24.dp))
						.clickable(onClick = onNavigateToNowPlaying)
						.then(secondaryModifier),
					color = MaterialTheme.colorScheme.surfaceContainerLow
				) {
					MiniNowPlayingContent(
						state = state,
						onHandlePlayerActions = onPlayerActions,
						modifier = Modifier.fillMaxWidth(0.9f)
					)
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun MainScreenContentPreview() = CuteMusicTheme {
	MainScreenContent(
		state = PreviewSamples.FAKE_MUSIC_STATE,
		playerState = PlayerState.PLAYING,
		bottomBarIndex = 0,
		musics = PreviewSamples.FAKE_MUSICS_MODELS,
		onMusicItemSelect = {},
		onNavigateToSettings = { },
		onNavigateToNowPlaying = { },
		onPlayerActions = {},
		onBottomBarNavigation = { _, _ -> },
	)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicListItem(
	music: Music,
	onShortClick: (Uri) -> Unit,
	modifier: Modifier = Modifier
) {

	val context = LocalContext.current
	val isInspectionMode = LocalInspectionMode.current

	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	var isSheetOpen by remember { mutableStateOf(false) }
	var art: Bitmap? by remember { mutableStateOf(null) }

	LaunchedEffect(music.uri) {
		if (isInspectionMode) return@LaunchedEffect
		// creates the bitmap for the music
		art = getMusicArt(context, music)
	}

	DisposableEffect(music.uri) {
		onDispose {
			// clear the resources associated with the bitmap
			art?.recycle()
		}
	}

	if (isSheetOpen) {
		ModalBottomSheet(
			modifier = Modifier.fillMaxHeight(),
			sheetState = sheetState,
			onDismissRequest = { isSheetOpen = false }
		) {
			BottomSheetContent(music = music, bitmap = art)
		}
	}

	Row(
		modifier = modifier
			.combinedClickable(
				onClick = { onShortClick(music.uri) },
			),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
			CommonArtwork(
				bitmap = art,
				contentDescription = "Artwork",
				modifier = Modifier
					.padding(start = 10.dp)
					.size(45.dp)
					.clip(RoundedCornerShape(15))
			)
			Column(
				modifier = Modifier.padding(15.dp)
			) {
				Text(
					text = music.readableTitle,
					fontFamily = GlobalFont,
					maxLines = 1
				)
				Text(
					text = music.artist,
					fontFamily = GlobalFont
				)
			}
		}
		IconButton(onClick = { isSheetOpen = true }) {
			Icon(
				imageVector = Icons.Outlined.MoreVert,
				contentDescription = null
			)
		}
	}
}


@PreviewLightDark
@Composable
private fun MusicItemPreview() = CuteMusicTheme {
	Surface {
		MusicListItem(
			music = PreviewSamples.FAKE_MUSIC_MODEL,
			onShortClick = {},
			modifier = Modifier.fillMaxWidth()
		)
	}
}
