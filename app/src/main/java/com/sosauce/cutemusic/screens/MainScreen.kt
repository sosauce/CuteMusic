@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.media3.common.Player
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.PlayerState
import com.sosauce.cutemusic.audio.getMusicArt
import com.sosauce.cutemusic.components.BottomSheetContent
import com.sosauce.cutemusic.components.CuteSearchbar
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.logic.BottomBar
import com.sosauce.cutemusic.screens.landscape.MainScreenLandscape
import com.sosauce.cutemusic.ui.theme.GlobalFont

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController, player: Player, musics: List<Music>, viewModel: MusicViewModel, playerState: MutableState<PlayerState>) {

    val config = LocalConfiguration.current

    if (config.orientation != Configuration.ORIENTATION_PORTRAIT) {
        MainScreenLandscape(musics, viewModel, player, navController)
    } else {
        MainScreenContent(navController, player, viewModel, musics, playerState)
    }

}



@Composable
private fun MainScreenContent(
    navController: NavController,
    player: Player,
    viewModel: MusicViewModel,
    musics: List<Music>,
    playerState: MutableState<PlayerState>
) {
    val greetings = listOf("Hi! 👋")
    val greeting by remember { mutableStateOf(greetings.random()) }
    var showSearchbar by remember { mutableStateOf(false) }
    val onSearchBarStateChanged: (Boolean) -> Unit = { isVisible -> showSearchbar = isVisible }


    if (showSearchbar) {
        CuteSearchbar({ showSearchbar = false }, viewModel, musics)
    }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Scaffold(
                topBar = {
                    AppBar(
                        title = greeting,
                        showBackArrow = false,
                        showMenuIcon = true,
                        navController = navController,
                        showSearchIcon = true,
                        onSearchBarStateChanged = onSearchBarStateChanged
                    )
                },
                bottomBar = {
                    BottomBar(
                        viewModel = viewModel,
                        onSeekNext = { player.seekToNextMediaItem() },
                        onSeekPrevious = { player.seekToPreviousMediaItem() },
                        onPlayOrPause = {
                            if (playerState.value.isPlaying == true) {
                                player.pause(); playerState.value.isPlaying = false
                            } else {
                                player.play(); playerState.value.isPlaying = true
                            }
                        },
                        navController = navController
                    )
                }
            ) { values ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ) {
                    items(musics, key = { music -> music.id }) { music ->
                        MusicListItem(music) { viewModel.play(music.uri) }
                    }
                }
            }
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun MusicListItem(
    music: Music,
    onClick: (Uri) -> Unit
) {

    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    var isSheetOpen by remember { mutableStateOf(false) }
    var art: ByteArray? by remember { mutableStateOf(byteArrayOf()) }

    LaunchedEffect(music.uri) {
        art = getMusicArt(context, music)
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false }
        ) {
            BottomSheetContent(music)
        }
    }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(music.uri) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = art ?: R.drawable.cute_music_icon,
                    contentDescription = "Artwork",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(45.dp)
                        .clip(RoundedCornerShape(15)),
                    contentScale = ContentScale.Crop
                )

            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    text = if (music.title.length >= 25) music.title.take(25) + "..." else music.title,
                    fontFamily = GlobalFont,
                    maxLines = 1
                )
                Text(text = music.artist,
                    fontFamily = GlobalFont
                )
            }
        }
        IconButton(onClick = { isSheetOpen = true }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = null
            )
        }
    }
}

