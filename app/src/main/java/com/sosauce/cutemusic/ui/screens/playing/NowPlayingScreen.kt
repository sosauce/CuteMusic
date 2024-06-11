package com.sosauce.cutemusic.ui.screens.playing


import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.customs.textCutter
import com.sosauce.cutemusic.ui.screens.playing.components.LoopButton
import com.sosauce.cutemusic.ui.screens.playing.components.MusicSlider
import com.sosauce.cutemusic.ui.screens.playing.components.ShuffleButton
import com.sosauce.cutemusic.ui.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.ImageUtils


@OptIn(UnstableApi::class)
@Composable
fun NowPlayingScreen(
    navController: NavController,
    viewModel: MusicViewModel
) {
    val config = LocalConfiguration.current
    if (config.orientation != Configuration.ORIENTATION_PORTRAIT) {
        NowPlayingLandscape(viewModel, navController)
    } else {
        NowPlayingContent(
            viewModel = viewModel,
            onEvent = viewModel::handlePlayerActions,
            onNavigateUp = { navController.navigate(navController.graph.startDestinationId) },
            onClickLoop = { viewModel.setLoop(it) },
            onClickShuffle = { viewModel.setShuffle(it) }
        )
    }

}

@Composable
private fun NowPlayingContent(
    viewModel: MusicViewModel,
    onEvent: (PlayerActions) -> Unit,
    onNavigateUp: () -> Unit,
    onClickLoop: (Boolean) -> Unit,
    onClickShuffle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var showSpeedCard by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }

    if (showSpeedCard) {
        SpeedCard(
            viewModel = viewModel,
            onDismiss = { showSpeedCard = false }
        )
    }

//    if (showQueueSheet) {
//        PlaylistQueueSheet(
//            viewModel = viewModel,
//            onDismiss = { showQueueSheet = false },
//            controller = viewModel.mediaController
//        )
//    }

    Scaffold(
        modifier = Modifier.padding(15.dp)
    ) { values ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                        onClick = { onNavigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                AsyncImage(
                    model = ImageUtils.imageRequester(
                        img = viewModel.currentArt,
                        context = context
                    ),
                    contentDescription = "Artwork",
                    modifier = Modifier
                        .size(340.dp)
                        .clip(RoundedCornerShape(5)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = textCutter(viewModel.currentlyPlaying, 35),
                        fontFamily = GlobalFont,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp
                    )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = textCutter(viewModel.currentArtist, 35),
                    fontFamily = GlobalFont,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                MusicSlider(viewModel)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ShuffleButton(
                        onClick = { onClickShuffle(it) }
                    )
                    IconButton(
                        onClick = { onEvent(PlayerActions.SeekToPreviousMusic) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FastRewind,
                            contentDescription = null
                        )
                    }
                    FloatingActionButton(
                        onClick = { onEvent(PlayerActions.PlayOrPause) }
                    ) {
                        Icon(
                            imageVector = if (viewModel.isCurrentlyPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                            contentDescription = "pause/play button"
                        )
                    }


                    IconButton(
                        onClick = { onEvent(PlayerActions.SeekToNextMusic) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FastForward,
                            contentDescription = null
                        )
                    }
                    LoopButton(
                        onClick = { onClickLoop(it) }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    IconButton(onClick = { showSpeedCard = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Speed,
                            contentDescription = "change speed"
                        )
                    }
//                    IconButton(onClick = { showQueueSheet = true }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Outlined.QueueMusic,
//                            contentDescription = "change speed"
//                        )
//                    }
                }
            }
    }
}

//@Preview
//@Composable
//private fun NPPrev() {
//    CuteMusicTheme {
//        NowPlayingContent(viewModel = DummyViewModel(), onEvent = {  }) {
//
//        }
//    }
//
//}






