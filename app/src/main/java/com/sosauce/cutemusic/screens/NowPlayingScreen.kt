package com.sosauce.cutemusic.screens


import android.content.res.Configuration
import androidx.annotation.OptIn
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
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.components.LoopButton
import com.sosauce.cutemusic.components.MusicSlider
import com.sosauce.cutemusic.components.ShuffleButton
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.logic.PlayerActions
import com.sosauce.cutemusic.logic.dataStore
import com.sosauce.cutemusic.logic.getSwipeSetting
import com.sosauce.cutemusic.logic.imageRequester
import com.sosauce.cutemusic.screens.landscape.NowPlayingLandscape
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.coroutines.flow.Flow
import kotlin.math.abs


@OptIn(UnstableApi::class)
@Composable
fun NowPlayingScreen(
    navController: NavController,
    viewModel: MusicViewModel,
    player: Player,
    state: MusicState
) {
    val config = LocalConfiguration.current

    if (config.orientation != Configuration.ORIENTATION_PORTRAIT) {
        NowPlayingLandscape(player, viewModel, navController, state)
    } else {
        NowPlayingContent(
            player = player,
            viewModel = viewModel,
            onPlayOrPause = {
                if (state.isPlaying) {
                    viewModel.handlePlayerActions(PlayerActions.Pause)
                } else {
                    viewModel.handlePlayerActions(PlayerActions.Play)
                }
            },
            onSeekNext = { viewModel.handlePlayerActions(PlayerActions.SeekToNextMusic) },
            onSeekPrevious = { viewModel.handlePlayerActions(PlayerActions.SeekToPreviousMusic) },
            onNavigateUp = { navController.navigate(navController.graph.startDestinationId) },
            onLoop = { viewModel.handlePlayerActions(PlayerActions.ApplyLoop) },
            onShuffle = { viewModel.handlePlayerActions(PlayerActions.ApplyShuffle) },
            state = state
        )
    }

}

@Composable
private fun NowPlayingContent(
    player: Player,
    viewModel: MusicViewModel,
    onSeekNext: () -> Unit,
    onSeekPrevious: () -> Unit,
    onPlayOrPause: () -> Unit,
    onNavigateUp: () -> Unit,
    onLoop: () -> Unit,
    onShuffle: () -> Unit,
    state: MusicState
) {
    val context = LocalContext.current
    val swipeGesturesEnabledFlow: Flow<Boolean> = getSwipeSetting(context.dataStore)
    val swipeGesturesEnabledState: State<Boolean> =
        swipeGesturesEnabledFlow.collectAsState(initial = false)

    Scaffold { _ ->
        Box(
            modifier = if (swipeGesturesEnabledState.value) {
                Modifier
                    .padding(15.dp)
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val (x, y) = dragAmount
                            if (abs(x) > abs(y)) {
                                if (x > 0) onSeekPrevious() else onSeekNext()
                            } else {
                                onNavigateUp()
                            }
                        }
                    }
            } else {
                Modifier
                    .padding(15.dp)
                    .fillMaxSize()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(45.dp))
                AsyncImage(
                    model = imageRequester(
                        img = state.artwork,
                        context = context
                    ),
                    contentDescription = "Artwork",
                    modifier = Modifier
                        .size(340.dp)
                        .clip(RoundedCornerShape(5))
                )


                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = if (state.currentlyPlaying == "null") viewModel.previousTitle else state.currentlyPlaying,
                    fontFamily = GlobalFont,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = if (state.currentlyArtist == "null") viewModel.previousArtist else state.currentlyArtist,
                    fontFamily = GlobalFont,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                MusicSlider(player, state)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ShuffleButton(
                        player = player,
                        onShuffle = { onShuffle() }
                    )
                    IconButton(
                        onClick = { onSeekPrevious() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FastRewind,
                            contentDescription = null
                        )
                    }
                    FloatingActionButton(
                        onClick = { onPlayOrPause() }
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                            contentDescription = "pause/play button"
                        )
                    }


                    IconButton(
                        onClick = { onSeekNext() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FastForward,
                            contentDescription = null
                        )
                    }
                    LoopButton(
                        player = player,
                        onClick = { onLoop() }
                    )
                }
                if (!swipeGesturesEnabledState.value) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = { onNavigateUp() }) {
                            Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home")
                        }
                    }
                }
            }
        }

    }
}






