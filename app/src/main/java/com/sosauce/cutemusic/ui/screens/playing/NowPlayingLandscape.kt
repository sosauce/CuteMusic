@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.playing

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
            onDismissRequest = { showSpeedCard = false },
            shouldSnap = snap,
            onChangeSnap = { snap = !snap }
        )
    }

    val imgSize by animateDpAsState(
        targetValue = if (showLyrics) 200.dp else 320.dp,
        label = "Image Size"
    )

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
                if (showLyrics) {
                    Spacer(Modifier.height(10.dp))
                    CuteText(musicState.title)
                    CuteText(
                        text = musicState.artist,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                    )
                }
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
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.CURRENTLY_PLAYING),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                    )
                                    .basicMarquee()
                            )
                            CuteText(
                                text = musicState.artist,
                                color = MaterialTheme.colorScheme.primary.copy(0.85f),
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