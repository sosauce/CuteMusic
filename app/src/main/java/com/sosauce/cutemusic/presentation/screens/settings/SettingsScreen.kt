@file:OptIn(ExperimentalUuidApi::class)

package com.sosauce.cutemusic.presentation.screens.settings

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.settings.compenents.AboutCard
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SettingsCategoryCard
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SettingsScreens
import com.sosauce.cutemusic.presentation.shared_components.CuteNavigationButton
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    val scrollState = rememberScrollState()
    val backStack = rememberNavBackStack(SettingsScreens.Settings)
    val items = listOf(
        Item(
            icon = R.drawable.palette,
            name = stringResource(R.string.look_and_feel),
            description = stringResource(R.string.look_and_feel_desc),
            onNavigate = { backStack.add(SettingsScreens.LookAndFeel) }
        ),
        Item(
            icon = R.drawable.music_note_rounded,
            name = stringResource(R.string.now_playing),
            description = stringResource(R.string.now_playing_desc),
            onNavigate = { backStack.add(SettingsScreens.NowPlaying) }
        ),
        Item(
            icon = R.drawable.headphones,
            name = stringResource(R.string.playback_controls),
            description = stringResource(R.string.playback_controls_desc),
            onNavigate = { backStack.add(SettingsScreens.Playback) }
        ),
        Item(
            icon = R.drawable.library,
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
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            } else {
                onNavigateUp()
            }
        },
        predictivePopTransitionSpec = {
            ContentTransform(
                fadeIn(),
                slideOutHorizontally { it },
            )
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<SettingsScreens.Settings> {
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
                        AboutCard()
                        Spacer(Modifier.height(20.dp))
                        items.fastForEachIndexed { index, item ->
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

            entry<SettingsScreens.Playback> {
                SettingsPlayback(
                    onNavigateUp = backStack::removeLastOrNull
                )
            }

            entry<SettingsScreens.Library> {

                val viewModel = koinViewModel<SafViewModel>()
                val safTracks by viewModel.safTracks.collectAsStateWithLifecycle()

                SettingsLibrary(
                    safTracksUi = safTracks,
                    musicState = musicState,
                    onNavigate = onNavigate,
                    onHandlePlayerActions = onHandlePlayerActions,
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
    val icon: Int,
    val onNavigate: () -> Unit
)
