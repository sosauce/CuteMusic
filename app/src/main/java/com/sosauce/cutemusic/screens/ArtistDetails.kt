@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.sosauce.cutemusic.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.audio.Artist
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.getMusicArt
import com.sosauce.cutemusic.components.BottomSheetContent
import com.sosauce.cutemusic.logic.imageRequester
import com.sosauce.cutemusic.logic.navigation.Screen
import com.sosauce.cutemusic.screens.utils.PreviewSamples
import com.sosauce.cutemusic.screens.utils.readableName
import com.sosauce.cutemusic.screens.utils.readableTitle
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun ArtistDetails(
	artist: Artist,
	navController: NavController,
	viewModel: MusicViewModel,
	modifier: Modifier = Modifier
) {
	ArtistScreenContent(
		artist = artist,
		onPopBackStack = navController::popBackStack,
		onNavigateToAlbum = { navController.navigate(Screen.AlbumsDetails(it)) },
		onMusicUriClicked = viewModel::play,
		modifier = modifier
	)
}

@Composable
fun ArtistScreenContent(
	artist: Artist,
	onPopBackStack: () -> Unit,
	onNavigateToAlbum: (Long) -> Unit,
	onMusicUriClicked: (Uri) -> Unit,
	modifier: Modifier = Modifier
) {
	val isInspectionMode = LocalInspectionMode.current

	val songsCount = pluralStringResource(
		id = R.plurals.song_count,
		count = artist.numberOfSongs,
		artist.numberOfSongs
	)

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = buildString {
							append(artist.name + " · ")
							append(songsCount)
						},
						fontFamily = GlobalFont,
						fontSize = 22.sp
					)
				},
				navigationIcon = {
					IconButton(onClick = onPopBackStack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back arrow"
						)
					}
				}
			)
		},
		modifier = modifier,
	) { values ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(values)
		) {
			LazyRow(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Center
			) {
				itemsIndexed(
					items = artist.albums,
					key = if (!isInspectionMode) { _, album -> album.id } else null
				) { _, album ->
					AlbumsCard(
						album = album,
						onClick = { onNavigateToAlbum(album.id) },
						modifier = Modifier.animateItem()
					)
				}
			}
			Spacer(modifier = Modifier.height(10.dp))
			HorizontalDivider()
			Spacer(modifier = Modifier.height(10.dp))
			LazyColumn(
				verticalArrangement = Arrangement.spacedBy(2.dp),
				modifier = Modifier.weight(1f)
			) {
				itemsIndexed(
					items = artist.songs,
					key = if (!isInspectionMode) { _, music -> music.id } else null,
				) { _, music ->
					ArtistMusicList(
						music = music,
						onShortClick = { onMusicUriClicked(music.uri) },
						onSelected = { /*TODO*/ },
						isSelected = false,
						modifier = Modifier.animateItem()
					)
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun ArtistsScreenContentsPreview() = CuteMusicTheme {
	ArtistScreenContent(
		artist = PreviewSamples.FAKE_ARTIST_MODEL,
		onPopBackStack = {},
		onNavigateToAlbum = { },
		onMusicUriClicked = {},
	)
}

@Composable
private fun AlbumsCard(
	album: Album,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Card(
		colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
		modifier = modifier
			.padding(horizontal = 10.dp, vertical = 5.dp)
			.clickable { onClick() },
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp)
		) {
			CommonArtwork(
				data = album.albumArt,
				contentDescription = "Album Art",
				modifier = Modifier
					.size(160.dp)
					.clip(RoundedCornerShape(15)),
				contentScale = ContentScale.Crop,
			)
			Text(
				text = album.readableName,
				fontFamily = GlobalFont
			)
			Text(
				text = pluralStringResource(
					id = R.plurals.song_count,
					count = album.numberOfSongs,
					album.numberOfSongs
				),
				fontFamily = GlobalFont
			)
		}
	}
}

@Composable
fun ArtistMusicList(
	music: Music,
	onShortClick: (Uri) -> Unit,
	onSelected: () -> Unit,
	isSelected: Boolean,
	modifier: Modifier = Modifier
) {

	val sheetState = rememberModalBottomSheetState()
	val context = LocalContext.current
	var isSheetOpen by remember { mutableStateOf(false) }
	var art: Bitmap? by remember { mutableStateOf(null) }

	LaunchedEffect(music.uri) {
		art = getMusicArt(context, music)
	}

	DisposableEffect(key1 = music.uri) {
		onDispose {
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
			.fillMaxWidth()
			.combinedClickable(
				onClick = { onShortClick(music.uri) },
				onLongClick = onSelected
			),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {

		Row(verticalAlignment = Alignment.CenterVertically) {
			if (!isSelected) {
				AsyncImage(
					model = imageRequester(
						img = art,
						context = context
					),
					contentDescription = "Artwork",
					modifier = Modifier
						.padding(start = 10.dp)
						.size(45.dp)
						.clip(RoundedCornerShape(15)),
					contentScale = ContentScale.Crop
				)
			} else {
				Image(
					painter = rememberAsyncImagePainter(art ?: R.drawable.cute_music_icon),
					contentDescription = "Artwork",
					modifier = Modifier
						.padding(start = 10.dp)
						.size(45.dp)
						.clip(RoundedCornerShape(15)),
					contentScale = ContentScale.Crop,
				)
			}

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