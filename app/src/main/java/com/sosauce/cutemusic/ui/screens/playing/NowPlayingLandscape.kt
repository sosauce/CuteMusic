package com.sosauce.cutemusic.ui.screens.playing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.screens.playing.components.LoopButton
import com.sosauce.cutemusic.ui.screens.playing.components.MusicSlider
import com.sosauce.cutemusic.ui.screens.playing.components.ShuffleButton
import com.sosauce.cutemusic.ui.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun NowPlayingLandscape(
    viewModel: MusicViewModel,
    navController: NavController
) {

    NPLContent(
        viewModel = viewModel,
        onEvent = viewModel::handlePlayerActions,
        onNavigateUp = { navController.navigateUp() },
        onClickLoop = { viewModel.setLoop(it) },
        onClickShuffle = { viewModel.setShuffle(it) }
    )
}

@Composable
private fun NPLContent(
    viewModel: MusicViewModel,
    onNavigateUp: () -> Unit,
    onEvent: (PlayerActions) -> Unit,
    onClickLoop: (Boolean) -> Unit,
    onClickShuffle: (Boolean) -> Unit
) {
    var showSpeedCard by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }

    if (showSpeedCard) SpeedCard(
        viewModel = viewModel,
        onDismiss = { showSpeedCard = false }
    )
//    if (showQueueSheet) {
//
//        PlaylistQueueSheet(
//            viewModel = viewModel,
//            onDismiss = { showQueueSheet = false },
//            controller = viewModel.mediaController
//        )
//    }

    Scaffold { _ ->
        Box(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(45.dp)

        ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AsyncImage(
                        model = viewModel.currentArt,
                        contentDescription = "Artwork",
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(10)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
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
                                modifier = Modifier.fillMaxWidth(0.9f))
                            {
                                Text(
                                    text = viewModel.currentlyPlaying,
                                    fontFamily = GlobalFont,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 30.sp
                                )
                            }
                        }
                        Text(
                            text = viewModel.currentArtist,
                            fontFamily = GlobalFont,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        )
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
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            FloatingActionButton(
                                onClick = { onEvent(PlayerActions.PlayOrPause) }
                            ) {
                                Icon(
                                    imageVector = if (viewModel.isCurrentlyPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                                    contentDescription = "play/pause button"
                                )
                            }
                            IconButton(
                                onClick = { onEvent(PlayerActions.SeekToNextMusic) }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FastForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground
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
                        ) {
                            IconButton(onClick = { showSpeedCard = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.Speed,
                                    contentDescription = "change speed"
                                )
                            }
                        }
                    }

                }
        }
    }
}