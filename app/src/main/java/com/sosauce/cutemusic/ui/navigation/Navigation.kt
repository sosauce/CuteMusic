@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.main.App
import com.sosauce.cutemusic.ui.screens.album.AlbumDetailsScreen
import com.sosauce.cutemusic.ui.screens.album.AlbumsScreen
import com.sosauce.cutemusic.ui.screens.artist.ArtistDetails
import com.sosauce.cutemusic.ui.screens.artist.ArtistsScreen
import com.sosauce.cutemusic.ui.screens.main.MainScreen
import com.sosauce.cutemusic.ui.screens.playing.NowPlayingScreen
import com.sosauce.cutemusic.ui.screens.settings.SettingsScreen
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.MusicViewModelFactory
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun Nav(
   app: App,
   mediaStoreHelper: MediaStoreHelper
) {

    val navController = rememberNavController()
    val postViewModel = koinViewModel<PostViewModel>()
    val viewModel = viewModel<MusicViewModel>(factory = MusicViewModelFactory(app, mediaStoreHelper.getMusics()))


        NavHost(
            navController = navController,
            startDestination = Screen.Main
        ) {
            composable<Screen.Main> {
                MainScreen(
                    navController = navController,
                    musics = postViewModel.musics,
                    viewModel = viewModel
                )

            }
            composable<Screen.Albums> {
                AlbumsScreen(
                    navController = navController,
                    albums = postViewModel.albums,
                    viewModel = viewModel,
                    postViewModel = postViewModel,
                )
            }
            composable<Screen.Artists> {
                ArtistsScreen(
                    artist = postViewModel.artists,
                    navController = navController,
                    viewModel = viewModel,
                    postViewModel = postViewModel,
                )
            }

            composable<Screen.NowPlaying> {
                NowPlayingScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
            composable<Screen.Settings> {
                SettingsScreen(
                    onPopBackStack = { navController.popBackStack() },
                    onNavigate = { navController.navigate(Screen.Settings) }
                )
            }
            composable<Screen.AlbumsDetails> {
                val index = it.toRoute<Screen.AlbumsDetails>()
                postViewModel.albums.find { album -> album.id == index.id }?.let { album ->
                    AlbumDetailsScreen(
                        album = album,
                        viewModel = viewModel,
                        onPopBackStack = { navController.popBackStack() },
                        postViewModel = postViewModel
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
                        postViewModel = postViewModel
                    )
                }

            }
        }
}