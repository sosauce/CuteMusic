@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sosauce.cutemusic.data.actions.MetadataActions
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
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun Nav() {

    val navController = rememberNavController()
    val viewModel = koinViewModel<MusicViewModel>()
    val postViewModel = koinViewModel<PostViewModel>()
    val metadataViewModel = koinViewModel<MetadataViewModel>()
    val blacklistedFolders by rememberAllBlacklistedFolders()
    val musics = postViewModel.musics
        .filter { it.mediaMetadata.extras?.getString("folder") !in blacklistedFolders }

    LaunchedEffect(musics) {
        Log.d("new musics", musics.toString())
    }



    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.Main
        ) {
            composable<Screen.Main> {
                MainScreen(
                    navController = navController,
                    viewModel = viewModel,
                    musics = musics,
                    animatedVisibilityScope = this,
                    onLoadMetadata = { uri ->
                        metadataViewModel.onHandleMetadataActions(MetadataActions.ClearState)
                        metadataViewModel.onHandleMetadataActions(
                            MetadataActions.LoadSong(
                                uri
                            )
                        )
                    }
                )

            }
            composable<Screen.Albums> {
                AlbumsScreen(
                    navController = navController,
                    albums = postViewModel.albums,
                    viewModel = viewModel,
                    postViewModel = postViewModel,
                    animatedVisibilityScope = this
                )
            }
            composable<Screen.Artists> {
                ArtistsScreen(
                    artist = postViewModel.artists,
                    navController = navController,
                    viewModel = viewModel,
                    postViewModel = postViewModel,
                    animatedVisibilityScope = this
                )
            }

            composable<Screen.NowPlaying> {
                NowPlayingScreen(
                    navController = navController,
                    viewModel = viewModel,
                    animatedVisibilityScope = this
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
                        postViewModel = postViewModel,
                        onNavigate = { screen -> navController.navigate(screen) }
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
                        metadataViewModel = metadataViewModel
                    )
                }
            }
        }
    }

}