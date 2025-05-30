@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.sosauce.cutemusic.ui.screens.album.AlbumDetailsScreen
import com.sosauce.cutemusic.ui.screens.album.AlbumsScreen
import com.sosauce.cutemusic.ui.screens.artist.ArtistDetails
import com.sosauce.cutemusic.ui.screens.artist.ArtistsScreen
import com.sosauce.cutemusic.ui.screens.main.MainScreen
import com.sosauce.cutemusic.ui.screens.metadata.MetadataEditor
import com.sosauce.cutemusic.ui.screens.metadata.MetadataViewModel
import com.sosauce.cutemusic.ui.screens.playing.NowPlaying
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistDetailsScreen
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistsScreen
import com.sosauce.cutemusic.ui.screens.settings.SettingsScreen
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import com.sosauce.cutemusic.utils.CurrentScreen
import com.sosauce.cutemusic.utils.ImageUtils
import org.koin.androidx.compose.koinViewModel

@Composable
fun Nav(onImageLoadSuccess: (ImageBitmap) -> Unit) {

    val backStack = rememberNavBackStack(Screen.Main).apply {
        CurrentScreen.screen = lastOrNull() ?: Screen.Main
    }
    val context = LocalContext.current
    val viewModel = koinViewModel<MusicViewModel>()
    val metadataViewModel = koinViewModel<MetadataViewModel>()
    val musics by viewModel.allTracks.collectAsStateWithLifecycle()
    val musicState by viewModel.musicState.collectAsStateWithLifecycle()
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val artists by viewModel.artists.collectAsStateWithLifecycle()

    LaunchedEffect(musicState.art) {
        ImageUtils.loadNewArt(
            context = context,
            onImageLoadSuccess = onImageLoadSuccess,
            art = musicState.art
        )
    }

    SharedTransitionLayout {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<Screen.Main> {
                    MainScreen(
                        musics = musics,
                        onNavigate = backStack::add,
                        currentlyPlaying = musicState.title,
                        isCurrentlyPlaying = musicState.isPlaying,
                        onLoadMetadata = { path, uri ->
                            metadataViewModel.onHandleMetadataActions(
                                MetadataActions.LoadSong(
                                    path,
                                    uri
                                )
                            )
                        },
                        isPlayerReady = musicState.isPlayerReady,
                        currentMusicUri = musicState.uri,
                        onHandlePlayerAction = viewModel::handlePlayerActions,
                        onHandleMediaItemAction = viewModel::handleMediaItemActions
                    )
                }

                entry<Screen.Albums> {
                    AlbumsScreen(
                        albums = albums,
                        currentlyPlaying = musicState.title,
                        isPlayerReady = musicState.isPlayerReady,
                        isPlaying = musicState.isPlaying,
                        onHandlePlayerActions = viewModel::handlePlayerActions,
                        onNavigate = backStack::add
                    )
                }

                entry<Screen.NowPlaying> {
                    NowPlaying(
                        musicState = musicState,
                        loadedMedias = musics.fastFilter { it.mediaId in musicState.loadedMedias }.sortedBy { musicState.loadedMedias[it.mediaId] },
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
                        onShortClick = { viewModel.handlePlayerActions(PlayerActions.StartPlayback(it)) },
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
                        artist = artists,
                        onNavigate = backStack::add,
                        currentlyPlaying = musicState.title,
                        onHandlePlayerActions = viewModel::handlePlayerActions,
                        isPlaying = musicState.isPlaying,
                        isPlayerReady = musicState.isPlayerReady
                    )
                }

                entry<Screen.ArtistsDetails> { key ->
                    artists.find { artist -> artist.id == key.id }?.let { artist ->
                        ArtistDetails(
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
                        onNavigate = backStack::add,
                        currentlyPlaying = musicState.title,
                        isCurrentlyPlaying = musicState.isPlaying,
                        isPlayerReady = musicState.isPlayerReady,
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
                            isPlayerReady = musicState.isPlayerReady,
                            currentMusicUri = musicState.uri,
                            onHandleMediaItemAction = viewModel::handleMediaItemActions,
                            musics = musics,
                            onNavigateUp = backStack::removeLastOrNull,
                            onHandlePlaylistAction = playlistViewModel::handlePlaylistActions
                        )
                    }
                }

            }
        )
    }
}