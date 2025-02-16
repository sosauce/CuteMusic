@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.playing


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
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
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.lyrics.LyricsView
import com.sosauce.cutemusic.ui.screens.playing.components.ActionsButtonsRow
import com.sosauce.cutemusic.ui.screens.playing.components.MusicSlider
import com.sosauce.cutemusic.ui.screens.playing.components.QuickActionsRow
import com.sosauce.cutemusic.ui.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.ui.shared_components.CuteText


@Composable
fun SharedTransitionScope.NowPlayingScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    musicState: MusicState,
    onChargeAlbumSongs: (String) -> Unit,
    onChargeArtistLists: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onPopBackstack: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    lyrics: List<Lyrics>
) {
    var showFullLyrics by remember { mutableStateOf(false) }

    if (rememberIsLandscape()) {
        NowPlayingLandscape(
            onNavigateUp = onPopBackstack,
            onHandlePlayerActions = onHandlePlayerActions,
            animatedVisibilityScope = animatedVisibilityScope,
            musicState = musicState,
            onChargeAlbumSongs = onChargeAlbumSongs,
            onNavigate = onNavigate,
            onChargeArtistLists = onChargeArtistLists,
            lyrics = lyrics
        )
    } else {
        AnimatedContent(showFullLyrics) { targetState ->
            when (targetState) {
                true -> {
                    LyricsView(
                        onHideLyrics = { showFullLyrics = false },
                        musicState = musicState,
                        onHandlePlayerActions = onHandlePlayerActions,
                        lyrics = lyrics
                    )
                }

                false -> {
                    NowPlayingContent(
                        onHandlePlayerActions = onHandlePlayerActions,
                        onNavigateUp = onPopBackstack,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onShowLyrics = { showFullLyrics = true },
                        musicState = musicState,
                        onChargeAlbumSongs = onChargeAlbumSongs,
                        onNavigate = onNavigate,
                        onChargeArtistLists = onChargeArtistLists,
                    )
                }
            }
        }
    }

}

@Composable
private fun SharedTransitionScope.NowPlayingContent(
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigateUp: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onShowLyrics: () -> Unit,
    musicState: MusicState,
    onChargeAlbumSongs: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onChargeArtistLists: (String) -> Unit
) {
    var showSpeedCard by remember { mutableStateOf(false) }
    var snap by rememberSnapSpeedAndPitch()

    if (showSpeedCard) {
        SpeedCard(
            onDismiss = { showSpeedCard = false },
            shouldSnap = snap,
            onChangeSnap = { snap = !snap },
            musicState = musicState,
            onHandlePlayerAction = onHandlePlayerActions
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onNavigateUp,
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                )
            }
        }
        Crossfade(musicState.currentArt) {
            AsyncImage(
                model = it,
                contentDescription = stringResource(R.string.artwork),
                modifier = Modifier
                    .size(340.dp)
                    .clip(RoundedCornerShape(5)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CuteText(
                    text = musicState.currentlyPlaying,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .basicMarquee()
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "currentlyPlaying"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                )
                CuteText(
                    text = musicState.currentArtist,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                    fontSize = 14.sp,
                    modifier = Modifier.basicMarquee()
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        MusicSlider(
            onHandlePlayerActions = onHandlePlayerActions,
            musicState = musicState
        )
        Spacer(modifier = Modifier.height(7.dp))
        ActionsButtonsRow(
            onHandlePlayerActions = onHandlePlayerActions,
            animatedVisibilityScope = animatedVisibilityScope,
            musicState = musicState
        )
        Spacer(modifier = Modifier.weight(1f))
        QuickActionsRow(
            musicState = musicState,
            onNavigate = onNavigate,
            onShowLyrics = onShowLyrics,
            onChargeAlbumSongs = onChargeAlbumSongs,
            onShowSpeedCard = { showSpeedCard = true },
            onChargeArtistLists = onChargeArtistLists,
            onHandlePlayerActions = onHandlePlayerActions
        )
    }
}







