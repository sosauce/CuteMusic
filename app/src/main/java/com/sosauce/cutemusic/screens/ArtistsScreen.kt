package com.sosauce.cutemusic.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Artist
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.logic.BottomBar
import com.sosauce.cutemusic.logic.NavigationItem
import com.sosauce.cutemusic.logic.navigation.Screen
import com.sosauce.cutemusic.screens.utils.PreviewSamples
import com.sosauce.cutemusic.screens.utils.readableName
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ArtistsScreen(
	artist: ImmutableList<Artist>,
	navController: NavController,
	viewModel: MusicViewModel,
	onNavigate: () -> Unit
) {
	ArtistsScreenContent(
		artist = artist,
		onNavigate = onNavigate,
		bottombarIndex = viewModel.selectedItem,
		onBottomBarNavigation = { idx, item ->
			navController.navigate(item.navigateTo) {
				viewModel.selectedItem = idx
				launchSingleTop = true
				restoreState = true
			}
		}, onNavigateToArtistDetails = { navController.navigate(Screen.ArtistsDetails(it)) }
	)
}

@Composable
private fun ArtistsScreenContent(
	artist: ImmutableList<Artist>,
	onNavigateToArtistDetails: (Long) -> Unit,
	onNavigate: () -> Unit,
	bottombarIndex: Int,
	onBottomBarNavigation: (Int, NavigationItem) -> Unit,
	modifier: Modifier = Modifier
) {
	val isInspectionMode = LocalInspectionMode.current

	val isNoArtists by remember(artist) {
		derivedStateOf(artist::isEmpty)
	}

	Scaffold(
		topBar = {
			AppBar(
				title = "Artists",
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
		},
		modifier = modifier,
	) { values ->
		Crossfade(
			targetState = isNoArtists,
			modifier = Modifier.padding(values)
		) { hasNoArtist ->
			if (hasNoArtist) {
				Box(
					modifier = Modifier.fillMaxSize(),
					contentAlignment = Alignment.Center
				) {
					Text(
						text = "No artist found !",
						modifier = Modifier
							.padding(16.dp)
							.fillMaxWidth(),
						textAlign = TextAlign.Center,
						fontFamily = GlobalFont
					)

				}
			} else {
				LazyVerticalGrid(
					columns = GridCells.Fixed(2),
					modifier = Modifier.fillMaxSize(),
				) {
					itemsIndexed(
						items = artist,
						key = if (!isInspectionMode) { _, artist -> artist.id } else null,
					) { _, artist ->
						ArtistCard(
							artist = artist,
							onClick = { onNavigateToArtistDetails(artist.id) },
							modifier = Modifier.animateItem()
						)
					}
				}
			}
		}
	}
}

@Composable
private fun ArtistCard(
	artist: Artist,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Card(
		colors = CardDefaults
			.cardColors(MaterialTheme.colorScheme.surfaceContainer),
		modifier = modifier
			.padding(all = 5.dp)
			.clickable(onClick = onClick),
	) {
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 12.dp)
		) {
			Image(
				painter = painterResource(id = R.drawable.cute_music_icon),
				contentDescription = "Artwork",
				modifier = Modifier
					.size(145.dp)
					.clip(RoundedCornerShape(15)),
				contentScale = ContentScale.Crop
			)
			Column(
				modifier = Modifier.padding(15.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = artist.readableName,
					fontFamily = GlobalFont,
					maxLines = 1
				)
			}
		}
	}
}

class ArtistsPreviewParmas : CollectionPreviewParameterProvider<ImmutableList<Artist>>(
	listOf(
		PreviewSamples.FAKE_ARTISTS_MODELS,
		persistentListOf()
	)
)

@PreviewLightDark
@Composable
private fun ArtistsScreenContentPreview(
	@PreviewParameter(ArtistsPreviewParmas::class)
	artist: ImmutableList<Artist>
) = CuteMusicTheme {
	ArtistsScreenContent(
		artist = artist,
		onNavigateToArtistDetails = {},
		onNavigate = { },
		bottombarIndex = 2,
		onBottomBarNavigation = { _, _ -> }
	)
}
