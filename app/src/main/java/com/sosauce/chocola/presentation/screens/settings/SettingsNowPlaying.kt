@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberArtworkShape
import com.sosauce.chocola.data.datastore.rememberCarousel
import com.sosauce.chocola.data.datastore.rememberLyricsAlignment
import com.sosauce.chocola.data.datastore.rememberLyricsFontSize
import com.sosauce.chocola.data.datastore.rememberShowAlbumName
import com.sosauce.chocola.data.datastore.rememberSliderStyle
import com.sosauce.chocola.data.datastore.rememberThumblessSlider
import com.sosauce.chocola.presentation.screens.playing.components.rememberCuteSliderState
import com.sosauce.chocola.presentation.screens.playing.components.toSlider
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsDropdownMenu
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsSwitch
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.chocola.presentation.screens.settings.compenents.ShapeSelector
import com.sosauce.chocola.presentation.screens.settings.compenents.SliderSelector
import com.sosauce.chocola.presentation.shared_components.LazyRowWithScrollButton
import com.sosauce.chocola.utils.ArtworkShape
import com.sosauce.chocola.utils.LyricsAlignment
import com.sosauce.chocola.utils.SliderStyle
import com.sosauce.chocola.utils.getItemShape
import com.sosauce.chocola.utils.toShape

@Composable
fun SettingsNowPlaying() {

    val scrollState = rememberScrollState()
    var artworkShape by rememberArtworkShape()
    var sliderStyle by rememberSliderStyle()
    var useThumb by rememberThumblessSlider()
    var useCarousel by rememberCarousel()
    var showAlbumName by rememberShowAlbumName()

    var lyricsAlignment by rememberLyricsAlignment()
    var lyricsFontSize by rememberLyricsFontSize()

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
    val lyricsAlignmentOptions = listOf(
        LyricsAlignment.START,
        LyricsAlignment.CENTERED,
        LyricsAlignment.END
    )

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
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
            SettingsSwitch(
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
            SettingsSwitch(
                checked = useThumb,
                onCheckedChange = { useThumb = !useThumb },
                topDp = 4.dp,
                bottomDp = 24.dp,
                text = stringResource(R.string.thumbless_slider)
            )
        }

        SettingsWithTitle(
            title = R.string.ui
        ) {
            SettingsSwitch(
                checked = showAlbumName,
                onCheckedChange = { showAlbumName = !showAlbumName },
                topDp = 24.dp,
                bottomDp = 24.dp,
                text = stringResource(R.string.show_album_name)
            )
        }
        SettingsWithTitle(
            title = R.string.lyrics
        ) {
            SettingsDropdownMenu(
                value = lyricsAlignment,
                topDp = 24.dp,
                bottomDp = 4.dp,
                text = R.string.alignment
            ) {
                lyricsAlignmentOptions.fastForEachIndexed { index, alignment ->
                    DropdownMenuItem(
                        onClick = { lyricsAlignment = alignment },
                        shape = MenuDefaults.getItemShape(index, lyricsAlignmentOptions.lastIndex),
                        text = { Text(alignment) }
                    )
                }
            }
            SettingsDropdownMenu(
                value = lyricsFontSize,
                topDp = 4.dp,
                bottomDp = 24.dp,
                text = R.string.font_size
            ) {
                (20..40).forEachIndexed { index, size ->
                    DropdownMenuItem(
                        onClick = { lyricsFontSize = size },
                        shape = MenuDefaults.getItemShape(index, 40),
                        text = { Text(size.toString()) }
                    )
                }
            }
        }
    }
}