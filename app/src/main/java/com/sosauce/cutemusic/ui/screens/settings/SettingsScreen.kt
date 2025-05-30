@file:OptIn(ExperimentalUuidApi::class)

package com.sosauce.cutemusic.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.domain.model.Folder
import com.sosauce.cutemusic.ui.screens.settings.compenents.AboutCard
import com.sosauce.cutemusic.ui.screens.settings.compenents.SettingsCategoryCard
import com.sosauce.cutemusic.ui.screens.settings.compenents.SettingsScreens
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.utils.showCuteSearchbar
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    folders: List<Folder>,
    latestSafTracks: List<MediaItem>,
    onShortClick: (String) -> Unit,
    isPlayerReady: Boolean,
    currentMusicUri: String
) {
    val listState = rememberLazyListState()
    val backStack = rememberNavBackStack(SettingsScreens.Settings)
    val items = arrayOf(
        Item(
            icon = rememberVectorPainter(Icons.Outlined.Palette),
            name = stringResource(R.string.look_and_feel),
            description = stringResource(R.string.look_and_feel_desc),
            onNavigate = { backStack.add(SettingsScreens.LookAndFeel) }
        ),
        Item(
            icon = painterResource(R.drawable.music_note_rounded),
            name = stringResource(R.string.now_playing),
            description = stringResource(R.string.now_playing_desc),
            onNavigate = { backStack.add(SettingsScreens.NowPlaying) }
        ),
        Item(
            icon = painterResource(R.drawable.library),
            name = stringResource(R.string.library),
            description = stringResource(R.string.library_desc),
            onNavigate = { backStack.add(SettingsScreens.Library) }
        ),
//        Item(
//            icon = rememberVectorPainter(Icons.Outlined.MoreHoriz),
//            name = stringResource(R.string.more),
//            description = stringResource(R.string.more_desc),
//            onNavigate = { backStack.add() }
//        )

    )

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<SettingsScreens.Settings> {
                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { paddingValues ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            contentPadding = paddingValues,
                            state = listState
                        ) {
                            item { AboutCard() }
                            item { Spacer(Modifier.height(20.dp)) }
                            itemsIndexed(
                                items = items,
                                key = { _, item -> item.id }
                            ) { index, item ->
                                SettingsCategoryCard(
                                    icon = item.icon,
                                    name = item.name,
                                    description = item.description,
                                    topDp = if (index == 0) 24.dp else 4.dp,
                                    bottomDp = if (index == items.lastIndex) 24.dp else 4.dp,
                                    onNavigate = item.onNavigate
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = listState.showCuteSearchbar,
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

            entry<SettingsScreens.LookAndFeel> {
                SettingsLookAndFeel(
                    onNavigateUp = backStack::removeLastOrNull
                )
            }

            entry<SettingsScreens.NowPlaying> {
                SettingsNowPlaying(
                    onNavigateUp = backStack::removeLastOrNull
                )
            }

            entry<SettingsScreens.Library> {
                SettingsLibrary(
                    folders = folders,
                    latestSafTracks = latestSafTracks,
                    onShortClick = onShortClick,
                    isPlayerReady = isPlayerReady,
                    currentMusicUri = currentMusicUri,
                    onNavigateUp = backStack::removeLastOrNull
                )
            }

        }
    )
}

@Immutable
private data class Item(
    val id: String = Uuid.random().toString(),
    val name: String,
    val description: String,
    val icon: Painter,
    val onNavigate: () -> Unit
)
