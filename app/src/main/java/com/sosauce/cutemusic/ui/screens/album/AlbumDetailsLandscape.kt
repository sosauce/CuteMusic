@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.album

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.thenIf

@Composable
fun SharedTransitionScope.AlbumDetailsLandscape(
    album: Album,
    onNavigateUp: () -> Unit,
    viewModel: MusicViewModel,
    musicState: MusicState,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    val albumSongs by viewModel.albumSongs.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .displayCutoutPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                AsyncImage(
                    model = ImageUtils.getAlbumArt(album.id),
                    stringResource(R.string.artwork),
                    modifier = Modifier
                        .statusBarsPadding()
                        .size(200.dp)
                        .sharedElement(
                            state = rememberSharedContentState(key = album.id),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                        .clip(RoundedCornerShape(10)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(10.dp))
                CuteText(
                    text = album.name,
                    modifier = Modifier.sharedElement(
                        state = rememberSharedContentState(key = album.name + album.id),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                )
                CuteText(
                    text = album.artist,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                    modifier = Modifier.sharedElement(
                        state = rememberSharedContentState(key = album.artist + album.id),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                )
                CuteText(pluralStringResource(R.plurals.tracks, albumSongs.size, albumSongs.size))
                Spacer(modifier = Modifier.width(5.dp))
            }

            LazyColumn {
                itemsIndexed(
                    items = albumSongs,
                    key = { _, music -> music.mediaId }
                ) { index, music ->
                    LocalMusicListItem(
                        modifier = Modifier
                            .thenIf(index == 0) { Modifier.statusBarsPadding() }
                            .padding(horizontal = 5.dp),
                        music = music,
                        currentMusicUri = musicState.uri,
                        onShortClick = {
                            viewModel.handlePlayerActions(
                                PlayerActions.StartPlayback(
                                    it
                                )
                            )
                        },
                        isPlayerReady = musicState.isPlayerReady,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }

        CuteNavigationButton(
            modifier = Modifier.align(Alignment.BottomStart)
        ) { onNavigateUp() }
    }

}