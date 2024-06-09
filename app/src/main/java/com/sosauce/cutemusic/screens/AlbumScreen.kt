@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.logic.BottomBar
import com.sosauce.cutemusic.logic.NavigationItem
import com.sosauce.cutemusic.logic.navigation.Screen
import com.sosauce.cutemusic.screens.utils.PreviewSamples
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SharedTransitionScope.AlbumsScreen(
	navController: NavController,
	albums: ImmutableList<Album>,
	viewModel: MusicViewModel,
	animatedVisibilityScope: AnimatedVisibilityScope,
	onNavigate: () -> Unit
) {
	AlbumsScreenContent(
		albums = albums,
		bottombarIndex = viewModel.selectedItem,
		animatedVisibilityScope = animatedVisibilityScope,
		onNavigate = onNavigate,
		onAlbumSelect = { navController.navigate(Screen.AlbumsDetails(it)) },
		onBottomBarNavigation = { idx, item ->
			navController.navigate(item.navigateTo) {
				viewModel.selectedItem = idx
				launchSingleTop = true
				restoreState = true
			}
		}
	)
}

@Composable
private fun SharedTransitionScope.AlbumsScreenContent(
	animatedVisibilityScope: AnimatedVisibilityScope,
	albums: ImmutableList<Album>,
	onAlbumSelect: (Long) -> Unit,
	bottombarIndex: Int,
	onBottomBarNavigation: (Int, NavigationItem) -> Unit,
	onNavigate: () -> Unit
) {
	val isAlbumsEmpty by remember(albums) {
		derivedStateOf { albums.isEmpty() }
	}

	Scaffold(
		topBar = {
			AppBar(
				title = "Albums",
				showBackArrow = false,
				showMenuIcon = true,
				onNavigate = onNavigate
			)

		},
		bottomBar = {
			BottomBar(
				selectedIndex = bottombarIndex,
				onNavigationItemClicked = onBottomBarNavigation
			)
		}
	) { values ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(values)
		) {
			if (isAlbumsEmpty) {
				Text(
					text = "No album found !",
					modifier = Modifier
						.padding(16.dp)
						.fillMaxWidth(),
					textAlign = TextAlign.Center,
					fontFamily = GlobalFont
				)
			} else {
				LazyVerticalGrid(
					columns = GridCells.Fixed(2),
					modifier = Modifier.fillMaxSize()
				) {
					itemsIndexed(
						items = albums,
						key = { _, album -> album.id },
					) { _, album ->
						ArtistCard(
							album = album,
							onClick = { onAlbumSelect(album.id) },
							animatedVisibilityScope = animatedVisibilityScope
						)
					}
				}
			}
		}
	}
}


@Composable
private fun SharedTransitionScope.ArtistCard(
	album: Album,
	onClick: () -> Unit,
	animatedVisibilityScope: AnimatedVisibilityScope
) {
	AlbumCardContent(
		album = album,
		onClick = onClick,
		sharedElementModifer = Modifier.sharedElement(
			state = rememberSharedContentState(key = album.id),
			animatedVisibilityScope = animatedVisibilityScope,
			boundsTransform = { _, _ -> tween(durationMillis = 1000) },
			clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(12.dp))
		),
	)
}


@Composable
fun AlbumCardContent(
	album: Album,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	sharedElementModifer: Modifier = Modifier,
) {
	Card(
		colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
		modifier = modifier
			.padding(horizontal = 5.dp, vertical = 5.dp)
			.clickable(onClick = onClick),
	) {
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxWidth()
		) {
			CommonArtwork(
				data = album.albumArt, contentDescription = "Artwork",
				modifier = Modifier
					.aspectRatio(1f)
					.padding(7.dp)
					.clip(RoundedCornerShape(15))
					.then(sharedElementModifer),
				contentScale = ContentScale.Crop
			)
			Column(
				modifier = Modifier.padding(15.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = if (album.name.length >= 15) album.name.take(15) + "..." else album.name,
					fontFamily = GlobalFont,
					maxLines = 1
				)
				Text(
					text = album.artist,
					fontFamily = GlobalFont
				)
			}
		}
	}
}

@Preview
@Composable
private fun AlbumCardPreview() = CuteMusicTheme {
	AlbumCardContent(album = PreviewSamples.FAKE_ALBUM_MODEL, onClick = {})
}