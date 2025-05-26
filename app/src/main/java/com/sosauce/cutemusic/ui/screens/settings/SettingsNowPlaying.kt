@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberCarousel
import com.sosauce.cutemusic.data.datastore.rememberNpArtShape
import com.sosauce.cutemusic.data.datastore.rememberSliderStyle
import com.sosauce.cutemusic.data.datastore.rememberThumblessSlider
import com.sosauce.cutemusic.data.datastore.rememberUseArtTheme
import com.sosauce.cutemusic.ui.screens.playing.components.rememberCuteSliderState
import com.sosauce.cutemusic.ui.screens.playing.components.toSlider
import com.sosauce.cutemusic.ui.screens.settings.compenents.SettingsCards
import com.sosauce.cutemusic.ui.screens.settings.compenents.ShapeSelector
import com.sosauce.cutemusic.ui.screens.settings.compenents.SliderSelector
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LazyRowWithScrollButton
import com.sosauce.cutemusic.utils.SliderStyle
import com.sosauce.cutemusic.utils.showCuteSearchbar
import com.sosauce.cutemusic.utils.toShape

@Composable
fun SettingsNowPlaying(
    onNavigateUp: () -> Unit
) {

    val state = rememberLazyListState()
    var npArtShape by rememberNpArtShape()
    var sliderStyle by rememberSliderStyle()
    var useMaterialArt by rememberUseArtTheme()
    var useThumb by rememberThumblessSlider()
    var useCarousel by rememberCarousel()
    val shapes = listOf(
        RoundedCornerShape(5).toString(),
        MaterialShapes.Square.toString(),
        MaterialShapes.Cookie9Sided.toString(),
        MaterialShapes.Cookie12Sided.toString(),
        MaterialShapes.Clover8Leaf.toString(),
        MaterialShapes.Arrow.toString(),
        MaterialShapes.Sunny.toString()
    )
    val sliders = listOf(
        SliderStyle.WAVY,
        SliderStyle.CLASSIC,
        SliderStyle.MATERIAL3
    )

    Scaffold { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = state,
                contentPadding = paddingValues
            ) {
                item {
                    CuteText(
                        text = stringResource(R.string.artwork),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
                    )
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
                                onClick = { npArtShape = shape },
                                shape = shape.toShape(),
                                isSelected = npArtShape == shape
                            )
                        }
                    }
                    SettingsCards(
                        checked = useMaterialArt,
                        onCheckedChange = { useMaterialArt = !useMaterialArt },
                        topDp = 4.dp,
                        bottomDp = 4.dp,
                        text = stringResource(R.string.use_art)
                    )
                    SettingsCards(
                        checked = useCarousel,
                        onCheckedChange = { useCarousel = !useCarousel },
                        topDp = 4.dp,
                        bottomDp = 24.dp,
                        text = stringResource(R.string.use_carousel)
                    )
                }
                item {
                    CuteText(
                        text = "Slider",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
                    )
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
                        text = "Thumbless slider"
                    )
                }
            }
            AnimatedVisibility(
                visible = state.showCuteSearchbar,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .align(Alignment.BottomStart),
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                CuteNavigationButton(
                    modifier = Modifier.navigationBarsPadding()
                ) { onNavigateUp() }
            }
        }
    }
}