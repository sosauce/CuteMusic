package com.sosauce.cutemusic.logic

import androidx.compose.runtime.Composable
import androidx.media3.common.Player
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.screens.AlbumDetailsScreen
import com.sosauce.cutemusic.screens.AlbumsScreen
import com.sosauce.cutemusic.screens.MainScreen
import com.sosauce.cutemusic.screens.NowPlayingScreen
import com.sosauce.cutemusic.screens.SettingsScreen

@Composable
fun Nav(player: Player, music: List<Music>, viewModel: MusicViewModel, albums: List<Album>) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "MainScreen") {

        composable(route = "MainScreen") {
            MainScreen(navController, player, music, viewModel)
        }
        composable(route = "NowPlaying") {
            NowPlayingScreen(navController, viewModel, player)
        }
        composable(route = "AlbumsScreen") {
            AlbumsScreen(navController, albums)
        }
        composable(route = "AlbumsDetailsScreen"){
            AlbumDetailsScreen(navController)
        }
        composable(route = "SettingsScreen") {
            SettingsScreen(navController)
        }
    }

}