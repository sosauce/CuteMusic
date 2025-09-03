@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.playing

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.lyrics.LyricsView
import com.sosauce.cutemusic.presentation.screens.playing.components.ActionButtonsRow
import com.sosauce.cutemusic.presentation.screens.playing.components.Artwork
import com.sosauce.cutemusic.presentation.screens.playing.components.CuteSlider
import com.sosauce.cutemusic.presentation.screens.playing.components.QuickActionsRow
import com.sosauce.cutemusic.presentation.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.presentation.screens.playing.components.TitleAndArtist
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.cutemusic.presentation.shared_components.MusicStateDetailsDialog
import com.sosauce.cutemusic.utils.SharedTransitionKeys

@Composable
fun SharedTransitionScope.NowPlayingLandscape(
    onNavigateUp: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    loadedMedias: List<MediaItem> = emptyList()
) {
    var showSpeedCard by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }
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
            musicState = musicState,
            onHandlePlayerAction = onHandlePlayerActions,
            onDismissRequest = { showSpeedCard = false },
            shouldSnap = snap,
            onChangeSnap = { snap = !snap }
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .displayCutoutPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {
                Artwork(
                    pagerModifier = Modifier
                        .fillMaxWidth(0.4f),
                    musicState = musicState,
                    onHandlePlayerActions = onHandlePlayerActions,
                    loadedMedias = loadedMedias,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            AnimatedContent(showLyrics) { targetState ->
                if (targetState) {
                    LyricsView(
                        onHideLyrics = { showLyrics = false },
                        musicState = musicState,
                        onHandlePlayerActions = onHandlePlayerActions
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = { onNavigateUp() },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        TitleAndArtist(
                            titleModifier = Modifier
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.CURRENTLY_PLAYING),
                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                ),
                            musicState = musicState
                        )
                        Spacer(Modifier.height(24.dp))
                        CuteSlider(
                            musicState = musicState,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                        ActionButtonsRow(
                            musicState = musicState,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        QuickActionsRow(
                            musicState = musicState,
                            onShowLyrics = { showLyrics = true },
                            onShowSpeedCard = { showSpeedCard = true },
                            onHandlePlayerActions = onHandlePlayerActions,
                            onNavigate = onNavigate,
                            loadedMedias = loadedMedias
                        )
                    }
                }
            }

        }
    }
}