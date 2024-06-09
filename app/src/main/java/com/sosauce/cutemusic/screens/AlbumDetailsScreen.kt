@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.getMusicArt
import com.sosauce.cutemusic.components.BottomSheetContent
import com.sosauce.cutemusic.screens.utils.PreviewSamples
import com.sosauce.cutemusic.screens.utils.artistReadable
import com.sosauce.cutemusic.screens.utils.readableTitle
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun SharedTransitionScope.AlbumDetailsScreen(
	scope: AnimatedVisibilityScope,
	album: Album,
	onSongClick: (Uri) -> Unit,
	onPopBackStack: () -> Unit,
	modifier: Modifier = Modifier
) {
	AlbumDetailsScreenContent(
		album = album,
		onSongClick = onSongClick,
		onPopBackStack = onPopBackStack,
		modifier = modifier,
		sharedElementModifier = Modifier.sharedElement(
			state = rememberSharedContentState(key = album.id),
			animatedVisibilityScope = scope,
			boundsTransform = { _, _ -> tween(durationMillis = 1000) },
			clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(4.dp))
		),
	)
}

@Composable
private fun AlbumDetailsScreenContent(
	album: Album,
	onSongClick: (Uri) -> Unit,
	onPopBackStack: () -> Unit,
	modifier: Modifier = Modifier,
	sharedElementModifier: Modifier = Modifier,
) {
	val isInspectionMode = LocalInspectionMode.current

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = album.name,
						fontFamily = GlobalFont
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
			CommonArtwork(
				data = album.albumArt,
				contentDescription = "Album Art",
				modifier = Modifier
					.align(Alignment.CenterHorizontally)
					.aspectRatio(1 / 1f)
					.padding(17.dp)
					.clip(RoundedCornerShape(24.dp))
					.then(sharedElementModifier),
				contentScale = ContentScale.Crop,
			)
			Column {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Center
				) {
					Text(
						text = album.artistReadable,
						fontFamily = GlobalFont,
						fontSize = 22.sp
					)
					Text(
						text = pluralStringResource(
							id = R.plurals.song_count,
							count = album.numberOfSongs,
							album.numberOfSongs
						),
						fontFamily = GlobalFont,
						fontSize = 22.sp
					)
				}
			}
			Spacer(modifier = Modifier.height(5.dp))
			HorizontalDivider()
			LazyColumn {
				itemsIndexed(
					items = album.songs,
					key = if (!isInspectionMode) { _, musics -> musics.id } else null,
				) { _, music ->
					AlbumSong(
						music = music,
						onShortClick = onSongClick,
						modifier = Modifier
							.fillMaxWidth()
							.animateItem()
					)
				}
			}
		}
	}
}

@Preview
@Composable
private fun AlbumDetailsScreenPreview() = CuteMusicTheme {
	AlbumDetailsScreenContent(
		album = PreviewSamples.FAKE_ALBUM_MODEL,
		onSongClick = {},
		onPopBackStack = { },
	)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumSong(
	music: Music,
	onShortClick: (Uri) -> Unit,
	modifier: Modifier = Modifier
) {
	val sheetState = rememberModalBottomSheetState(false)
	val context = LocalContext.current
	var isSheetOpen by remember { mutableStateOf(false) }
	var art: Bitmap? by remember { mutableStateOf(null) }

	LaunchedEffect(music.uri) {
		art = getMusicArt(context, music)
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
			.clickable { onShortClick(music.uri) },
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {

		Row(verticalAlignment = Alignment.CenterVertically) {
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
