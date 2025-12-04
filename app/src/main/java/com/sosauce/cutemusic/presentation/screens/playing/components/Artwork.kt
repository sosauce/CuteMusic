@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberCarousel
import com.sosauce.cutemusic.data.datastore.rememberNpArtShape
import com.sosauce.cutemusic.data.datastore.rememberShouldApplyShuffle
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.toShape
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlin.math.absoluteValue

@Composable
fun Artwork(
    pagerModifier: Modifier = Modifier,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val context = LocalContext.current
    val useCarousel by rememberCarousel()
    val artShape by rememberNpArtShape()
    val useShuffle by rememberShouldApplyShuffle()
    val pagerState =
        rememberPagerState(initialPage = musicState.loadedMedias.indexOfFirst { it.mediaId == musicState.track.mediaId }
            .takeIf { it != -1 } ?: 0) { musicState.loadedMedias.size }



    if (useCarousel) {
        var lastPage by remember { mutableIntStateOf(musicState.loadedMedias.indexOfFirst { it.mediaId == musicState.track.mediaId }) }

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
            key = { musicState.loadedMedias[it].mediaId },
            //contentPadding = PaddingValues(horizontal = 5.dp),
            modifier = pagerModifier
        ) { page ->

            val image = rememberAsyncImagePainter(musicState.loadedMedias[page].artUri)
            val imageState by image.state.collectAsStateWithLifecycle()

            when (imageState) {
                is AsyncImagePainter.State.Error -> {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .wrapContentSize()
                            .fillMaxSize(0.9f)
                            .clip(artShape.toShape())
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.music_note_rounded),
                            contentDescription = null,
                            modifier = Modifier.size(110.dp),
                            tint = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)

                        )
                    }
                }

                else -> {
                    Image(
                        painter = image,
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
                            .aspectRatio(1f)
                            .wrapContentSize()
                            .fillMaxSize(0.9f)
                            .clip(artShape.toShape()),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

    } else {

        val image = rememberAsyncImagePainter(ImageUtils.imageRequester(musicState.track.artUri, context))
        val imageState by image.state.collectAsStateWithLifecycle()

        when (imageState) {
            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .wrapContentSize()
                        .fillMaxSize()
                        .clip(artShape.toShape())
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.music_note_rounded),
                        contentDescription = null,
                        modifier = Modifier.size(110.dp),
                        tint = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)

                    )
                }
            }

            else -> {
                Image(
                    painter = image,
                    contentDescription = stringResource(R.string.artwork),
                    modifier = Modifier
                        .aspectRatio(1f)
                        .wrapContentSize()
                        .fillMaxSize()
                        .clip(artShape.toShape()),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


