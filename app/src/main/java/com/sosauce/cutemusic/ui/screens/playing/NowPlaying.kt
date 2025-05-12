@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.playing

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.lyrics.LyricsView
import com.sosauce.cutemusic.ui.screens.playing.components.ActionButtonsRow
import com.sosauce.cutemusic.ui.screens.playing.components.Artwork
import com.sosauce.cutemusic.ui.screens.playing.components.CuteSlider
import com.sosauce.cutemusic.ui.screens.playing.components.QuickActionsRow
import com.sosauce.cutemusic.ui.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistItem
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistPicker
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicStateDetailsDialog
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.copyMutate
import com.sosauce.cutemusic.utils.ignoreParentPadding
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.NowPlaying(
    animatedVisibilityScope: AnimatedVisibilityScope,
    musicState: MusicState,
    loadedMedias: List<MediaItem> = emptyList(),
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onNavigateUp: () -> Unit,
    lyrics: List<Lyrics>
) {
    var showFullLyrics by remember { mutableStateOf(false) }
    val isLandscape = rememberIsLandscape()


    if (isLandscape) {
        NowPlayingLandscape(
            musicState = musicState,
            onHandlePlayerActions = onHandlePlayerActions,
            onNavigate = onNavigate,
            onNavigateUp = onNavigateUp,
            lyrics = lyrics,
            animatedVisibilityScope = animatedVisibilityScope,
            loadedMedias = loadedMedias
        )
    } else {
        AnimatedContent(
            targetState = showFullLyrics
        ) { targetState ->
            if (targetState) {
                LyricsView(
                    lyrics = lyrics,
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
                    onShowLyrics = { showFullLyrics = true },
                    animatedVisibilityScope = animatedVisibilityScope
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
    animatedVisibilityScope: AnimatedVisibilityScope,
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
            .padding(horizontal = 10.dp)
            .navigationBarsPadding()
            .statusBarsPadding(),
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
                        animatedVisibilityScope = animatedVisibilityScope
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
                        animatedVisibilityScope = animatedVisibilityScope

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
                animatedVisibilityScope = animatedVisibilityScope,
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions
            )
            Spacer(modifier = Modifier.weight(1f))
            QuickActionsRow(
                musicState = musicState,
                onShowLyrics = onShowLyrics,
                onShowSpeedCard = { showSpeedCard = true },
                onHandlePlayerActions = onHandlePlayerActions,
                onNavigate = onNavigate
            )
        }

    }

}