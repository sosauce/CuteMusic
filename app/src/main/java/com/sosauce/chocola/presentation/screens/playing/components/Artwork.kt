@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class
)

package com.sosauce.chocola.presentation.screens.playing.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberArtworkShape
import com.sosauce.chocola.data.datastore.rememberCarousel
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.utils.ArtworkShape
import com.sosauce.chocola.utils.ImageUtils
import com.sosauce.chocola.utils.ignoreParentPadding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun Artwork(
    pagerModifier: Modifier = Modifier,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val useCarousel by rememberCarousel()
    var artworkShape by rememberArtworkShape()

    if (useCarousel) {
        val carouselState =
            rememberCarouselState(initialItem = musicState.mediaIndex) { musicState.loadedMedias.size }

        var isProgrammaticScroll by remember { mutableStateOf(false) }

        // Sync carousel position when playback changes track externally
        LaunchedEffect(musicState.mediaIndex) {
            if (!carouselState.isScrollInProgress &&
                carouselState.currentItem != musicState.mediaIndex
            ) {
                isProgrammaticScroll = true
                carouselState.animateScrollToItem(musicState.mediaIndex)
                isProgrammaticScroll = false
            }
        }

        // Use rememberUpdatedState to always read latest values inside the long-lived LaunchedEffect
        val currentMediaIndex by rememberUpdatedState(musicState.mediaIndex)
        val currentShuffle by rememberUpdatedState(musicState.shuffle)
        val currentTrackCount by rememberUpdatedState(musicState.loadedMedias.size)

        // Dispatch track change when user finishes swiping
        LaunchedEffect(carouselState) {
            snapshotFlow { carouselState.isScrollInProgress }
                .filter { !it }
                .map { carouselState.currentItem }
                .distinctUntilChanged()
                .collectLatest { settledItem ->
                    if (currentTrackCount == 0) return@collectLatest
                    val safeIndex = settledItem.coerceIn(0, currentTrackCount - 1)
                    if (isProgrammaticScroll) return@collectLatest
                    if (safeIndex != currentMediaIndex) {
                        if (currentShuffle) {
                            if (safeIndex > currentMediaIndex) {
                                onHandlePlayerActions(PlayerActions.SeekToNextMusic)
                            } else {
                                onHandlePlayerActions(PlayerActions.SeekToPreviousMusic)
                            }
                        } else {
                            onHandlePlayerActions(PlayerActions.SeekToMusicIndex(safeIndex))
                        }
                    }
                }
        }

        HorizontalCenteredHeroCarousel(
            state = carouselState,
            modifier = pagerModifier
                .ignoreParentPadding()
                .aspectRatio(1f)
                .wrapContentSize()
                .fillMaxSize(),
            itemSpacing = 10.dp
        ) { page ->
            val image = rememberAsyncImagePainter(musicState.loadedMedias[page].artUri)
            val imageState by image.state.collectAsStateWithLifecycle()

            when (imageState) {
                is AsyncImagePainter.State.Error -> ErrorImage()
                else -> {
                    Image(
                        painter = image,
                        contentDescription = stringResource(R.string.artwork),
                        modifier = Modifier
                            .aspectRatio(1f)
                            .wrapContentSize()
                            .fillMaxSize()
                            .maskClip(MaterialTheme.shapes.extraLarge),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

    } else {

        val image =
            rememberAsyncImagePainter(musicState.track.artUri)
        val imageState by image.state.collectAsStateWithLifecycle()

        when (imageState) {
            is AsyncImagePainter.State.Error -> ErrorImage()
            else -> {
                Image(
                    painter = image,
                    contentDescription = stringResource(R.string.artwork),
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(ArtworkShape.toShape(artworkShape)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun ErrorImage() {

    var artworkShape by rememberArtworkShape()

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = ArtworkShape.toShape(artworkShape)
            ),
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


