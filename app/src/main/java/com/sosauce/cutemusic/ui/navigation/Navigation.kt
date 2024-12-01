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
import com.sosauce.cutemusic.ui.screens.album.AlbumDetailsScreen
import com.sosauce.cutemusic.ui.screens.album.AlbumsScreen
import com.sosauce.cutemusic.ui.screens.all_folders.AllFoldersScreen
import com.sosauce.cutemusic.ui.screens.artist.ArtistDetails
import com.sosauce.cutemusic.ui.screens.artist.ArtistsScreen
import com.sosauce.cutemusic.ui.screens.blacklisted.BlacklistedScreen
import com.sosauce.cutemusic.ui.screens.main.MainScreen
import com.sosauce.cutemusic.ui.screens.metadata.MetadataEditor
import com.sosauce.cutemusic.ui.screens.metadata.MetadataViewModel
import com.sosauce.cutemusic.ui.screens.playing.NowPlayingScreen
import com.sosauce.cutemusic.ui.screens.settings.SettingsScreen
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import org.koin.androidx.compose.koinViewModel

// https://stackoverflow.com/a/78771053


@Composable
fun Nav() {

    val navController = rememberNavController()
    val viewModel = koinViewModel<MusicViewModel>()
    val postViewModel = koinViewModel<PostViewModel>()
    val metadataViewModel = koinViewModel<MetadataViewModel>()
    val musics by postViewModel.musics.collectAsStateWithLifecycle()
    val musicState by viewModel.musicState.collectAsStateWithLifecycle()
    val albums by postViewModel.albums.collectAsStateWithLifecycle()


    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.Main
        ) {
            composable<Screen.Main> {
                MainScreen(
                    musics = musics,
                    selectedIndex = viewModel.selectedItem,
                    onNavigate = { navController.navigate(it) },
                    currentlyPlaying = musicState.currentlyPlaying,
                    isCurrentlyPlaying = musicState.isCurrentlyPlaying,
                    onShortClick = { viewModel.handlePlayerActions(PlayerActions.StartPlayback(it)) },
                    onNavigationItemClicked = { index, item ->
                        navController.navigate(item.navigateTo) {
                            viewModel.selectedItem = index
                            launchSingleTop = true
                        }
                    },
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
                    onChargeAlbumSongs = postViewModel::albumSongs,
                    onChargeArtistLists = {
                        postViewModel.artistSongs(it)
                        postViewModel.artistAlbums(it)
                    }
                )

            }

            composable<Screen.Albums> {

                AlbumsScreen(
                    albums = albums,
                    animatedVisibilityScope = this,
                    currentlyPlaying = musicState.currentlyPlaying,
                    chargePVMAlbumSongs = postViewModel::albumSongs,
                    isPlayerReady = musicState.isPlayerReady,
                    isPlaying = musicState.isCurrentlyPlaying,
                    onHandlePlayerActions = viewModel::handlePlayerActions,
                    onNavigate = { navController.navigate(it) },
                    onNavigationItemClicked = { index, item ->
                        navController.navigate(item.navigateTo) {
                            viewModel.selectedItem = index
                            launchSingleTop = true
                        }
                    },
                    selectedIndex = viewModel.selectedItem,
                )
            }
            composable<Screen.Artists> {
                ArtistsScreen(
                    artist = postViewModel.artists,
                    onNavigate = { navController.navigate(it) },
                    onNavigationItemClicked = { index, item ->
                        navController.navigate(item.navigateTo) {
                            viewModel.selectedItem = index
                            launchSingleTop = true
                        }
                    },
                    selectedIndex = viewModel.selectedItem,
                    onChargeArtistLists = {
                        postViewModel.artistSongs(it)
                        postViewModel.artistAlbums(it)
                    },
                    currentlyPlaying = musicState.currentlyPlaying,
                    onHandlePlayerActions = viewModel::handlePlayerActions,
                    isPlaying = musicState.isCurrentlyPlaying,
                    animatedVisibilityScope = this,
                    isPlayerReady = musicState.isPlayerReady
                )
            }

            composable<Screen.NowPlaying> {
                NowPlayingScreen(
                    navController = navController,
                    viewModel = viewModel,
                    animatedVisibilityScope = this,
                    musicState = musicState,
                    onChargeAlbumSongs = postViewModel::albumSongs,
                    onChargeArtistLists = {
                        postViewModel.artistSongs(it)
                        postViewModel.artistAlbums(it)
                    }
                )
            }
            composable<Screen.Settings> {
                SettingsScreen(
                    onPopBackStack = navController::navigateUp,
                    onNavigate = { navController.navigate(it) }
                )
            }
            composable<Screen.AlbumsDetails> {
                val index = it.toRoute<Screen.AlbumsDetails>()
                albums.find { album -> album.id == index.id }?.let { album ->
                    AlbumDetailsScreen(
                        album = album,
                        viewModel = viewModel,
                        onPopBackStack = navController::navigateUp,
                        postViewModel = postViewModel,
                        musicState = musicState,
                        animatedVisibilityScope = this,
                        onNavigate = { screen -> navController.navigate(screen) },
                    )
                }

            }
            composable<Screen.ArtistsDetails> {
                val index = it.toRoute<Screen.ArtistsDetails>()
                postViewModel.artists.find { artist -> artist.id == index.id }?.let { artist ->
                    ArtistDetails(
                        artist = artist,
                        navController = navController,
                        viewModel = viewModel,
                        postViewModel = postViewModel,
                        onNavigate = { screen -> navController.navigate(screen) },
                        musicState = musicState,
                        animatedVisibilityScope = this
                    )
                }
            }
            composable<Screen.Blacklisted> {
                BlacklistedScreen(
                    navController = navController,
                    folders = postViewModel.folders,
                )
            }
            composable<Screen.MetadataEditor> {
                val index = it.toRoute<Screen.MetadataEditor>()
                musics.find { music -> music.mediaId == index.id }?.let { music ->
                    MetadataEditor(
                        music = music,
                        onPopBackStack = navController::navigateUp,
                        onNavigate = { screen -> navController.navigate(screen) },
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

            composable<Screen.AllFolders> {
                AllFoldersScreen(
                    musics = musics,
                    onNavigationItemClicked = { index, item ->
                        navController.navigate(item.navigateTo) {
                            viewModel.selectedItem = index
                            launchSingleTop = true
                        }
                    },
                    selectedIndex = viewModel.selectedItem,
                    onNavigate = { navController.navigate(it) },
                    currentlyPlaying = musicState.currentlyPlaying,
                    isCurrentlyPlaying = musicState.isCurrentlyPlaying,
                    onHandlePlayerActions = viewModel::handlePlayerActions,
                    isPlayerReady = musicState.isPlayerReady,
                    animatedVisibilityScope = this,
                )
            }
        }
    }
}