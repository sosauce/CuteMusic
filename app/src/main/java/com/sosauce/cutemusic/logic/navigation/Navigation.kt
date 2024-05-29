@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.logic.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.screens.AlbumDetailsScreen
import com.sosauce.cutemusic.screens.AlbumsScreen
import com.sosauce.cutemusic.screens.ArtistDetails
import com.sosauce.cutemusic.screens.ArtistsScreen
import com.sosauce.cutemusic.screens.MainScreen
import com.sosauce.cutemusic.screens.NowPlayingScreen
import com.sosauce.cutemusic.screens.SettingsScreen

@Composable
fun Nav(
    musics: List<Music>,
    viewModel: MusicViewModel
) {

    val navController = rememberNavController()
    val npState by viewModel.npState.collectAsState()
    val state by viewModel.state.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.Main
        ) {

            composable<Screen.Main> {
                MainScreen(
                    navController = navController,
                    musics = musics,
                    viewModel = viewModel,
                    state = state,
                    onNavigate = { navController.navigate(Screen.Settings) }
                )
            }
            composable<Screen.NowPlaying> {
                NowPlayingScreen(
                    navController = navController,
                    viewModel = viewModel,
                    state = npState
                )
            }
            composable<Screen.Settings> {
                SettingsScreen(
                    onPopBackStack = { navController.popBackStack() },
                    onNavigate = { navController.navigate(Screen.Settings) }
                )
            }
            composable<Screen.Albums> {
                AlbumsScreen(
                    navController = navController,
                    albums = albums,
                    viewModel = viewModel,
                    animatedVisibilityScope = this,
                    onNavigate = { navController.navigate(Screen.Settings) }
                )
            }
            composable<Screen.AlbumsDetails> {
                val index = it.toRoute<Screen.AlbumsDetails>()
                val album = albums[index.id]
                AlbumDetailsScreen(
                    album = album,
                    viewModel = viewModel,
                    animatedVisibilityScope = this,
                    onPopBackStack = { navController.popBackStack() }
                )

            }
            composable<Screen.Artists> {
                ArtistsScreen(
                    artist = artists,
                    navController = navController,
                    viewModel = viewModel,
                    onNavigate = { navController.navigate(Screen.Settings) }
                )
            }
            composable<Screen.ArtistsDetails> {
                val index = it.toRoute<Screen.ArtistsDetails>()
                val artist = artists[index.id]
                ArtistDetails(
                    artist = artist,
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}