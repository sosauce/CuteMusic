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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.logic.PlayerActions
import com.sosauce.cutemusic.screens.MusicListItem
import com.sosauce.cutemusic.ui.theme.GlobalFont

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenLandscape(
    musics: List<Music>,
    viewModel: MusicViewModel,
    navController: NavController,
    state: MusicState,
    onNavigate: () -> Unit
) {

    Scaffold(
        topBar = {
            AppBar(
                title = "landscape",
                showBackArrow = false,
                showMenuIcon = true,
                onNavigate = { onNavigate() }
            )
        }
    ) { values ->

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(values),
                verticalArrangement = Arrangement.Top
            ) {
                itemsIndexed(musics) { index, music ->
                    MusicListItem(
                        music
                    ) { viewModel.play(music.uri) }
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
                    text = if (state.currentlyPlaying == "null") "" else state.currentlyPlaying,
                    fontFamily = GlobalFont,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Row(horizontalArrangement = Arrangement.End) {
                    IconButton(
                        onClick = { viewModel.handlePlayerActions(PlayerActions.SeekToPreviousMusic) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FastRewind,
                            contentDescription = "previous song"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.handlePlayerActions(PlayerActions.PlayOrPause) }
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                            contentDescription = "play/pause button"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.handlePlayerActions(PlayerActions.SeekToNextMusic) }
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