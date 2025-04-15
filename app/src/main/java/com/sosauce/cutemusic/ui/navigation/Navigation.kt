@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sosauce.cutemusic.data.actions.MetadataActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.screens.album.AlbumDetailsScreen
import com.sosauce.cutemusic.ui.screens.album.AlbumsScreen
import com.sosauce.cutemusic.ui.screens.artist.ArtistDetails
import com.sosauce.cutemusic.ui.screens.artist.ArtistsScreen
import com.sosauce.cutemusic.ui.screens.blacklisted.BlacklistedScreen
import com.sosauce.cutemusic.ui.screens.main.MainScreen
import com.sosauce.cutemusic.ui.screens.metadata.MetadataEditor
import com.sosauce.cutemusic.ui.screens.metadata.MetadataViewModel
import com.sosauce.cutemusic.ui.screens.playing.NowPlaying
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistDetailsScreen
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistsScreen
import com.sosauce.cutemusic.ui.screens.saf.SafScreen
import com.sosauce.cutemusic.ui.screens.settings.SettingsScreen
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import com.sosauce.cutemusic.utils.CurrentScreen
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.NAVIGATION_PREFIX
import com.sosauce.cutemusic.utils.navigateSingleTop
import org.koin.androidx.compose.koinViewModel

@Composable
fun Nav(onImageLoadSuccess: (ImageBitmap) -> Unit) {

    val navController = rememberNavController().apply {
        addOnDestinationChangedListener { _, destination, _ ->
            CurrentScreen.screen =
                destination.route?.removePrefix(NAVIGATION_PREFIX) ?: Screen.Main.toString()
        }
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
        NavHost(
            navController = navController,
            startDestination = Screen.Main
        ) {
            composable<Screen.Main> {
                MainScreen(
                    musics = musics,
                    onNavigate = navController::navigateSingleTop,
                    currentlyPlaying = musicState.title,
                    isCurrentlyPlaying = musicState.isPlaying,
                    onShortClick = { viewModel.handlePlayerActions(PlayerActions.StartPlayback(it)) },
                    animatedVisibilityScope = this,
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
                    onHandlePlayerAction = { viewModel.handlePlayerActions(it) },
                    onChargeAlbumSongs = viewModel::loadAlbumSongs,
                    onChargeArtistLists = viewModel::loadArtistData,
                    onDeleteMusic = { uris, intentSender ->
                        viewModel.deleteMusic(
                            uris,
                            intentSender
                        )
                    }
                )

            }

            composable<Screen.Albums> {

                AlbumsScreen(
                    albums = albums,
                    animatedVisibilityScope = this,
                    currentlyPlaying = musicState.title,
                    chargePVMAlbumSongs = viewModel::loadAlbumSongs,
                    isPlayerReady = musicState.isPlayerReady,
                    isPlaying = musicState.isPlaying,
                    onHandlePlayerActions = viewModel::handlePlayerActions,
                    onNavigate = navController::navigateSingleTop,

                    )
            }
            composable<Screen.NowPlaying> {

                val lyrics by viewModel.lyrics.collectAsStateWithLifecycle()
                val loadedMedias by viewModel.loadedMedias.collectAsStateWithLifecycle()

                NowPlaying(
                    animatedVisibilityScope = this,
                    musicState = musicState,
                    loadedMedias = loadedMedias,
                    onHandlePlayerActions = viewModel::handlePlayerActions,
                    onNavigateUp = navController::navigateUp,
                    onChargeAlbumSongs = viewModel::loadAlbumSongs,
                    onChargeArtistLists = viewModel::loadArtistData,
                    onNavigate = navController::navigateSingleTop,
                    lyrics = lyrics
                )
            }
            composable<Screen.Settings> {
                SettingsScreen(
                    onNavigateUp = navController::navigateUp,
                    onNavigate = navController::navigateSingleTop
                )
            }
            composable<Screen.AlbumsDetails> {
                val index = it.toRoute<Screen.AlbumsDetails>()
                albums.find { album -> album.id == index.id }?.let { album ->
                    AlbumDetailsScreen(
                        album = album,
                        viewModel = viewModel,
                        onNavigateUp = navController::navigateUp,
                        musicState = musicState,
                        animatedVisibilityScope = this,
                        onNavigate = navController::navigateSingleTop,
                        onDeleteMusic = viewModel::deleteMusic,
                        onChargeAlbumSongs = viewModel::loadAlbumSongs,
                        onChargeArtistLists = viewModel::loadArtistData,
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

            composable<Screen.Artists> {
                ArtistsScreen(
                    artist = artists,
                    onNavigate = navController::navigateSingleTop,
                    onChargeArtistLists = viewModel::loadArtistData,
                    currentlyPlaying = musicState.title,
                    onHandlePlayerActions = viewModel::handlePlayerActions,
                    isPlaying = musicState.isPlaying,
                    animatedVisibilityScope = this,
                    isPlayerReady = musicState.isPlayerReady
                )
            }
            composable<Screen.ArtistsDetails> {
                val index = it.toRoute<Screen.ArtistsDetails>()
                artists.find { artist -> artist.id == index.id }?.let { artist ->
                    ArtistDetails(
                        artist = artist,
                        viewModel = viewModel,
                        onNavigate = navController::navigateSingleTop,
                        onNavigateUp = navController::navigateUp,
                        musicState = musicState,
                        animatedVisibilityScope = this,
                        onDeleteMusic = viewModel::deleteMusic,
                        onChargeAlbumSongs = viewModel::loadAlbumSongs,
                        onChargeArtistLists = viewModel::loadArtistData,
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
            composable<Screen.Blacklisted> {

                val folders by viewModel.folders.collectAsStateWithLifecycle()

                BlacklistedScreen(
                    folders = folders,
                    onNavigateUp = navController::navigateUp
                )
            }
            composable<Screen.MetadataEditor> {
                val index = it.toRoute<Screen.MetadataEditor>()
                musics.find { music -> music.mediaId == index.id }?.let { music ->
                    MetadataEditor(
                        music = music,
                        onNavigateUp = navController::navigateUp,
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

            composable<Screen.Saf> {
                val latestSafTracks by viewModel.safTracks.collectAsStateWithLifecycle()

                SafScreen(
                    onNavigateUp = navController::navigateUp,
                    latestSafTracks = latestSafTracks,
                    onShortClick = { viewModel.handlePlayerActions(PlayerActions.StartPlayback(it)) },
                    isPlayerReady = musicState.isPlayerReady,
                    currentMusicUri = musicState.uri,
                )
            }

            composable<Screen.Playlists> {
                PlaylistsScreen(
                    onNavigate = navController::navigateSingleTop,
                    currentlyPlaying = musicState.title,
                    isCurrentlyPlaying = musicState.isPlaying,
                    animatedVisibilityScope = this,
                    isPlayerReady = musicState.isPlayerReady,
                    onHandlePlayerAction = viewModel::handlePlayerActions
                )
            }

            composable<Screen.PlaylistDetails> {
                val playlistViewModel = koinViewModel<PlaylistViewModel>()
                val index = it.toRoute<Screen.PlaylistDetails>()
                val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

                playlists.find { it.id == index.id }?.let { playlist ->
                    PlaylistDetailsScreen(
                        playlist = playlist,
                        onNavigate = navController::navigateSingleTop,
                        onShortClick = {
                            viewModel.handlePlayerActions(
                                PlayerActions.StartPlayback(
                                    it
                                )
                            )
                        },
                        isPlayerReady = musicState.isPlayerReady,
                        currentMusicUri = musicState.uri,
                        onDeleteMusic = viewModel::deleteMusic,
                        onChargeAlbumSongs = viewModel::loadAlbumSongs,
                        onChargeArtistLists = viewModel::loadArtistData,
                        musics = musics,
                        onNavigateUp = navController::navigateUp
                    )
                }
            }
        }
    }
}