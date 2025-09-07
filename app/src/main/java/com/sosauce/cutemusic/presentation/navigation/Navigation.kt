@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.presentation.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.sosauce.cutemusic.data.actions.MetadataActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberAlbumGrids
import com.sosauce.cutemusic.presentation.screens.album.AlbumDetailsScreen
import com.sosauce.cutemusic.presentation.screens.album.AlbumsScreen
import com.sosauce.cutemusic.presentation.screens.artist.ArtistDetailsScreen
import com.sosauce.cutemusic.presentation.screens.artist.ArtistsScreen
import com.sosauce.cutemusic.presentation.screens.main.MainScreen
import com.sosauce.cutemusic.presentation.screens.metadata.MetadataEditor
import com.sosauce.cutemusic.presentation.screens.metadata.MetadataViewModel
import com.sosauce.cutemusic.presentation.screens.playing.NowPlaying
import com.sosauce.cutemusic.presentation.screens.playlists.PlaylistDetailsScreen
import com.sosauce.cutemusic.presentation.screens.playlists.PlaylistViewModel
import com.sosauce.cutemusic.presentation.screens.playlists.PlaylistsScreen
import com.sosauce.cutemusic.presentation.screens.settings.SettingsScreen
import com.sosauce.cutemusic.presentation.shared_components.MusicViewModel
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.LocalScreen
import com.sosauce.cutemusic.utils.LocalSharedTransitionScope
import org.koin.androidx.compose.koinViewModel

@Composable
fun Nav(onImageLoadSuccess: (ImageBitmap) -> Unit) {

    val backStack = rememberNavBackStack(Screen.Main)
    val currentScreen by remember {
        derivedStateOf { backStack.lastOrNull() ?: Screen.Main }
    }
    val context = LocalContext.current
    val viewModel = koinViewModel<MusicViewModel>()
    val metadataViewModel = koinViewModel<MetadataViewModel>()
    val musics by viewModel.allTracks.collectAsStateWithLifecycle()
    val musicState by viewModel.musicState.collectAsStateWithLifecycle()
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val artists by viewModel.artists.collectAsStateWithLifecycle()
    var numberOfAlbumGrids by rememberAlbumGrids()

    LaunchedEffect(musicState.art) {
        ImageUtils.loadNewArt(
            context = context,
            onImageLoadSuccess = onImageLoadSuccess,
            art = musicState.art
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
                predictivePopTransitionSpec = {
                    ContentTransform(
                        fadeIn(),
                        slideOutHorizontally { it },
                    )
                },
                entryProvider = entryProvider {
                    entry<Screen.Main> {
                        MainScreen(
                            musics = musics,
                            musicState = musicState,
                            onNavigate = backStack::add,
                            onLoadMetadata = { path, uri ->
                                metadataViewModel.onHandleMetadataActions(
                                    MetadataActions.LoadSong(
                                        path,
                                        uri
                                    )
                                )
                            },
                            onHandlePlayerAction = viewModel::handlePlayerActions,
                            onHandleMediaItemAction = viewModel::handleMediaItemActions
                        )
                    }

                    entry<Screen.Albums> {
                        AlbumsScreen(
                            albums = albums,
                            musicState = musicState,
                            numberOfAlbumGrids = numberOfAlbumGrids,
                            onChangeNumberOfGrids = {
                                numberOfAlbumGrids =
                                    if (numberOfAlbumGrids == 4) 2 else numberOfAlbumGrids + 1
                            },
                            onHandlePlayerActions = viewModel::handlePlayerActions,
                            onNavigate = backStack::add
                        )
                    }

                    entry<Screen.NowPlaying> {
                        NowPlaying(
                            musicState = musicState,
                            loadedMedias = musics.fastFilter { it.mediaId in musicState.loadedMedias }
                                .sortedBy { musicState.loadedMedias[it.mediaId] },
                            onHandlePlayerActions = viewModel::handlePlayerActions,
                            onNavigateUp = backStack::removeLastOrNull,
                            onNavigate = backStack::add,
                        )
                    }

                    entry<Screen.Settings> {
                        val folders by viewModel.folders.collectAsStateWithLifecycle()
                        val latestSafTracks by viewModel.safTracks.collectAsStateWithLifecycle()

                        SettingsScreen(
                            onNavigateUp = backStack::removeLastOrNull,
                            folders = folders,
                            latestSafTracks = latestSafTracks,
                            onShortClick = {
                                viewModel.handlePlayerActions(
                                    PlayerActions.StartPlayback(
                                        it
                                    )
                                )
                            },
                            isPlayerReady = musicState.isPlayerReady,
                            currentMusicUri = musicState.uri,
                        )
                    }

                    entry<Screen.AlbumsDetails> { key ->
                        albums.find { album -> album.id == key.id }?.let { album ->
                            AlbumDetailsScreen(
                                musics = musics.fastFilter { it.mediaMetadata.albumTitle == album.name },
                                album = album,
                                onNavigateUp = backStack::removeLastOrNull,
                                musicState = musicState,
                                onNavigate = backStack::add,
                                onHandleMediaItemAction = viewModel::handleMediaItemActions,
                                onHandlePlayerActions = viewModel::handlePlayerActions,
                                onLoadMetadata = { path, uri ->
                                    metadataViewModel.onHandleMetadataActions(
                                        MetadataActions.LoadSong(
                                            path,
                                            uri
                                        )
                                    )
                                }
                            )
                        }
                    }

                    entry<Screen.Artists> {
                        ArtistsScreen(
                            artists = artists,
                            musicState = musicState,
                            onNavigate = backStack::add,
                            onHandlePlayerActions = viewModel::handlePlayerActions,
                        )
                    }

                    entry<Screen.ArtistsDetails> { key ->
                        artists.find { artist -> artist.id == key.id }?.let { artist ->
                            ArtistDetailsScreen(
                                musics = musics.fastFilter { it.mediaMetadata.artist.toString() == artist.name },
                                albums = albums.fastFilter { it.artist == artist.name },
                                artist = artist,
                                onNavigate = backStack::add,
                                onNavigateUp = backStack::removeLastOrNull,
                                onHandlePlayerAction = viewModel::handlePlayerActions,
                                musicState = musicState,
                                onHandleMediaItemAction = viewModel::handleMediaItemActions,
                                onLoadMetadata = { path, uri ->
                                    metadataViewModel.onHandleMetadataActions(
                                        MetadataActions.LoadSong(
                                            path,
                                            uri
                                        )
                                    )
                                }
                            )
                        }
                    }

                    entry<Screen.MetadataEditor> { key ->
                        musics.find { music -> music.mediaId == key.id }?.let { music ->
                            MetadataEditor(
                                music = music,
                                onNavigateUp = backStack::removeLastOrNull,
                                metadataViewModel = metadataViewModel,
                                onEditMusic = { uris, intentSender ->
                                    viewModel.editMusic(
                                        uris,
                                        intentSender
                                    )
                                }
                            )
                        }
                    }

                    entry<Screen.Playlists> {
                        PlaylistsScreen(
                            musicState = musicState,
                            onNavigate = backStack::add,
                            onHandlePlayerAction = viewModel::handlePlayerActions
                        )
                    }

                    entry<Screen.PlaylistDetails> { key ->
                        val playlistViewModel = koinViewModel<PlaylistViewModel>()
                        val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

                        playlists.find { it.id == key.id }?.let { playlist ->
                            PlaylistDetailsScreen(
                                playlist = playlist,
                                musicState = musicState,
                                onNavigate = backStack::add,
                                onHandlePlayerAction = viewModel::handlePlayerActions,
                                onHandleMediaItemAction = viewModel::handleMediaItemActions,
                                onLoadMetadata = { path, uri ->
                                    metadataViewModel.onHandleMetadataActions(
                                        MetadataActions.LoadSong(
                                            path,
                                            uri
                                        )
                                    )
                                },
                                musics = musics.filter { it.mediaId in playlist.musics },
                                onNavigateUp = backStack::removeLastOrNull,
                                onHandlePlaylistAction = playlistViewModel::handlePlaylistActions
                            )
                        }
                    }

                }
            )
        }
    }
}

