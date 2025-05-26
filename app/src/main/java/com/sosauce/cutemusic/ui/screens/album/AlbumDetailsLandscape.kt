@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.album

import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun SharedTransitionScope.AlbumDetailsLandscape(
    musics: List<MediaItem>,
    album: Album,
    onNavigateUp: () -> Unit,
    onNavigate: (Screen) -> Unit,
    musicState: MusicState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
    onHandleMediaItemAction: (MediaItemActions) -> Unit,
) {


    Box(
        modifier = Modifier
            .fillMaxSize()
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
                            sharedContentState = rememberSharedContentState(key = album.id),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                        .clip(RoundedCornerShape(10)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(10.dp))
                CuteText(
                    text = album.name,
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(key = album.name + album.id),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                )
                CuteText(
                    text = album.artist,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(key = album.artist + album.id),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                )
                CuteText(pluralStringResource(R.plurals.tracks, musics.size, musics.size))
                Spacer(modifier = Modifier.width(5.dp))
            }
            Scaffold { paddingValues ->
                LazyColumn(
                    contentPadding = paddingValues
                ) {
                    items(
                        items = musics,
                        key = { it.mediaId }
                    ) { music ->
                        LocalMusicListItem(
                            modifier = Modifier
                                .padding(horizontal = 5.dp),
                            music = music,
                            currentMusicUri = musicState.uri,
                            onShortClick = {
                                onHandlePlayerActions(
                                    PlayerActions.StartPlayback(
                                        it
                                    )
                                )
                            },
                            isPlayerReady = musicState.isPlayerReady,
                            onLoadMetadata = onLoadMetadata,
                            onHandleMediaItemAction = onHandleMediaItemAction,
                            onNavigate = onNavigate
                        )
                    }
                }
            }
        }

        CuteNavigationButton(
            modifier = Modifier
                .navigationBarsPadding()
                .align(Alignment.BottomStart)
        ) { onNavigateUp() }
    }

}