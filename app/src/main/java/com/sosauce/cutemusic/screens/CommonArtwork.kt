package com.sosauce.cutemusic.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sosauce.cutemusic.R

@Composable
fun CommonArtwork(
	bitmap: Bitmap?,
	contentDescription: String?,
	modifier: Modifier = Modifier,
	alignment: Alignment = Alignment.Center,
	contentScale: ContentScale = ContentScale.Crop,
	colorFilter: ColorFilter? = null,
) {
	val context = LocalContext.current
	val isLocalInsepectionMode = LocalInspectionMode.current

	if (isLocalInsepectionMode) {
		Image(
			painter = painterResource(id = R.drawable.cute_music_icon),
			contentDescription = contentDescription,
			contentScale = contentScale,
			alignment = alignment,
			colorFilter = colorFilter,
			modifier = modifier,
		)
	} else {
		AsyncImage(
			model = ImageRequest.Builder(context)
				.data(bitmap)
				.crossfade(true)
				.build(),
			contentDescription = contentDescription,
			contentScale = contentScale,
			alignment = alignment,
			colorFilter = colorFilter,
			modifier = modifier,
		)
	}
}