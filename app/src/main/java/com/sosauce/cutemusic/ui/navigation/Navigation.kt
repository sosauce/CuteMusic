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
import com.sosauce.cutemusic.data.datastore.rememberAllBlacklistedFolders
import com.sosauce.cutemusic.ui.screens.album.AlbumDetailsScreen
import com.sosauce.cutemusic.ui.screens.album.AlbumsScreen
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
import com.sosauce.cutemusic.utils.ListToHandle
import org.koin.androidx.compose.koinViewModel

// https://stackoverflow.com/a/78771053


@Composable
fun Nav() {

    val navController = rememberNavController()
    val viewModel = koinViewModel<MusicViewModel>()
    val postViewModel = koinViewModel<PostViewModel>()
    val metadataViewModel = koinViewModel<MetadataViewModel>()
    val blacklistedFolders by rememberAllBlacklistedFolders()
    val musics = postViewModel.musics
        .filter { it.mediaMetadata.extras?.getString("folder") !in blacklistedFolders }
    val musicState by viewModel.musicState.collectAsStateWithLifecycle()


    SharedTransitionLayout {

        this
        NavHost(
            navController = navController,
            startDestination = Screen.Main
        ) {

            this@SharedTransitionLayout
            composable<Screen.Main> {
                MainScreen(
                    musics = musics,
                    selectedIndex = viewModel.selectedItem,
                    onNavigateTo = { navController.navigate(it) },
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
                    onLoadMetadata = { uri ->
                        metadataViewModel.onHandleMetadataActions(MetadataActions.ClearState)
                        metadataViewModel.onHandleMetadataActions(
                            MetadataActions.LoadSong(
                                uri
                            )
                        )
                    },
                    isPlayerReady = viewModel.isPlayerReady(),
                    currentMusicUri = musicState.currentMusicUri,
                    onHandlePlayerAction = { viewModel.handlePlayerActions(it) },
                    onDeleteMusic = { uris, intentSender ->
                        postViewModel.deleteMusic(
                            uris,
                            intentSender
                        )
                    },
                    onHandleSorting = { sortingType ->
                        postViewModel.handleFiltering(
                            listToHandle = ListToHandle.TRACKS,
                            sortingType = sortingType
                        )
                    },
                    onHandleSearching = { query ->
                        postViewModel.handleSearch(
                            listToHandle = ListToHandle.TRACKS,
                            query = query
                        )
                    },
                    onChargeAlbumSongs = postViewModel::albumSongs,
                    onChargeArtistLists = {
                        postViewModel.artistSongs(it)
                        postViewModel.artistAlbums(it)
                    },
                    musicState = musicState
                )

            }
            composable<Screen.Albums> {
                AlbumsScreen(
                    albums = postViewModel.albums,
                    animatedVisibilityScope = this,
                    onHandleSorting = { sortingType ->
                        postViewModel.handleFiltering(
                            listToHandle = ListToHandle.ALBUMS,
                            sortingType = sortingType
                        )
                    },
                    onHandleSearching = { query ->
                        postViewModel.handleSearch(
                            listToHandle = ListToHandle.ALBUMS,
                            query = query
                        )
                    },
                    currentlyPlaying = musicState.currentlyPlaying,
                    chargePVMAlbumSongs = postViewModel::albumSongs,
                    isPlayerReady = viewModel.isPlayerReady(),
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
                    musicState = musicState
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
                    isPlayerReady = viewModel.isPlayerReady(),
                    onHandleSorting = { sortingType ->
                        postViewModel.handleFiltering(
                            listToHandle = ListToHandle.ARTISTS,
                            sortingType = sortingType
                        )
                    },
                    onHandleSearching = { query ->
                        postViewModel.handleSearch(
                            listToHandle = ListToHandle.ARTISTS,
                            query = query
                        )
                    },
                    musicState = musicState
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
                postViewModel.albums.find { album -> album.id == index.id }?.let { album ->
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
        }
    }
}