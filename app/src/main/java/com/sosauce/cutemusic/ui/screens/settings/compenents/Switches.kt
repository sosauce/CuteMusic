@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.ui.screens.settings.compenents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberCarousel
import com.sosauce.cutemusic.data.datastore.rememberNpArtShape
import com.sosauce.cutemusic.data.datastore.rememberShowBackButton
import com.sosauce.cutemusic.data.datastore.rememberShowShuffleButton
import com.sosauce.cutemusic.data.datastore.rememberShowXButton
import com.sosauce.cutemusic.data.datastore.rememberUseArtTheme
import com.sosauce.cutemusic.data.datastore.rememberUseSystemFont
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.playing.components.rememberCuteSliderState
import com.sosauce.cutemusic.ui.screens.playing.components.toSlider
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LazyRowWithScrollButton
import com.sosauce.cutemusic.ui.shared_components.MockCuteSearchbar
import com.sosauce.cutemusic.utils.SliderStyle
import com.sosauce.cutemusic.utils.toShape

@Composable
fun Misc(
    onNavigate: (Screen) -> Unit
) {
    //var killService by remember { rememberKillService(context) }

    Column {
        CuteText(
            text = stringResource(id = R.string.misc),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )
        TextSettingsCards(
            text = stringResource(id = R.string.blacklisted_folders),
            onClick = { onNavigate(Screen.Blacklisted) },
            topDp = 24.dp,
            bottomDp = 4.dp
        )
        TextSettingsCards(
            text = stringResource(id = R.string.saf_manager),
            onClick = { onNavigate(Screen.Saf) },
            topDp = 4.dp,
            bottomDp = 24.dp
        )
//        SettingsCards(
//            checked = killService,
//            onCheckedChange = { killService = !killService },
//            topDp = 4.dp,
//            bottomDp = 4.dp,
//            text = "Kill Service"
//        )

    }
}


@Composable
fun UISettings() {
    var useSystemFont by rememberUseSystemFont()
    var showXButton by rememberShowXButton()
    var showShuffleButton by rememberShowShuffleButton()
    var showBackButton by rememberShowBackButton()


    Column {
        CuteText(
            text = stringResource(id = R.string.UI),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomEnd = 4.dp,
                        bottomStart = 4.dp
                    )
                )
        ) {
            Column {
                CuteText(
                    text = "CuteSearchbar",
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                )
                MockCuteSearchbar(
                    Modifier.fillMaxWidth().padding(10.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(0.95f).align(Alignment.CenterHorizontally)
                )
                SwitchWithText(
                    checked = showShuffleButton,
                    onCheckedChange = { showShuffleButton = !showShuffleButton },
                    text = stringResource(R.string.show_shuffle_btn)
                )
                SwitchWithText(
                    checked = showBackButton,
                    onCheckedChange = { showBackButton = !showBackButton },
                    text = "Show back button"
                )
                SwitchWithText(
                    checked = showXButton,
                    onCheckedChange = { showXButton = !showXButton },
                    text = stringResource(id = R.string.show_close_button)
                )
            }
        }
        SettingsCards(
            checked = useSystemFont,
            onCheckedChange = { useSystemFont = !useSystemFont },
            topDp = 4.dp,
            bottomDp = 24.dp,
            text = stringResource(id = R.string.use_sys_font)
        )
    }
}

@Composable
fun NowPlayingSettings() {

    var npArtShape by rememberNpArtShape()
    var useMaterialArt by rememberUseArtTheme()
    var useCarousel by rememberCarousel()

    val shapes = arrayOf(
        RoundedCornerShape(5).toString(),
        MaterialShapes.Square.toString(),
        MaterialShapes.Cookie9Sided.toString(),
        MaterialShapes.Cookie12Sided.toString(),
        MaterialShapes.Clover8Leaf.toString(),
        MaterialShapes.Arrow.toString(),
        MaterialShapes.Sunny.toString()
    )
    val sliders = arrayOf(
        SliderStyle.WAVY,
        SliderStyle.CLASSIC,
        SliderStyle.MATERIAL3
    )


    Column {
        CuteText(
            text = "Now playing",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomEnd = 4.dp,
                        bottomStart = 4.dp
                    )
                )
        ) {
            Column {
                CuteText(
                    text = stringResource(R.string.artwork),
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                )
                LazyRowWithScrollButton(
                    items = shapes.toList()
                ) { shape ->
                    ShapeSelector(
                        onClick = { npArtShape = shape },
                        shape = shape.toShape(),
                        isSelected = npArtShape == shape
                    )
                }
                SwitchWithText(
                    checked = useMaterialArt,
                    onCheckedChange = { useMaterialArt = !useMaterialArt },
                    text = stringResource(R.string.use_art)
                )
                SwitchWithText(
                    checked = useCarousel,
                    onCheckedChange = { useCarousel = !useCarousel },
                    text = stringResource(R.string.use_carousel)
                )
            }
        }
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 2.dp,
                        topEnd = 2.dp,
                        bottomEnd = 24.dp,
                        bottomStart = 24.dp
                    )
                )
        ) {
            Column {
                CuteText(
                    text = "Slider style",
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                )
                LazyRowWithScrollButton(
                    items = sliders.toList()
                ) { slider ->
                    SliderSelector(
                        onClick = {},
                        isSelected = true
                    ) { slider.toSlider(rememberCuteSliderState(enabled = false)) }
                }
            }

        }
    }
}


