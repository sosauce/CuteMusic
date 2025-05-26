@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAppTheme
import com.sosauce.cutemusic.data.datastore.rememberShowBackButton
import com.sosauce.cutemusic.data.datastore.rememberShowShuffleButton
import com.sosauce.cutemusic.data.datastore.rememberShowXButton
import com.sosauce.cutemusic.data.datastore.rememberUseSystemFont
import com.sosauce.cutemusic.ui.screens.settings.compenents.SettingsCards
import com.sosauce.cutemusic.ui.screens.settings.compenents.ThemeSelector
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LazyRowWithScrollButton
import com.sosauce.cutemusic.ui.shared_components.MockCuteSearchbar
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.CuteTheme
import com.sosauce.cutemusic.utils.anyDarkColorScheme
import com.sosauce.cutemusic.utils.anyLightColorScheme
import com.sosauce.cutemusic.utils.showCuteSearchbar

@Composable
fun SettingsLookAndFeel(
    onNavigateUp: () -> Unit
) {
    var theme by rememberAppTheme()
    var useSystemFont by rememberUseSystemFont()
    var showXButton by rememberShowXButton()
    var showShuffleButton by rememberShowShuffleButton()
    var showBackButton by rememberShowBackButton()
    val state = rememberLazyListState()
    val themeItems = listOf(
        ThemeItem(
            onClick = { theme = CuteTheme.SYSTEM },
            backgroundColor = if (isSystemInDarkTheme()) anyDarkColorScheme().background else anyLightColorScheme().background,
            text = stringResource(R.string.system),
            isSelected = theme == CuteTheme.SYSTEM,
            iconAndTint = Pair(painterResource(R.drawable.system_theme), if (isSystemInDarkTheme()) anyDarkColorScheme().onBackground else anyLightColorScheme().onBackground)
        ),
        ThemeItem(
            onClick = { theme = CuteTheme.DARK },
            backgroundColor = anyDarkColorScheme().background,
            text = stringResource(R.string.dark_mode),
            isSelected = theme == CuteTheme.DARK,
            iconAndTint = Pair(painterResource(R.drawable.dark_mode), anyDarkColorScheme().onBackground)
        ),
        ThemeItem(
            onClick = { theme = CuteTheme.LIGHT },
            backgroundColor = anyLightColorScheme().background,
            text = stringResource(R.string.light_mode),
            isSelected = theme == CuteTheme.LIGHT,
            iconAndTint = Pair(rememberVectorPainter(Icons.Outlined.LightMode), anyLightColorScheme().onBackground)
        ),
        ThemeItem(
            onClick = { theme = CuteTheme.AMOLED },
            backgroundColor = Color.Black,
            text = stringResource(R.string.amoled_mode),
            isSelected = theme == CuteTheme.AMOLED,
            iconAndTint = Pair(rememberVectorPainter(Icons.Rounded.Contrast), Color.White)
        )
    )
    val fontItems = listOf(
        FontItem(
            onClick = { useSystemFont = false },
            fontStyle = FontStyle.DEFAULT,
            borderColor = if (!useSystemFont) MaterialTheme.colorScheme.primary else Color.Transparent,
            text = {
                Text(
                    text = "Tt",
                    fontFamily = GlobalFont
                )
            },
        ),
        FontItem(
            onClick = { useSystemFont = true },
            fontStyle = FontStyle.SYSTEM,
            borderColor = if (useSystemFont) MaterialTheme.colorScheme.primary else Color.Transparent,
            text = { Text("Tt") }
        )
    )


    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = state,
                contentPadding = paddingValues
            ) {
                item {
                    CuteText(
                        text = stringResource(id = R.string.theme),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        LazyRowWithScrollButton(
                            items = themeItems
                        ) { item ->
                            ThemeSelector(
                                onClick = item.onClick,
                                backgroundColor = item.backgroundColor,
                                text = item.text,
                                isThemeSelected = item.isSelected,
                                icon = {
                                    Icon(
                                        painter = item.iconAndTint.first,
                                        contentDescription = null,
                                        tint = item.iconAndTint.second,
                                    )
                                }
                            )
                        }
                    }
                }
                item {
                    CuteText(
                        text = "Font",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        LazyRowWithScrollButton(
                            items = fontItems
                        ) { item ->
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { item.onClick() }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(50.dp)
                                        .clip(MaterialShapes.Cookie9Sided.toShape())
                                        .border(
                                            width = 2.dp,
                                            color = item.borderColor,
                                            shape = MaterialShapes.Cookie9Sided.toShape()
                                        )
                                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                                    contentAlignment = Alignment.Center
                                ) { item.text() }
                                Spacer(Modifier.weight(1f))
                                CuteText(
                                    text = if (item.fontStyle == FontStyle.SYSTEM) stringResource(R.string.system) else "Default"
                                )
                            }
                        }
                    }
                }
                item {
                    CuteText(
                        text = "CuteSearchbar",
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
                        MockCuteSearchbar(
                            Modifier.fillMaxWidth().padding(10.dp)
                        )
                    }
                    SettingsCards(
                        checked = showXButton,
                        onCheckedChange = { showXButton = !showXButton },
                        topDp = 4.dp,
                        bottomDp = 4.dp,
                        text = stringResource(R.string.show_close_button)
                    )
                    SettingsCards(
                        checked = showShuffleButton,
                        onCheckedChange = { showShuffleButton = !showShuffleButton },
                        topDp = 4.dp,
                        bottomDp = 4.dp,
                        text = stringResource(R.string.show_shuffle_btn)
                    )
                    SettingsCards(
                        checked = showBackButton,
                        onCheckedChange = { showBackButton = !showBackButton },
                        topDp = 4.dp,
                        bottomDp = 24.dp,
                        text = "Show back button"
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

@Immutable
private data class ThemeItem(
    val onClick: () -> Unit,
    val backgroundColor: Color,
    val text: String,
    val isSelected: Boolean,
    val iconAndTint: Pair<Painter, Color>
)

@Immutable
private data class FontItem(
    val onClick: () -> Unit,
    val fontStyle: FontStyle,
    val borderColor: Color,
    val text: @Composable () -> Unit
)

private enum class FontStyle{
    DEFAULT,
    SYSTEM
}