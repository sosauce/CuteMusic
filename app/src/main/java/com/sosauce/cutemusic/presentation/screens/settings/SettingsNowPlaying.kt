@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberArtworkShape
import com.sosauce.cutemusic.data.datastore.rememberCarousel
import com.sosauce.cutemusic.data.datastore.rememberSliderStyle
import com.sosauce.cutemusic.data.datastore.rememberThumblessSlider
import com.sosauce.cutemusic.presentation.screens.playing.components.rememberCuteSliderState
import com.sosauce.cutemusic.presentation.screens.playing.components.toSlider
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SettingsCards
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.cutemusic.presentation.screens.settings.compenents.ShapeSelector
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SliderSelector
import com.sosauce.cutemusic.presentation.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.presentation.shared_components.LazyRowWithScrollButton
import com.sosauce.cutemusic.utils.ArtworkShape
import com.sosauce.cutemusic.utils.SliderStyle
import com.sosauce.cutemusic.utils.toShape

@Composable
fun SettingsNowPlaying(
    onNavigateUp: () -> Unit
) {

    val scrollState = rememberScrollState()
    var artworkShape by rememberArtworkShape()
    var sliderStyle by rememberSliderStyle()
    var useThumb by rememberThumblessSlider()
    var useCarousel by rememberCarousel()
    val shapes = listOf(
        ArtworkShape.CLASSIC,
        ArtworkShape.CIRCLE,
        ArtworkShape.COOKIE_4,
        ArtworkShape.COOKIE_9,
        ArtworkShape.COOKIE_12,
        ArtworkShape.CLOVER_8,
        ArtworkShape.SUNNY,
        ArtworkShape.ARROW,
        ArtworkShape.DIAMOND,
        ArtworkShape.BUN,
        ArtworkShape.HEART
    )
    val sliders = listOf(
        SliderStyle.WAVY,
        SliderStyle.CLASSIC,
        SliderStyle.MATERIAL3
    )

    Scaffold(
        bottomBar = {
            CuteNavigationButton(onNavigateUp = onNavigateUp)
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(pv)
        ) {
            SettingsWithTitle(
                title = R.string.artwork
            ) {
                Card(
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 4.dp
                    )
                ) {
                    LazyRowWithScrollButton(
                        items = shapes
                    ) { shape ->
                        ShapeSelector(
                            onClick = { artworkShape = shape },
                            shape = shape.toShape(),
                            isSelected = artworkShape == shape
                        )
                    }
                }
//                SettingsCards(
//                    checked = artAsBackground,
//                    onCheckedChange = { artAsBackground = !artAsBackground },
//                    topDp = 4.dp,
//                    bottomDp = 4.dp,
//                    text = stringResource(R.string.art_as_bg)
//                )
                SettingsCards(
                    checked = useCarousel,
                    onCheckedChange = { useCarousel = !useCarousel },
                    topDp = 4.dp,
                    bottomDp = 24.dp,
                    text = stringResource(R.string.use_carousel)
                )
            }
            SettingsWithTitle(
                title = R.string.slider
            ) {
                Card(
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 4.dp
                    )
                ) {
                    LazyRowWithScrollButton(
                        items = sliders
                    ) { slider ->
                        SliderSelector(
                            onClick = { sliderStyle = slider },
                            isSelected = sliderStyle == slider
                        ) { slider.toSlider(rememberCuteSliderState(enabled = false)) }
                    }
                }
                SettingsCards(
                    checked = useThumb,
                    onCheckedChange = { useThumb = !useThumb },
                    topDp = 4.dp,
                    bottomDp = 24.dp,
                    text = stringResource(R.string.thumbless_slider)
                )
            }
        }
    }
}