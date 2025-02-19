@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.artist

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.album.AlbumCard
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment

@Composable
fun SharedTransitionScope.ArtistDetails(
    artist: Artist,
    viewModel: MusicViewModel,
    postViewModel: PostViewModel,
    onNavigate: (Screen) -> Unit,
    onPopBackstack: () -> Unit,
    musicState: MusicState,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val artistSongs by postViewModel.artistSongs.collectAsStateWithLifecycle()
    val artistAlbums by postViewModel.artistAlbums.collectAsStateWithLifecycle()

    if (rememberIsLandscape()) {
        ArtistDetailsLandscape(
            onNavigateUp = onPopBackstack,
            artistAlbums = artistAlbums,
            artistSongs = artistSongs,
            onClickPlay = { viewModel.handlePlayerActions(PlayerActions.StartPlayback(it)) },
            onNavigate = onNavigate,
            chargePVMAlbumSongs = { postViewModel.loadAlbumSongs(it) },
            artist = artist,
            currentMusicUri = musicState.currentMusicUri,
            isPlayerReady = musicState.isPlayerReady,
            animatedVisibilityScope = animatedVisibilityScope
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
            ) {
                CuteText(
                    text = artist.name,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .sharedElement(
                            state = rememberSharedContentState(key = artist.name + artist.id),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                )
                Spacer(Modifier.height(20.dp))
                if (artistAlbums.isNotEmpty()) {
                    CuteText(
                        text = pluralStringResource(
                            R.plurals.albums,
                            artistAlbums.size,
                            artistAlbums.size
                        ),
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
                LazyRow {
                    items(
                        items = artistAlbums,
                        key = { it.id }
                    ) { album ->
                        AlbumCard(
                            album = album,
                            modifier = Modifier
                                .clip(RoundedCornerShape(15.dp))
                                .clickable {
                                    postViewModel.loadAlbumSongs(album.name)
                                    onNavigate(Screen.AlbumsDetails(album.id))
                                },
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
                if (artistAlbums.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .clip(RoundedCornerShape(50.dp))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                CuteText(
                    text = pluralStringResource(
                        R.plurals.songs,
                        artistSongs.size,
                        artistSongs.size
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
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
                modifier = Modifier.align(rememberSearchbarAlignment()),
                showSearchField = false,
                onNavigate = { onNavigate(Screen.NowPlaying) },
                fab = {
                    CuteActionButton(
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "fab"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    ) {
                        viewModel.handlePlayerActions(
                            PlayerActions.StartArtistPlayback(
                                artistName = artist.name,
                                mediaId = null
                            )
                        )
                    }
                },
                navigationIcon = { CuteNavigationButton { onPopBackstack() } }
            )
        }
    }
}