package com.sosauce.cutemusic.ui.screens.settings.compenents

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.LocalContext
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAppTheme
import com.sosauce.cutemusic.data.datastore.rememberCarousel
import com.sosauce.cutemusic.data.datastore.rememberShowShuffleButton
import com.sosauce.cutemusic.data.datastore.rememberShowXButton
import com.sosauce.cutemusic.data.datastore.rememberUseArtTheme
import com.sosauce.cutemusic.data.datastore.rememberUseClassicSlider
import com.sosauce.cutemusic.data.datastore.rememberUseSystemFont
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.CuteTheme
import com.sosauce.cutemusic.utils.anyDarkColorScheme
import com.sosauce.cutemusic.utils.anyLightColorScheme

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
fun ThemeManagement() {
    var theme by rememberAppTheme()

    Column {
        CuteText(
            text = stringResource(id = R.string.theme),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
        ) {
            LazyRow {
                item {
                    ThemeSelector(
                        onClick = { theme = CuteTheme.SYSTEM },
                        backgroundColor = if (isSystemInDarkTheme()) anyDarkColorScheme().background else anyLightColorScheme().background,
                        text = stringResource(R.string.system),
                        isThemeSelected = theme == CuteTheme.SYSTEM,
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.system_theme),
                                contentDescription = null,
                                tint = if (isSystemInDarkTheme()) anyDarkColorScheme().onBackground else anyLightColorScheme().onBackground
                            )
                        }
                    )
                }
                item {
                    ThemeSelector(
                        onClick = { theme = CuteTheme.DARK },
                        backgroundColor = anyDarkColorScheme().background,
                        text = stringResource(R.string.dark_mode),
                        isThemeSelected = theme == CuteTheme.DARK,
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.dark_mode),
                                contentDescription = null,
                                tint = anyDarkColorScheme().onBackground
                            )
                        }
                    )
                }
                item {
                    ThemeSelector(
                        onClick = { theme = CuteTheme.LIGHT },
                        backgroundColor = anyLightColorScheme().background,
                        text = "Light",
                        isThemeSelected = theme == CuteTheme.LIGHT,
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.LightMode,
                                contentDescription = null,
                                tint = anyLightColorScheme().onBackground
                            )
                        }
                    )
                }
                item {
                    ThemeSelector(
                        onClick = { theme = CuteTheme.AMOLED },
                        backgroundColor = Color.Black,
                        text = stringResource(R.string.amoled_mode),
                        isThemeSelected = theme == CuteTheme.AMOLED,
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Contrast,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    )
                }
            }
        }
//        SettingsCards(
//            checked = followSys,
//            onCheckedChange = { followSys = !followSys },
//            topDp = 24.dp,
//            bottomDp = 4.dp,
//            text = stringResource(id = R.string.follow_sys),
//            optionalIcon = {
//                Icon(
//                    painter = painterResource(R.drawable.system_theme),
//                    contentDescription = null,
//                )
//            }
//        )
//        AnimatedContent(
//            targetState = !followSys,
//            transitionSpec = { slideInHorizontally { -it } togetherWith slideOutHorizontally { -it } }
//        ) { isVisible ->
//            if (isVisible) {
//                SettingsCards(
//                    checked = darkMode,
//                    onCheckedChange = { darkMode = !darkMode },
//                    topDp = 4.dp,
//                    bottomDp = 4.dp,
//                    text = stringResource(id = R.string.dark_mode),
//                    optionalIcon = {
//                        Icon(
//                            painter = painterResource(R.drawable.dark_mode),
//                            contentDescription = null,
//                        )
//                    }
//                )
//            }
//
//        }
//        SettingsCards(
//            checked = amoledMode,
//            onCheckedChange = { amoledMode = !amoledMode },
//            topDp = 4.dp,
//            bottomDp = 24.dp,
//            text = stringResource(id = R.string.amoled_mode),
//            optionalIcon = {
//                Icon(
//                    imageVector = Icons.Rounded.Contrast,
//                    contentDescription = null,
//                )
//            }
//        )
    }
}

@Composable
fun UISettings() {
    var useClassicSlider by rememberUseClassicSlider()
    var useSystemFont by rememberUseSystemFont()
    var showXButton by rememberShowXButton()
    var showShuffleButton by rememberShowShuffleButton()
    var useArtTheme by rememberUseArtTheme()
    var useCarousel by rememberCarousel()


    Column {
        CuteText(
            text = stringResource(id = R.string.UI),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )
        SettingsCards(
            checked = useClassicSlider,
            onCheckedChange = { useClassicSlider = !useClassicSlider },
            topDp = 24.dp,
            bottomDp = 4.dp,
            text = stringResource(id = R.string.classic_slider)
        )
        SettingsCards(
            checked = useArtTheme,
            onCheckedChange = { useArtTheme = !useArtTheme },
            topDp = 4.dp,
            bottomDp = 4.dp,
            text = stringResource(id = R.string.use_art),
            optionalDescription = {
                CuteText(
                    text = stringResource(R.string.art_description),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                    fontSize = 12.sp

                )
            }
        )
        SettingsCards(
            checked = useCarousel,
            onCheckedChange = { useCarousel = !useCarousel },
            topDp = 4.dp,
            bottomDp = 4.dp,
            text = stringResource(id = R.string.use_carousel)
        )
        SettingsCards(
            checked = showXButton,
            onCheckedChange = { showXButton = !showXButton },
            topDp = 4.dp,
            bottomDp = 4.dp,
            text = stringResource(id = R.string.show_close_button)
        )
        SettingsCards(
            checked = showShuffleButton,
            onCheckedChange = { showShuffleButton = !showShuffleButton },
            topDp = 4.dp,
            bottomDp = 4.dp,
            text = stringResource(id = R.string.show_shuffle_btn)
        )
        SettingsCards(
            checked = useSystemFont,
            onCheckedChange = { useSystemFont = !useSystemFont },
            topDp = 4.dp,
            bottomDp = 24.dp,
            text = stringResource(id = R.string.use_sys_font)
        )
    }
}
