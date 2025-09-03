@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.artist.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.presentation.shared_components.CuteText
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.rememberInteractionSource

@Composable
fun SharedTransitionScope.ArtistHeader(
    artist: Artist,
    musics: List<MediaItem>,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val context = LocalContext.current
    val interactionSources = List(2) { rememberInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = ImageUtils.imageRequester(ImageUtils.getAlbumArt(artist.albumId), context),
            contentDescription = stringResource(R.string.artwork),
            modifier = Modifier
                .size(220.dp)
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = artist.id),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                )
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(10.dp))
        CuteText(
            text = artist.name,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            modifier = Modifier
                .basicMarquee()
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = artist.name + artist.id),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                )
        )
//        CuteText(
//            text = album.artist,
//            style = MaterialTheme.typography.bodyLargeEmphasized,
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            modifier = Modifier
//                .basicMarquee()
//                .sharedElement(
//                    sharedContentState = rememberSharedContentState(key = album.artist + album.id),
//                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
//                )
//
//        )
        Spacer(Modifier.height(15.dp))

        ButtonGroup(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            IconButton(
                onClick = {
                    onHandlePlayerActions(
                        PlayerActions.StartArtistPlayback(
                            artistName = artist.name,
                            mediaId = musics.first().mediaId
                        )
                    )
                },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledIconButtonColors(),
                interactionSource = interactionSources[0],
                modifier = Modifier
                    .weight(1f)
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                    .animateWidth(interactionSources[0])
            ) {
                Icon(
                    painter = painterResource(R.drawable.widget_play),
                    contentDescription = null
                )
            }
            IconButton(
                onClick = {
                    onHandlePlayerActions(
                        PlayerActions.StartArtistPlayback(
                            artistName = artist.name,
                            mediaId = null
                        )
                    )
                },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledIconButtonColors(),
                interactionSource = interactionSources[1],
                modifier = Modifier
                    .weight(1f)
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                    .animateWidth(interactionSources[1])
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shuffle,
                    contentDescription = null
                )
            }
        }
    }
}