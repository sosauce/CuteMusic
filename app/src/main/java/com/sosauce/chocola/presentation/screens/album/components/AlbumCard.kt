@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.album.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.utils.ImageUtils
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@Composable
fun SharedTransitionScope.AlbumCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    album: Album,
    onClick: () -> Unit
) {

    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(1.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(15.dp)
    ) {
        AsyncImage(
            model = ImageUtils.imageRequester(
                ImageUtils.getAlbumArt(album.id)
                    ?: androidx.media3.session.R.drawable.media3_icon_album,
                context
            ),
            contentDescription = stringResource(id = R.string.artwork),
            modifier = Modifier
                .size(160.dp)
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = album.id),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                )
                .clip(SquircleShape(percent = 50, smoothing = CornerSmoothing.Full)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(10.dp))
        Column {
            Text(
                text = album.name,
                maxLines = 1,
                style = MaterialTheme.typography.titleMediumEmphasized,
                modifier = Modifier.basicMarquee()
            )
            Text(
                text = album.artist,
                style = MaterialTheme.typography.bodyLargeEmphasized.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                modifier = Modifier.basicMarquee()
            )
        }
    }
}