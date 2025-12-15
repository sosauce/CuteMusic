@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.playing

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.lyrics.LyricsView
import com.sosauce.cutemusic.presentation.screens.playing.components.ActionButtonsRow
import com.sosauce.cutemusic.presentation.screens.playing.components.Artwork
import com.sosauce.cutemusic.presentation.screens.playing.components.CuteSlider
import com.sosauce.cutemusic.presentation.screens.playing.components.MoreOptionsButton
import com.sosauce.cutemusic.presentation.screens.playing.components.QuickActionsRow
import com.sosauce.cutemusic.presentation.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.presentation.screens.playing.components.TitleAndArtist
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.cutemusic.presentation.shared_components.MusicDetailsDialog
import com.sosauce.cutemusic.utils.SharedTransitionKeys

@Composable
fun SharedTransitionScope.NowPlaying(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onNavigateUp: () -> Unit,
    onShrinkToSearchbar: () -> Unit = {}
) {
    var showFullLyrics by remember { mutableStateOf(false) }
    val isLandscape = rememberIsLandscape()

    if (isLandscape) {
        NowPlayingLandscape(
            musicState = musicState,
            onHandlePlayerActions = onHandlePlayerActions,
            onNavigate = onNavigate,
            onShrinkToSearchbar = onShrinkToSearchbar
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
                    onHandlePlayerActions = onHandlePlayerActions,
                    onNavigate = onNavigate,
                    onShowLyrics = { showFullLyrics = true },
                    onShrinkToSearchbar = onShrinkToSearchbar
                )
            }

        }
    }


}

@Composable
private fun SharedTransitionScope.NowPlayingContent(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onShowLyrics: () -> Unit,
    onShrinkToSearchbar: () -> Unit
) {
    var snap by rememberSnapSpeedAndPitch()
    var showSpeedCard by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onShrinkToSearchbar,
                        shapes = IconButtonDefaults.shapes(),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                        ),
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))

                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_down),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                actions = {
                    MoreOptionsButton(
                        modifier = Modifier.padding(end = 15.dp),
                        musicState = musicState,
                        onNavigate = onNavigate
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent
            ) {
                QuickActionsRow(
                    musicState = musicState,
                    onShowLyrics = onShowLyrics,
                    onShowSpeedCard = { showSpeedCard = true },
                    onHandlePlayerActions = onHandlePlayerActions,
                    onNavigate = onNavigate
                )
            }
        }
    ) { paddingValues ->

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

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 15.dp)
                .verticalScroll(rememberScrollState()), // for smaller screens where it might not entirely fit
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Artwork(
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions
            )
            TitleAndArtist(
                titleModifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.CURRENTLY_PLAYING),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                    ),
                musicState = musicState
            )
            CuteSlider(
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions
            )
            ActionButtonsRow(
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions
            )
        }
    }


}