@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.playing

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.lyrics.LyricsScreen
import com.sosauce.cutemusic.presentation.screens.playing.components.ActionButtonsRow
import com.sosauce.cutemusic.presentation.screens.playing.components.Artwork
import com.sosauce.cutemusic.presentation.screens.playing.components.CuteSlider
import com.sosauce.cutemusic.presentation.screens.playing.components.PlayingTopRow
import com.sosauce.cutemusic.presentation.screens.playing.components.QuickActionsRow
import com.sosauce.cutemusic.presentation.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.presentation.screens.playing.components.TitleAndArtist
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.cutemusic.presentation.shared_components.MusicDetailsDialog
import com.sosauce.cutemusic.utils.SharedTransitionKeys

@Composable
fun SharedTransitionScope.NowPlayingLandscape(
    onHandlePlayerActions: (PlayerActions) -> Unit,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onShrinkToSearchbar: () -> Unit
) {
    var showSpeedCard by remember { mutableStateOf(false) }
    var snap by rememberSnapSpeedAndPitch()
    var showPlaylistDialog by remember { mutableStateOf(false) }

    var showDetailsDialog by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->

        if (showDetailsDialog) {
            MusicDetailsDialog(
                track = musicState.track,
                onDismissRequest = { showDetailsDialog = false }
            )
        }

        if (showPlaylistDialog) {
            PlaylistPicker(
                mediaId = listOf(musicState.track.mediaId),
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 15.dp)
                .fillMaxSize()
        ) {
            Column {
                Artwork(
                    pagerModifier = Modifier
                        .fillMaxWidth(0.4f),
                    musicState = musicState,
                    onHandlePlayerActions = onHandlePlayerActions
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlayingTopRow(
                    musicState = musicState,
                    onNavigate = onNavigate,
                    onShrinkToSearchbar = onShrinkToSearchbar
                )
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
                    onShowSpeedCard = { showSpeedCard = true },
                    onHandlePlayerActions = onHandlePlayerActions,
                    onNavigate = onNavigate
                )
            }

        }
    }
}