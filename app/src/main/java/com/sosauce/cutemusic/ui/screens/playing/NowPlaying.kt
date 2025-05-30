@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.playing

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.lyrics.LyricsView
import com.sosauce.cutemusic.ui.screens.playing.components.ActionButtonsRow
import com.sosauce.cutemusic.ui.screens.playing.components.Artwork
import com.sosauce.cutemusic.ui.screens.playing.components.CuteSlider
import com.sosauce.cutemusic.ui.screens.playing.components.QuickActionsRow
import com.sosauce.cutemusic.ui.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistPicker
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicStateDetailsDialog
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.ignoreParentPadding

@Composable
fun SharedTransitionScope.NowPlaying(
    musicState: MusicState,
    loadedMedias: List<MediaItem> = emptyList(),
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onNavigateUp: () -> Unit
) {
    var showFullLyrics by remember { mutableStateOf(false) }
    val isLandscape = rememberIsLandscape()


    if (isLandscape) {
        NowPlayingLandscape(
            musicState = musicState,
            onHandlePlayerActions = onHandlePlayerActions,
            onNavigate = onNavigate,
            onNavigateUp = onNavigateUp,
            loadedMedias = loadedMedias
        )
    } else {
        AnimatedContent(
            targetState = showFullLyrics
        ) { targetState ->
            if (targetState) {
                LyricsView(
                    onHideLyrics = { showFullLyrics = false },
                    musicState = musicState,
                    onHandlePlayerActions = onHandlePlayerActions
                )
            } else {
                NowPlayingContent(
                    musicState = musicState,
                    loadedMedias = loadedMedias,
                    onHandlePlayerActions = onHandlePlayerActions,
                    onNavigate = onNavigate,
                    onNavigateUp = onNavigateUp,
                    onShowLyrics = { showFullLyrics = true }
                )
            }

        }
    }


}

@Composable
private fun SharedTransitionScope.NowPlayingContent(
    musicState: MusicState,
    loadedMedias: List<MediaItem> = emptyList(),
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onNavigateUp: () -> Unit,
    onShowLyrics: () -> Unit,
) {
    var showSpeedCard by remember { mutableStateOf(false) }
    var snap by rememberSnapSpeedAndPitch()
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }


    if (showDetailsDialog) {
        MusicStateDetailsDialog(
            musicState = musicState,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showPlaylistDialog) {
        PlaylistPicker(
            mediaId = listOf(musicState.mediaId),
            onDismissRequest = { showPlaylistDialog = false }
        )
    }

    if (showSpeedCard) {
        SpeedCard(
            onDismissRequest = { showSpeedCard = false },
            shouldSnap = snap,
            onChangeSnap = { snap = !snap }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 10.dp,
                    top = 10.dp
                ),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onNavigateUp,
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Artwork(
            pagerModifier = Modifier.ignoreParentPadding(),
            musicState = musicState,
            onHandlePlayerActions = onHandlePlayerActions,
            loadedMedias = loadedMedias,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            CuteText(
                text = musicState.title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 25.sp,
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.CURRENTLY_PLAYING),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                    )
                    .basicMarquee()
            )
            CuteText(
                text = musicState.artist,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 20.sp,
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.ARTIST + musicState.mediaId),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current

                    )
                    .basicMarquee()
            )
        }

        Spacer(Modifier.height(24.dp))
        CuteSlider(
            musicState = musicState,
            onHandlePlayerActions = onHandlePlayerActions
        )
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer) {
            ActionButtonsRow(
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions
            )
            Spacer(modifier = Modifier.weight(1f))
            QuickActionsRow(
                musicState = musicState,
                onShowLyrics = onShowLyrics,
                onShowSpeedCard = { showSpeedCard = true },
                onHandlePlayerActions = onHandlePlayerActions,
                onNavigate = onNavigate,
                loadedMedias = loadedMedias
            )
//            QuickActionsRow2(
//                musicState = musicState,
//                onShowLyrics = onShowLyrics,
//                onShowSpeedCard = { showSpeedCard = true },
//                onHandlePlayerActions = onHandlePlayerActions,
//                onNavigate = onNavigate
//            )
        }

    }

}