@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.album

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.MusicState
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding

@Composable
fun SharedTransitionScope.AlbumDetailsScreen(
    album: Album,
    viewModel: MusicViewModel,
    postViewModel: PostViewModel,
    onPopBackStack: () -> Unit,
    musicState: MusicState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigate: (Screen) -> Unit,
) {
    val albumSongs by remember { mutableStateOf(postViewModel.albumSongs) }

    if (rememberIsLandscape()) {
        AlbumDetailsLandscape(
            album = album,
            onNavigateUp = { onPopBackStack() },
            postViewModel = postViewModel,
            viewModel = viewModel,
            musicState = musicState
        )
    } else {
        AlbumDetailsContent(
            album = album,
            viewModel = viewModel,
            onPopBackStack = { onPopBackStack() },
            albumSongs = albumSongs,
            musicState = musicState,
            animatedVisibilityScope = animatedVisibilityScope,
            onNavigate = onNavigate,
        )
    }

}

@Composable
private fun SharedTransitionScope.AlbumDetailsContent(
    album: Album,
    viewModel: MusicViewModel,
    onPopBackStack: () -> Unit,
    albumSongs: List<MediaItem>,
    musicState: MusicState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigate: (Screen) -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onPopBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back arrow"
                        )
                    }
                }
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
            LazyColumn {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = ImageUtils.imageRequester(
                                img = ImageUtils.getAlbumArt(album.id),
                                context = context
                            ),
                            contentDescription = stringResource(R.string.artwork),
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            CuteText(
                                text = album.name,
                                fontSize = 22.sp,
                                modifier = Modifier.basicMarquee()
                            )
                            CuteText(
                                text = album.artist,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                                modifier = Modifier.basicMarquee()
                            )
                            Spacer(Modifier.height(60.dp))
                            CuteText(
                                text = "${albumSongs.size} ${if (albumSongs.size <= 1) "song" else "songs"}",
                                fontSize = 22.sp,
                                modifier = Modifier.basicMarquee()
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Column {
                        albumSongs.forEach { music ->
                            MusicListItem(
                                music = music,
                                onShortClick = {
                                    viewModel.handlePlayerActions(
                                        PlayerActions.StartAlbumPlayback(
                                            albumName = music.mediaMetadata.albumTitle.toString(),
                                            mediaId = it
                                        )
                                    )
                                },
                                currentMusicUri = musicState.currentMusicUri,
                                isPlayerReady = viewModel.isPlayerReady()
                            )
                        }
                    }
                }
            }

            CuteSearchbar(
                currentlyPlaying = musicState.currentlyPlaying,
                isPlayerReady = viewModel.isPlayerReady(),
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
                        PlayerActions.StartAlbumPlayback(
                            albumName = album.name,
                            mediaId = null
                        )
                    )
                }
            )

        }
    }
}
