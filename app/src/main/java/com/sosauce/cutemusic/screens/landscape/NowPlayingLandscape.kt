package com.sosauce.cutemusic.screens.landscape

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.components.LoopButton
import com.sosauce.cutemusic.components.MusicSlider
import com.sosauce.cutemusic.components.ShuffleButton
import com.sosauce.cutemusic.logic.dataStore
import com.sosauce.cutemusic.logic.getSwipeSetting
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.coroutines.flow.Flow
import kotlin.math.abs

@Composable
fun NowPlayingLandscape(
    player: Player,
    viewModel: MusicViewModel,
    navController: NavController
) {

    NPLContent(
        player,
        viewModel,
        navController,
        onPlayOrPause = {
            if (viewModel.isPlayerPlaying) {
                player.pause()
                viewModel.isPlayerPlaying = false
            } else {
                player.play()
                viewModel.isPlayerPlaying = true
            }
        },
        onSeekNext = { player.seekToNextMediaItem() },
        onSeekPrevious = { player.seekToPreviousMediaItem() }
    )
}

@Composable
private fun NPLContent(
    player: Player,
    viewModel: MusicViewModel,
    navController: NavController,
    onSeekNext: () -> Unit,
    onSeekPrevious: () -> Unit,
    onPlayOrPause: () -> Unit,
) {

    val context = LocalContext.current

    val swipeGesturesEnabledFlow: Flow<Boolean> = getSwipeSetting(context.dataStore)
    val swipeGesturesEnabledState: State<Boolean> = swipeGesturesEnabledFlow.collectAsState(initial = false)

    Box(
        modifier = if (swipeGesturesEnabledState.value) {
            Modifier
                .fillMaxSize()
                .padding(45.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val (x, y) = dragAmount
                        if (abs(x) > abs(y)) {
                            if (x > 0) player.seekToPreviousMediaItem() else player.seekToNextMediaItem()
                        } else {
                            navController.navigate("MainScreen")
                        }
                    }
                }
        } else {
            Modifier
                .fillMaxSize()
                .padding(45.dp) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ) {
            AsyncImage(
                model = player.mediaMetadata.artworkData,
                contentDescription = "Artwork",
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(10)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = viewModel.title,
                    fontFamily = GlobalFont,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 30.sp
                )
                Text(
                    text = viewModel.artist,
                    fontFamily = GlobalFont,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
                MusicSlider(viewModel, player)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ShuffleButton(player, viewModel)
                    IconButton(
                        onClick = { onSeekPrevious() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FastRewind,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    FloatingActionButton(
                        onClick = { onPlayOrPause() }
                    ) {
                        Icon(
                            imageVector = if (viewModel.isPlayerPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                            contentDescription = "play/pause button"
                        )
                    }
                    IconButton(
                        onClick = { onSeekNext() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FastForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    LoopButton(player, viewModel)
                }
                if (!swipeGesturesEnabledState.value) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = { navController.navigate("MainScreen") }) {
                            Text(text = "Home Screen", fontFamily = GlobalFont)
                        }
                    }
                }
            }

        }
    }
}