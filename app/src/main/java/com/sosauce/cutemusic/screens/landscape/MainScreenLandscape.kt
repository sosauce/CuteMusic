package com.sosauce.cutemusic.screens.landscape

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.navigation.NavController
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.screens.MusicListItem
import com.sosauce.cutemusic.ui.theme.GlobalFont

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenLandscape(
    musics: List<Music>,
    viewModel: MusicViewModel,
    player: Player,
    navController: NavController
) {
    val playerState = remember { viewModel.playerState }

    Scaffold(
        topBar = {
            AppBar(
                title = "landscape",
                navController = navController,
                showBackArrow = false,
                showMenuIcon = true,
                showSearchIcon = true
            ) {

            }
        }
    ){ values ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(values),
                verticalArrangement = Arrangement.Top
            ) {
                items(musics.size) { index ->
                    val music = remember { musics[index] }
                    MusicListItem(music) { viewModel.play(music.uri)  }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.background)
                    .clickable { navController.navigate("NowPlaying") }
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viewModel.playerState.value.title.toString(),
                    fontFamily = GlobalFont,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Row(horizontalArrangement = Arrangement.End) {
                     IconButton(
                        onClick = { player.seekToPreviousMediaItem() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FastRewind,
                            contentDescription = "previous song"
                        )
                    }
                     IconButton(
                        onClick = {
                            if (playerState.value.isPlaying == true) {
                                player.pause()
                                playerState.value.isPlaying = false
                            } else {
                                player.play()
                                playerState.value.isPlaying = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (playerState.value.isPlaying == true) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                            contentDescription = "play/pause button"
                        )
                    }
                     IconButton(
                        onClick = { player.seekToNextMediaItem() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FastForward,
                            contentDescription = "next song"
                        )
                    }
                }
            }
        }

    }

}