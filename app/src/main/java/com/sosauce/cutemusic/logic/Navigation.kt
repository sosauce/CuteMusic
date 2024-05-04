package com.sosauce.cutemusic.logic

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.audio.Artist
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.screens.AlbumDetailsScreen
import com.sosauce.cutemusic.screens.AlbumsScreen
import com.sosauce.cutemusic.screens.ArtistDetails
import com.sosauce.cutemusic.screens.ArtistsScreen
import com.sosauce.cutemusic.screens.MainScreen
import com.sosauce.cutemusic.screens.NowPlayingScreen
import com.sosauce.cutemusic.screens.SettingsScreen

@Composable
fun Nav(player: Player, music: List<Music>, viewModel: MusicViewModel, albums: List<Album>, artists: List<Artist>) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = "MainScreen") {

        composable(route = "MainScreen") {
            MainScreen(navController, music, viewModel, state)
        }
        composable(route = "NowPlaying") {
            NowPlayingScreen(navController, viewModel, player, state)
        }
        composable(route = "SettingsScreen") {
            SettingsScreen(navController)
        }
        composable(route = "AlbumsScreen") {
                AlbumsScreen(navController, albums, viewModel)
        }
        composable(
            route = "AlbumsDetailsScreen/{index}",
            arguments = listOf(navArgument(name = "index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 0
            val album = albums[index]
            AlbumDetailsScreen(
                navController = navController,
                album = album,
                viewModel = viewModel
            )
        }
        composable(route = "ArtistsScreen") {
            ArtistsScreen(artists, navController, viewModel)
        }
        composable(
            route = "ArtistDetailsScreen/{index}",
            arguments = listOf(navArgument(name = "index") { type = NavType.IntType })
            ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 0
            val artist = artists[index]
            ArtistDetails(artist = artist, navController = navController, viewModel)
        }
    }
}