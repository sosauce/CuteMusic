package com.sosauce.cutemusic.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.media3.common.Player
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.screens.AboutScreen
import com.sosauce.cutemusic.screens.MainScreen
import com.sosauce.cutemusic.screens.NowPlayingScreen
import com.sosauce.cutemusic.screens.SettingsScreen

@Composable
fun Nav(player: Player, music: List<Music>, viewModel: MusicViewModel) {
    val navController = rememberNavController()
    val playerState = remember { viewModel.playerState }
    NavHost(navController = navController, startDestination = "MainScreen") {

        composable(route = "MainScreen") {
            MainScreen(navController, player, music, viewModel, playerState)
        }
        composable(route = "NowPlaying") {
            NowPlayingScreen(navController, viewModel, player, playerState)
        }
        composable(route = "SettingsScreen") {
            SettingsScreen(navController)
        }
        composable(route = "AboutScreen") {
            AboutScreen(navController)
        }
    }

}