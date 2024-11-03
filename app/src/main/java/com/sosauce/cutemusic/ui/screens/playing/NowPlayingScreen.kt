@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.playing


import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.MusicState
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.lyrics.LyricsView
import com.sosauce.cutemusic.ui.screens.playing.components.ActionsButtonsRow
import com.sosauce.cutemusic.ui.screens.playing.components.MusicSlider
import com.sosauce.cutemusic.ui.screens.playing.components.QuickActionsRow
import com.sosauce.cutemusic.ui.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.utils.ImageUtils


@Composable
fun SharedTransitionScope.NowPlayingScreen(
    navController: NavController,
    viewModel: MusicViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    musicState: MusicState,
    onChargeAlbumSongs: (String) -> Unit,
    onChargeArtistLists: (String) -> Unit
) {
    var showFullLyrics by remember { mutableStateOf(false) }

    if (rememberIsLandscape()) {
        NowPlayingLandscape(
            viewModel = viewModel,
            onNavigateUp = navController::navigateUp,
            onEvent = { viewModel.handlePlayerActions(it) },
            onClickLoop = { viewModel.handlePlayerActions(PlayerActions.ApplyLoop) },
            onClickShuffle = { viewModel.handlePlayerActions(PlayerActions.ApplyShuffle) },
            animatedVisibilityScope = animatedVisibilityScope,
            musicState = musicState,
            onChargeAlbumSongs = onChargeAlbumSongs,
            onNavigate = { navController.navigate(it) },
            onChargeArtistLists = onChargeArtistLists
        )
    } else {
        when (showFullLyrics) {
            true -> {
                LyricsView(
                    viewModel = viewModel,
                    onHideLyrics = { showFullLyrics = false },
                    musicState = musicState
                )
            }

            false -> {
                NowPlayingContent(
                    viewModel = viewModel,
                    onEvent = viewModel::handlePlayerActions,
                    onNavigateUp = navController::navigateUp,
                    onClickLoop = { viewModel.handlePlayerActions(PlayerActions.ApplyLoop) },
                    onClickShuffle = { viewModel.handlePlayerActions(PlayerActions.ApplyShuffle) },
                    animatedVisibilityScope = animatedVisibilityScope,
                    onShowLyrics = { showFullLyrics = true },
                    musicState = musicState,
                    onChargeAlbumSongs = onChargeAlbumSongs,
                    onNavigate = { navController.navigate(it) },
                    onChargeArtistLists = onChargeArtistLists
                )
            }
        }
    }

}

@Composable
private fun SharedTransitionScope.NowPlayingContent(
    viewModel: MusicViewModel,
    onEvent: (PlayerActions) -> Unit,
    onNavigateUp: () -> Unit,
    onClickLoop: () -> Unit,
    onClickShuffle: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onShowLyrics: () -> Unit,
    musicState: MusicState,
    onChargeAlbumSongs: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onChargeArtistLists: (String) -> Unit
) {
    val context = LocalContext.current
    var showSpeedCard by remember { mutableStateOf(false) }
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
                onClick = onNavigateUp
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                )
            }
        }
        AsyncImage(
            model = ImageUtils.imageRequester(
                img = musicState.currentArt,
                context = context
            ),
            contentDescription = stringResource(R.string.artwork),
            modifier = Modifier
                .size(340.dp)
                .clip(RoundedCornerShape(5)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                CuteText(
                    text = musicState.currentlyPlaying,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .sharedElement(
                            state = rememberSharedContentState(key = "currentlyPlaying"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                tween(durationMillis = 500)
                            }
                        )
                        .basicMarquee()
                )
                //Spacer(modifier = Modifier.height(5.dp))
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
            viewModel = viewModel,
            musicState = musicState
        )
        Spacer(modifier = Modifier.height(7.dp))
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
            onShowLyrics = onShowLyrics,
            onChargeAlbumSongs = onChargeAlbumSongs,
            onShowSpeedCard = { showSpeedCard = true },
            onChargeArtistLists = onChargeArtistLists
        )
    }
}







