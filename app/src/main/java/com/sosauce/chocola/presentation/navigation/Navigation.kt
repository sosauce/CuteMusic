@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.chocola.presentation.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.sosauce.chocola.presentation.screens.album.AlbumDetailsScreen
import com.sosauce.chocola.presentation.screens.album.AlbumDetailsViewModel
import com.sosauce.chocola.presentation.screens.album.AlbumsScreen
import com.sosauce.chocola.presentation.screens.album.AlbumsViewModel
import com.sosauce.chocola.presentation.screens.artist.ArtistDetailsScreen
import com.sosauce.chocola.presentation.screens.artist.ArtistDetailsViewModel
import com.sosauce.chocola.presentation.screens.artist.ArtistsScreen
import com.sosauce.chocola.presentation.screens.artist.ArtistsViewModel
import com.sosauce.chocola.presentation.screens.lyrics.LyricsScreen
import com.sosauce.chocola.presentation.screens.lyrics.LyricsViewModel
import com.sosauce.chocola.presentation.screens.main.MainScreen
import com.sosauce.chocola.presentation.screens.main.MainViewModel
import com.sosauce.chocola.presentation.screens.metadata.MetadataEditor
import com.sosauce.chocola.presentation.screens.metadata.MetadataViewModel
import com.sosauce.chocola.presentation.screens.playing.QueueScreen
import com.sosauce.chocola.presentation.screens.playlists.PlaylistDetailsScreen
import com.sosauce.chocola.presentation.screens.playlists.PlaylistDetailsViewModel
import com.sosauce.chocola.presentation.screens.playlists.PlaylistViewModel
import com.sosauce.chocola.presentation.screens.playlists.PlaylistsScreen
import com.sosauce.chocola.presentation.screens.settings.SettingsScreen
import com.sosauce.chocola.presentation.screens.setup.SetupScreen
import com.sosauce.chocola.presentation.shared_components.MusicViewModel
import com.sosauce.chocola.utils.ImageUtils
import com.sosauce.chocola.utils.LocalScreen
import com.sosauce.chocola.utils.LocalSharedTransitionScope
import com.sosauce.chocola.utils.hasMusicPermission
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun Nav(onImageLoad: (ImageBitmap?) -> Unit) {

    val context = LocalContext.current
    val startScreen = if (context.hasMusicPermission()) Screen.Main else Screen.Setup
    val backStack = rememberNavBackStack(startScreen)
    val currentScreen by remember {
        derivedStateOf { backStack.lastOrNull() ?: Screen.Main }
    }
    val musicViewModel = koinViewModel<MusicViewModel>()
    val musicState by musicViewModel.musicState.collectAsStateWithLifecycle()

    LaunchedEffect(musicState.track.artUri) {
        ImageUtils.loadNewArt(
            context = context,
            onImageLoad = onImageLoad,
            art = musicState.track.artUri
        )
    }
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this,
            LocalScreen provides currentScreen
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                predictivePopTransitionSpec = {
                    ContentTransform(
                        fadeIn(),
                        slideOutHorizontally { it },
                    )
                },
                entryProvider = entryProvider {

                    entry<Screen.Setup> {
                        SetupScreen(
                            onNavigateToApp = {
                                backStack.clear()
                                backStack.add(Screen.Main)
                            }
                        )
                    }

                    entry<Screen.Main> {

                        val viewModel = koinViewModel<MainViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()


                        MainScreen(
                            state = state,
                            musicState = musicState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerAction = musicViewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.Albums> {

                        val viewModel = koinViewModel<AlbumsViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        AlbumsScreen(
                            state = state,
                            musicState = musicState,
                            onHandlePlayerActions = musicViewModel::handlePlayerActions,
                            onNavigate = backStack::navigate
                        )
                    }

                    entry<Screen.Settings> {
                        SettingsScreen(
                            onNavigateUp = backStack::removeLastOrNull,
                            musicState = musicState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerActions = musicViewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.AlbumsDetails> { key ->

                        val viewModel = koinViewModel<AlbumDetailsViewModel>(
                            parameters = { parametersOf(key.name) }
                        )
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        AlbumDetailsScreen(
                            state = state,
                            onNavigateUp = backStack::removeLastOrNull,
                            musicState = musicState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerActions = musicViewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.Artists> {

                        val viewModel = koinViewModel<ArtistsViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        ArtistsScreen(
                            state = state,
                            musicState = musicState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerActions = musicViewModel::handlePlayerActions,
                        )
                    }

                    entry<Screen.ArtistsDetails> { key ->

                        val viewModel = koinViewModel<ArtistDetailsViewModel>(
                            parameters = { parametersOf(key.name) }
                        )
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        ArtistDetailsScreen(
                            state = state,
                            onNavigate = backStack::navigate,
                            onNavigateUp = backStack::removeLastOrNull,
                            onHandlePlayerAction = musicViewModel::handlePlayerActions,
                            musicState = musicState
                        )
                    }

                    entry<Screen.MetadataEditor> { key ->

                        val metadataViewModel = koinViewModel<MetadataViewModel>(
                            parameters = { parametersOf(key.trackPath) }
                        )

                        MetadataEditor(
                            trackUri = key.trackUri.toUri(),
                            fileName = key.trackPath.substringAfterLast("/"),
                            onNavigateUp = backStack::removeLastOrNull,
                            metadataViewModel = metadataViewModel
                        )
                    }

                    entry<Screen.Playlists> {

                        val playlistViewModel = koinViewModel<PlaylistViewModel>()
                        val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

                        PlaylistsScreen(
                            playlists = playlists,
                            onHandlePlaylistAction = playlistViewModel::handlePlaylistActions,
                            musicState = musicState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerAction = musicViewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.PlaylistDetails> { key ->
                        val viewModel = koinViewModel<PlaylistDetailsViewModel>(
                            parameters = { parametersOf(key.id) }
                        )
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        PlaylistDetailsScreen(
                            state = state,
                            musicState = musicState,
                            onNavigate = backStack::navigate,
                            onHandlePlayerAction = musicViewModel::handlePlayerActions,
                            onNavigateUp = backStack::removeLastOrNull,
                            onHandlePlaylistAction = viewModel::handlePlaylistActions
                        )
                    }

                    entry<Screen.Queue> {
                        QueueScreen(
                            musicState = musicState,
                            onNavigateUp = backStack::removeLastOrNull,
                            onHandlePlayerAction = musicViewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.Lyrics> { key ->
                        val viewModel = koinViewModel<LyricsViewModel>(
                            parameters = { parametersOf(key.trackPath) }
                        )
                        val lyrics by viewModel.lyrics.collectAsStateWithLifecycle()
                        LyricsScreen(
                            onNavigateBack = backStack::removeLastOrNull,
                            lyrics = lyrics,
                            musicState = musicState,
                            onHandlePlayerActions = musicViewModel::handlePlayerActions
                        )
                    }
                }
            )
        }
    }
}

fun NavBackStack<NavKey>.navigate(screen: NavKey) {
    remove(screen)
    add(screen)
}

