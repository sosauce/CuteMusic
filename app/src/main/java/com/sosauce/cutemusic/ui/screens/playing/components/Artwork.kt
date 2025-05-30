@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberCarousel
import com.sosauce.cutemusic.data.datastore.rememberNpArtShape
import com.sosauce.cutemusic.data.datastore.rememberShouldApplyShuffle
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.toShape
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlin.math.absoluteValue

@Composable
fun SharedTransitionScope.Artwork(
    pagerModifier: Modifier = Modifier,
    loadedMedias: List<MediaItem> = emptyList(),
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val useCarousel by rememberCarousel()
    val artShape by rememberNpArtShape()
    val useShuffle by rememberShouldApplyShuffle()
    val pagerState =
        rememberPagerState(initialPage = loadedMedias.indexOfFirst { it.mediaId == musicState.mediaId }) { loadedMedias.size }


    if (useCarousel) {
        var lastPage by remember { mutableIntStateOf(loadedMedias.indexOfFirst { it.mediaId == musicState.mediaId }) }

        LaunchedEffect(pagerState.settledPage) {
            if (musicState.mediaIndex == pagerState.settledPage) return@LaunchedEffect
            if (pagerState.settledPage != lastPage) {
                snapshotFlow { pagerState.isScrollInProgress }
                    .filter { !it }
                    .first()
                if (useShuffle) {
                    onHandlePlayerActions(PlayerActions.PlayRandom)
                } else onHandlePlayerActions(PlayerActions.SeekToMusicIndex(pagerState.settledPage))
                lastPage = pagerState.settledPage
            }
        }


        LaunchedEffect(musicState.mediaIndex) {
            pagerState.animateScrollToPage(musicState.mediaIndex)
        }

        HorizontalPager(
            state = pagerState,
            key = { loadedMedias[it].mediaId },
            contentPadding = PaddingValues(horizontal = 30.dp),
            modifier = pagerModifier
        ) { page ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = loadedMedias[page].mediaMetadata.artworkUri,
                    contentDescription = stringResource(R.string.artwork),
                    modifier = Modifier
                        .graphicsLayer {
                            val pageOffset =
                                (pagerState.currentPage - page + pagerState.currentPageOffsetFraction).absoluteValue

                            lerp(
                                start = 75.dp,
                                stop = 100.dp,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleY = scale / 100.dp
                            }
                        }
//                        .sharedElement(
//                            state = rememberSharedContentState(key = SharedTransitionKeys.MUSIC_ARTWORK + musicState.currentMediaId),
//                            animatedVisibilityScope = animatedVisibilityScope
//
//                        )
                        .fillMaxSize(0.95f)
                        .clip(artShape.toShape()),
                    contentScale = ContentScale.Crop
                )
            }
        }

    } else {
        Crossfade(
            targetState = musicState.art,
            modifier = Modifier
                .aspectRatio(1f)
                .wrapContentSize()
        ) {
            AsyncImage(
                model = ImageUtils.imageRequester(it),
                contentDescription = stringResource(R.string.artwork),
                modifier = Modifier
                    .fillMaxSize(0.9f)
//                    .sharedElement(
//                        sharedContentState = rememberSharedContentState(musicState.mediaId),
//                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
//                    )
                    .clip(artShape.toShape()),
                contentScale = ContentScale.Crop
            )
        }
    }
}


