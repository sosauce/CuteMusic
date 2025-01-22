@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.artist

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.album.AlbumCard
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding

@Composable
fun SharedTransitionScope.ArtistDetails(
    artist: Artist,
    navController: NavController,
    viewModel: MusicViewModel,
    postViewModel: PostViewModel,
    onNavigate: (Screen) -> Unit,
    musicState: MusicState,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val artistSongs by remember { mutableStateOf(postViewModel.artistSongs) }
    val artistAlbums by remember { mutableStateOf(postViewModel.artistAlbums) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    if (rememberIsLandscape()) {
        ArtistDetailsLandscape(
            onNavigateUp = navController::navigateUp,
            artistAlbums = artistAlbums,
            artistSongs = artistSongs,
            onClickPlay = { viewModel.handlePlayerActions(PlayerActions.StartPlayback(it)) },
            onNavigate = { navController.navigate(it) },
            chargePVMAlbumSongs = { postViewModel.albumSongs(it) },
            artist = artist,
            currentMusicUri = musicState.currentMusicUri,
            isPlayerReady = musicState.isPlayerReady,
            animatedVisibilityScope = animatedVisibilityScope
        )
    } else {
        Scaffold(
            //modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CuteText(
                                text = artist.name + " · ",
                                fontSize = 20.sp
                            )
                            CuteText(
                                text = "${artistSongs.size} ${if (artistSongs.size <= 1) "song" else "songs"}",
                                fontSize = 20.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = navController::navigateUp
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back arrow"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { values ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = values.calculateLeftPadding(LayoutDirection.Ltr) + 10.dp,
                        end = values.calculateRightPadding(LayoutDirection.Rtl) + 10.dp,
                        top = values.calculateTopPadding(),
                        bottom = values.calculateBottomPadding()
                    )
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    LazyRow {
                        items(items = artistAlbums, key = { it.id }) { album ->
                            AlbumCard(
                                album = album,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(15.dp))
                                    .clickable {
                                        postViewModel.albumSongs(album.name)
                                        onNavigate(Screen.AlbumsDetails(album.id))
                                    },
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }
                    }
                    if (artistAlbums.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    artistSongs.forEach { music ->
                        MusicListItem(
                            music = music,
                            onShortClick = {
                                viewModel.handlePlayerActions(
                                    PlayerActions.StartArtistPlayback(
                                        artistName = artist.name,
                                        mediaId = it
                                    )
                                )
                            },
                            currentMusicUri = musicState.currentMusicUri,
                            isPlayerReady = musicState.isPlayerReady
                        )
                    }
                }
                CuteSearchbar(
                    currentlyPlaying = musicState.currentlyPlaying,
                    isPlayerReady = musicState.isPlayerReady,
                    isPlaying = musicState.isCurrentlyPlaying,
                    onHandlePlayerActions = viewModel::handlePlayerActions,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth(rememberSearchbarMaxFloatValue())
                        .padding(end = rememberSearchbarRightPadding())
                        .align(rememberSearchbarAlignment()),
                    showSearchField = false,
                    onNavigate = { onNavigate(Screen.NowPlaying) },
                    onClickFAB = {
                        viewModel.handlePlayerActions(
                            PlayerActions.StartArtistPlayback(
                                artistName = artist.name,
                                mediaId = null
                            )
                        )
                    }
                )
            }
        }
    }
}