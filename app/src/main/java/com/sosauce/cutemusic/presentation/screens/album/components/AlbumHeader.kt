@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class
)

package com.sosauce.cutemusic.presentation.screens.album.components

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
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.Album
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.rememberInteractionSource
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

@Composable
fun SharedTransitionScope.AlbumHeader(
    album: Album,
    tracks: List<CuteTrack>,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val context = LocalContext.current
    remember { HazeState() }
    val interactionSources = List(2) { rememberInteractionSource() }

//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .size(220.dp)
//    ) {
//        AsyncImage(
//            model = ImageUtils.imageRequester(ImageUtils.getAlbumArt(album.id), context),
//            contentDescription = stringResource(R.string.artwork),
//            modifier = Modifier
//                .fillMaxSize()
//                .hazeSource(hazeState)
//                .sharedElement(
//                    sharedContentState = rememberSharedContentState(key = album.id),
//                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
//                ),
//            contentScale = ContentScale.Crop
//        )
//
//        Row(
//            modifier = Modifier
//                .align(Alignment.BottomStart)
//                .fillMaxWidth()
//                .hazeEffect(
//                    state = hazeState,
//                    style = HazeMaterials.ultraThin(Color.Transparent)
//                ) {
//                    progressive = HazeProgressive.verticalGradient(
//                        startIntensity = 1f,
//                        endIntensity = 0f,
//                        startY = Float.POSITIVE_INFINITY,
//                        endY = 0f
//                    )
//                }
//                .padding(5.dp),
//            verticalAlignment = Alignment.Bottom
//        ) {
//            Column(
//                horizontalAlignment = Alignment.Start
//            ) {
//                Text(
//                    text = album.name,
//                    style = MaterialTheme.typography.headlineMediumEmphasized,
//                    maxLines = 1,
//                    modifier = Modifier
////                .sharedElement(
////                    sharedContentState = rememberSharedContentState(key = album.name + album.id),
////                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
////                )
//                        .basicMarquee()
//                )
//                Text(
//                    text = album.artist,
//                    style = MaterialTheme.typography.bodyLargeEmphasized.copy(
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    ),
//                    modifier = Modifier
//                        .basicMarquee()
////                .sharedElement(
////                    sharedContentState = rememberSharedContentState(key = album.artist + album.id),
////                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
////                )
//
//                )
//            }
//            Spacer(Modifier.weight(1f))
//            MediumFloatingActionButton(
//                shape = MaterialShapes.Pill.toShape(),
//                onClick = {
//                    onHandlePlayerActions(
//                        PlayerActions.StartAlbumPlayback(
//                            albumName = album.name,
//                            mediaId = null
//                        )
//                    )
//                }
//            ) {
//                Icon(
//                    imageVector = Icons.Rounded.Shuffle,
//                    contentDescription = null
//                )
//            }
//            MediumFloatingActionButton(
//                shape = MaterialShapes.Pill.toShape(),
//                onClick = {
//                    onHandlePlayerActions(
//                        PlayerActions.StartAlbumPlayback(
//                            albumName = album.name,
//                            mediaId = musics.first().mediaId
//                        )
//                    )
//                }
//            ) {
//                Icon(
//                    imageVector = Icons.Rounded.PlayArrow,
//                    contentDescription = null
//                )
//            }
//        }
//
//    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = ImageUtils.imageRequester(ImageUtils.getAlbumArt(album.id), context),
            contentDescription = stringResource(R.string.artwork),
            modifier = Modifier
                .size(220.dp)
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = album.id),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                )
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = album.name,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            maxLines = 1,
            modifier = Modifier
//                .sharedElement(
//                    sharedContentState = rememberSharedContentState(key = album.name + album.id),
//                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
//                )
                .basicMarquee()
        )
        Text(
            text = album.artist,
            style = MaterialTheme.typography.bodyLargeEmphasized.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .basicMarquee()
//                .sharedElement(
//                    sharedContentState = rememberSharedContentState(key = album.artist + album.id),
//                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
//                )

        )
        Spacer(Modifier.height(15.dp))

        ButtonGroup(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            IconButton(
                onClick = {
                    onHandlePlayerActions(
                        PlayerActions.Play(
                            index = 0,
                            tracks = tracks
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
                        PlayerActions.Play(
                            index = 0,
                            tracks = tracks,
                            random = true
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
                    painter = painterResource(R.drawable.shuffle),
                    contentDescription = null
                )
            }
        }
    }
}

