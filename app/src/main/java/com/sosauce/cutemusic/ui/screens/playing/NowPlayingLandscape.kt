@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.playing

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateIntAsState
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.lyrics.LyricsView
import com.sosauce.cutemusic.ui.screens.playing.components.ActionsButtonsRow
import com.sosauce.cutemusic.ui.screens.playing.components.MusicSlider
import com.sosauce.cutemusic.ui.screens.playing.components.QuickActionsRow
import com.sosauce.cutemusic.ui.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel

@Composable
fun SharedTransitionScope.NowPlayingLandscape(
    viewModel: MusicViewModel,
    onNavigateUp: () -> Unit,
    onEvent: (PlayerActions) -> Unit,
    onClickLoop: () -> Unit,
    onClickShuffle: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    musicState: MusicState,
    onChargeAlbumSongs: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onChargeArtistLists: (String) -> Unit
) {
    var showSpeedCard by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }
    var snap by rememberSnapSpeedAndPitch()


    if (showSpeedCard) {
        SpeedCard(
            onDismiss = { showSpeedCard = false },
            shouldSnap = snap,
            onChangeSnap = { snap = !snap },
            musicState = musicState,
            onHandlePlayerAction = onEvent
        )
    }

    val imgSize by animateIntAsState(
        targetValue = if (showLyrics) 200 else 320,
        label = "Image Size"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(start = 30.dp, end = 30.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {
                AsyncImage(
                    model = musicState.currentArt,
                    stringResource(R.string.artwork),
                    modifier = Modifier
                        .size(imgSize.dp)
                        .clip(RoundedCornerShape(10)),
                    contentScale = ContentScale.Crop
                )
                if (showLyrics) {
                    Spacer(Modifier.height(10.dp))
                    CuteText(
                        text = musicState.currentlyPlaying
                    )
                    CuteText(
                        text = musicState.currentArtist
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            if (showLyrics) {
                LyricsView(
                    viewModel = viewModel,
                    onHideLyrics = { showLyrics = false },
                    isLandscape = true,
                    musicState = musicState
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onNavigateUp() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )
                        {
                            CuteText(
                                text = musicState.currentlyPlaying,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 30.sp
                            )
                        }
                    }
                    CuteText(
                        text = musicState.currentArtist,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                        fontSize = 16.sp
                    )
                    MusicSlider(
                        viewModel = viewModel,
                        musicState = musicState
                    )
                    ActionsButtonsRow(
                        onClickLoop = onClickLoop,
                        onClickShuffle = onClickShuffle,
                        onEvent = onEvent,
                        animatedVisibilityScope = animatedVisibilityScope,
                        musicState = musicState
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    QuickActionsRow(
                        musicState = musicState,
                        onNavigate = onNavigate,
                        onShowLyrics = { showLyrics = true },
                        onChargeAlbumSongs = onChargeAlbumSongs,
                        onShowSpeedCard = { showSpeedCard = true },
                        onChargeArtistLists = onChargeArtistLists,
                        onHandlePlayerActions = onEvent,
                    )
                }
            }

        }
    }
}