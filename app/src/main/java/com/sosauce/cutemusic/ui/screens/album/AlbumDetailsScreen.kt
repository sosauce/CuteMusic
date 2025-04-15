@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.album

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment

@Composable
fun SharedTransitionScope.AlbumDetailsScreen(
    album: Album,
    viewModel: MusicViewModel,
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigate: (Screen) -> Unit,
    onDeleteMusic: (List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit,
    onChargeAlbumSongs: (String) -> Unit,
    onChargeArtistLists: (String) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
) {
    val albumSongs by viewModel.albumSongs.collectAsStateWithLifecycle()

    if (rememberIsLandscape()) {
        AlbumDetailsLandscape(
            album = album,
            onNavigateUp = onNavigateUp,
            viewModel = viewModel,
            musicState = musicState,
            animatedVisibilityScope = animatedVisibilityScope,
            onLoadMetadata = onLoadMetadata,
            onDeleteMusic = onDeleteMusic,
            onChargeAlbumSongs = onChargeAlbumSongs,
            onChargeArtistLists = onChargeArtistLists,
            onNavigate = onNavigate
        )
    } else {
        AlbumDetailsContent(
            album = album,
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            albumSongs = albumSongs,
            musicState = musicState,
            animatedVisibilityScope = animatedVisibilityScope,
            onNavigate = onNavigate,
            onLoadMetadata = onLoadMetadata,
            onDeleteMusic = onDeleteMusic,
            onChargeAlbumSongs = onChargeAlbumSongs,
            onChargeArtistLists = onChargeArtistLists,
        )
    }

}

@Composable
private fun SharedTransitionScope.AlbumDetailsContent(
    album: Album,
    viewModel: MusicViewModel,
    onNavigateUp: () -> Unit,
    albumSongs: List<MediaItem>,
    musicState: MusicState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigate: (Screen) -> Unit,
    onDeleteMusic: (List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit,
    onChargeAlbumSongs: (String) -> Unit,
    onChargeArtistLists: (String) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        LazyColumn {
            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    AsyncImage(
                        model = ImageUtils.imageRequester(ImageUtils.getAlbumArt(album.id)),
                        contentDescription = stringResource(R.string.artwork),
                        modifier = Modifier
                            .size(150.dp)
                            .sharedElement(
                                state = rememberSharedContentState(key = album.id),
                                animatedVisibilityScope = animatedVisibilityScope,
                            )
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        CuteText(
                            text = album.name,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = album.name + album.id),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                )
                                .basicMarquee()
                        )
                        CuteText(
                            text = album.artist,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = album.artist + album.id),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                )
                                .basicMarquee()
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                CuteText(
                    text = pluralStringResource(
                        R.plurals.tracks,
                        albumSongs.size,
                        albumSongs.size
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
                Column {
                    albumSongs.sortedWith(
                        compareBy(
                            { it.mediaMetadata.trackNumber == null || it.mediaMetadata.trackNumber == 0 },
                            { it.mediaMetadata.trackNumber }
                        ))
                        .forEach { music ->
                            LocalMusicListItem(
                                music = music,
                                onShortClick = {
                                    viewModel.handlePlayerActions(
                                        PlayerActions.StartAlbumPlayback(
                                            albumName = music.mediaMetadata.albumTitle.toString(),
                                            mediaId = it
                                        )
                                    )
                                },
                                currentMusicUri = musicState.uri,
                                isPlayerReady = musicState.isPlayerReady,
                                onLoadMetadata = onLoadMetadata,
                                onDeleteMusic = onDeleteMusic,
                                onChargeAlbumSongs = onChargeAlbumSongs,
                                onChargeArtistLists = onChargeArtistLists,
                                onNavigate = onNavigate,
                                showTrackNumber = true
                            )
                        }
                }
            }
        }


        CuteSearchbar(
            currentlyPlaying = musicState.title,
            isPlayerReady = musicState.isPlayerReady,
            isPlaying = musicState.isPlaying,
            onHandlePlayerActions = viewModel::handlePlayerActions,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier.align(rememberSearchbarAlignment()),
            showSearchField = false,
            onNavigate = onNavigate,
            fab = {
                CuteActionButton(
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.FAB),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                ) {
                    viewModel.handlePlayerActions(
                        PlayerActions.StartAlbumPlayback(
                            albumName = album.name,
                            mediaId = null
                        )
                    )
                }
            },
            navigationIcon = {
                CuteNavigationButton(
                    onNavigateUp = onNavigateUp
                )
            }
        )


    }
}
