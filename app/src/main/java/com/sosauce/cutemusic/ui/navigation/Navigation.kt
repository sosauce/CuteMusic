@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sosauce.cutemusic.data.actions.MetadataActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberUseNpV2
import com.sosauce.cutemusic.ui.screens.album.AlbumDetailsScreen
import com.sosauce.cutemusic.ui.screens.album.AlbumsScreen
import com.sosauce.cutemusic.ui.screens.artist.ArtistDetails
import com.sosauce.cutemusic.ui.screens.artist.ArtistsScreen
import com.sosauce.cutemusic.ui.screens.blacklisted.BlacklistedScreen
import com.sosauce.cutemusic.ui.screens.main.MainScreen
import com.sosauce.cutemusic.ui.screens.metadata.MetadataEditor
import com.sosauce.cutemusic.ui.screens.metadata.MetadataViewModel
import com.sosauce.cutemusic.ui.screens.playing.NowPlayingScreen
import com.sosauce.cutemusic.ui.screens.playing.NowPlayingV2
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistDetailsScreen
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistsScreen
import com.sosauce.cutemusic.ui.screens.saf.SafScreen
import com.sosauce.cutemusic.ui.screens.settings.SettingsScreen
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.utils.CurrentScreen
import com.sosauce.cutemusic.utils.NAVIGATION_PREFIX
import com.sosauce.cutemusic.utils.navigateSingleTop
import org.koin.androidx.compose.koinViewModel

// https://stackoverflow.com/a/78771053

@Composable
fun Nav(
    viewModel: MusicViewModel
) {

    val navController = rememberNavController().apply {
        addOnDestinationChangedListener { _, destination, _ ->
            CurrentScreen.screen =
                destination.route?.removePrefix(NAVIGATION_PREFIX) ?: Screen.Main.toString()
        }
    }
    val postViewModel = koinViewModel<PostViewModel>()
    val metadataViewModel = koinViewModel<MetadataViewModel>()
    //val tracks by postViewModel.musics.collectAsStateWithLifecycle()
    //val safTracks by postViewModel.safTracks.collectAsStateWithLifecycle()
    val musics by postViewModel.musics.collectAsStateWithLifecycle()
    val musicState by viewModel.musicState.collectAsStateWithLifecycle()
    val albums by postViewModel.albums.collectAsStateWithLifecycle()
    val artists by postViewModel.artists.collectAsStateWithLifecycle()


    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.Main
        ) {
            composable<Screen.Main> {
                MainScreen(
                    musics = musics,
                    onNavigate = { navController.navigateSingleTop(it) },
                    currentlyPlaying = musicState.currentlyPlaying,
                    isCurrentlyPlaying = musicState.isCurrentlyPlaying,
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
                    currentMusicUri = musicState.currentMusicUri,
                    onHandlePlayerAction = { viewModel.handlePlayerActions(it) },
                    onDeleteMusic = { uris, intentSender ->
                        postViewModel.deleteMusic(
                            uris,
                            intentSender
                        )
                    },
                    onChargeAlbumSongs = postViewModel::loadAlbumSongs,
                    onChargeArtistLists = {
                        postViewModel.loadArtistSongs(it)
                        postViewModel.loadArtistAlbums(it)
                    }
                )

            }

            composable<Screen.Albums> {

                AlbumsScreen(
                    albums = albums,
                    animatedVisibilityScope = this,
                    currentlyPlaying = musicState.currentlyPlaying,
                    chargePVMAlbumSongs = postViewModel::loadAlbumSongs,
                    isPlayerReady = musicState.isPlayerReady,
                    isPlaying = musicState.isCurrentlyPlaying,
                    onHandlePlayerActions = viewModel::handlePlayerActions,
                    onNavigate = { navController.navigateSingleTop(it) },
                )
            }
            composable<Screen.NowPlaying> {

                val lyrics by viewModel.lyrics.collectAsStateWithLifecycle()
                val useV2 by rememberUseNpV2()

                if (useV2) {
                    NowPlayingV2(
                        animatedVisibilityScope = this,
                        musicState = musicState,
                        onHandlePlayerActions = viewModel::handlePlayerActions,
                        onNavigateUp = navController::navigateUp,
                        onChargeAlbumSongs = postViewModel::loadAlbumSongs,
                        onChargeArtistLists = {
                            postViewModel.loadArtistSongs(it)
                            postViewModel.loadArtistAlbums(it)
                        },
                        onNavigate = navController::navigateSingleTop,
                        lyrics = lyrics
                    )
                } else {
                    NowPlayingScreen(
                        animatedVisibilityScope = this,
                        musicState = musicState,
                        onChargeAlbumSongs = postViewModel::loadAlbumSongs,
                        onChargeArtistLists = {
                            postViewModel.loadArtistSongs(it)
                            postViewModel.loadArtistAlbums(it)
                        },
                        onNavigate = { navController.navigateSingleTop(it) },
                        onNavigateUp = navController::popBackStack,
                        onHandlePlayerActions = { viewModel.handlePlayerActions(it) },
                        lyrics = lyrics
                    )
                }


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
                        postViewModel = postViewModel,
                        musicState = musicState,
                        animatedVisibilityScope = this,
                        onNavigate = { screen -> navController.navigateSingleTop(screen) },
                    )
                }

            }

            composable<Screen.Artists> {
                ArtistsScreen(
                    artist = artists,
                    onNavigate = { navController.navigateSingleTop(it) },
                    onChargeArtistLists = {
                        postViewModel.loadArtistSongs(it)
                        postViewModel.loadArtistAlbums(it)
                    },
                    currentlyPlaying = musicState.currentlyPlaying,
                    onHandlePlayerActions = viewModel::handlePlayerActions,
                    isPlaying = musicState.isCurrentlyPlaying,
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
                        postViewModel = postViewModel,
                        onNavigate = { screen -> navController.navigateSingleTop(screen) },
                        onNavigateUp = { navController.popBackStack() },
                        musicState = musicState,
                        animatedVisibilityScope = this
                    )
                }
            }
            composable<Screen.Blacklisted> {

                val folders by postViewModel.folders.collectAsStateWithLifecycle()

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
                            postViewModel.editMusic(
                                uris,
                                intentSender
                            )
                        }
                    )
                }
            }

            composable<Screen.Saf> {
                val latestSafTracks by postViewModel.safTracks.collectAsStateWithLifecycle()

                SafScreen(
                    onNavigateUp = navController::navigateUp,
                    latestSafTracks = latestSafTracks,
                    onShortClick = { viewModel.handlePlayerActions(PlayerActions.StartPlayback(it)) },
                    isPlayerReady = musicState.isPlayerReady,
                    currentMusicUri = musicState.currentMusicUri,
                )
            }

            composable<Screen.Playlists> {
                PlaylistsScreen(
                    onNavigate = { navController.navigateSingleTop(it) },
                    currentlyPlaying = musicState.currentlyPlaying,
                    isCurrentlyPlaying = musicState.isCurrentlyPlaying,
                    onNavigationItemClicked = { screen -> navController.navigateSingleTop(screen) },
                    animatedVisibilityScope = this,
                    isPlayerReady = musicState.isPlayerReady,
                    onHandlePlayerAction = { viewModel.handlePlayerActions(it) }
                )
            }

            composable<Screen.PlaylistDetails> {
                val playlistViewModel = koinViewModel<PlaylistViewModel>()
                val index = it.toRoute<Screen.PlaylistDetails>()
                val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

                playlists.find { it.id == index.id }?.let { playlist ->
                    PlaylistDetailsScreen(
                        playlist = playlist,
                        onNavigate = { navController.navigateSingleTop(it) },
                        onShortClick = {
                            viewModel.handlePlayerActions(
                                PlayerActions.StartPlayback(
                                    it
                                )
                            )
                        },
                        isPlayerReady = musicState.isPlayerReady,
                        currentMusicUri = musicState.currentMusicUri,
                        onDeleteMusic = { uris, intentSender ->
                            postViewModel.deleteMusic(
                                uris,
                                intentSender
                            )
                        },
                        onChargeAlbumSongs = postViewModel::loadAlbumSongs,
                        onChargeArtistLists = {
                            postViewModel.loadArtistSongs(it)
                            postViewModel.loadArtistAlbums(it)
                        },
                        musics = musics,
                        onNavigateUp = navController::navigateUp
                    )
                }


            }
        }
    }
}