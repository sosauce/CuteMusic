@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.album.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.Album
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun SharedTransitionScope.AlbumCard(
    modifier: Modifier = Modifier,
    album: Album,
    onClick: () -> Unit
) {

    val context = LocalContext.current

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
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
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = album.id),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                )
                .sizeIn(maxHeight = 160.dp)
                .clip(RoundedCornerShape(15)),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(10.dp))
        Column {
            Text(
                text = album.name,
                maxLines = 1,
                style = MaterialTheme.typography.titleMediumEmphasized,
                modifier = Modifier
//                    .sharedElement(
//                        sharedContentState = rememberSharedContentState(key = album.name + album.id),
//                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
//                    )
                    .basicMarquee()
            )
            Text(
                text = album.artist,
                style = MaterialTheme.typography.bodyLargeEmphasized.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                modifier = Modifier
                    .basicMarquee()
//                    .sharedElement(
//                        sharedContentState = rememberSharedContentState(key = album.artist + album.id),
//                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
//                    )

            )
        }
    }
}