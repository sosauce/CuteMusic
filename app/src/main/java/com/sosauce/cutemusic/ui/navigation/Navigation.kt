package com.sosauce.cutemusic.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sosauce.cutemusic.main.App
import com.sosauce.cutemusic.ui.screens.album.AlbumDetailsScreen
import com.sosauce.cutemusic.ui.screens.album.AlbumsScreen
import com.sosauce.cutemusic.ui.screens.artist.ArtistDetails
import com.sosauce.cutemusic.ui.screens.artist.ArtistsScreen
import com.sosauce.cutemusic.ui.screens.blacklisted.BlacklistedScreen
import com.sosauce.cutemusic.ui.screens.main.MainScreen
import com.sosauce.cutemusic.ui.screens.metadata.MetadataEditor
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
) {

    val navController = rememberNavController()
    val postViewModel = koinViewModel<PostViewModel>()
    val state by postViewModel.state.collectAsStateWithLifecycle()
    val musics = postViewModel.musics
    val blacklistedFolderNames = state.blacklistedFolders.map { it.path }.toSet()
    val viewModel = viewModel<MusicViewModel>(factory = MusicViewModelFactory(app, musics))
    Log.d("Testing", postViewModel.musics.toString())



        NavHost(
            navController = navController,
            startDestination = Screen.Main
        ) {
            composable<Screen.Main> {
                MainScreen(
                    navController = navController,
                    viewModel = viewModel,
                    musics = musics,
                    
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
                        onNavigate = { navController.navigate(it) }
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
            composable<Screen.Blacklisted> {
                BlacklistedScreen(
                    navController = navController,
                    folders = postViewModel.folders,
                    state = state,
                    onEvents = postViewModel::onEvent,
                    blacklistedFolderNames = blacklistedFolderNames
                )
            }
            composable<Screen.MetadataEditor> {
                val index = it.toRoute<Screen.MetadataEditor>()
                musics.find { music -> music.mediaId == index.id }?.let { music ->
                    MetadataEditor(
                        music = music,
                        onPopBackStack = navController::navigateUp,
                        onNavigate = { navController.navigate(it) }
                    )
                }
            }
        }
}