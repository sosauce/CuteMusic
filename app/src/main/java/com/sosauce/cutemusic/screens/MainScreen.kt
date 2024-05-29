@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.getMusicArt
import com.sosauce.cutemusic.components.BottomSheetContent
import com.sosauce.cutemusic.components.CuteSearchbar
import com.sosauce.cutemusic.components.MiniNowPlayingContent
import com.sosauce.cutemusic.logic.BottomBar
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.logic.PlayerState
import com.sosauce.cutemusic.logic.navigation.Screen
import com.sosauce.cutemusic.logic.rememberIsSwipeEnabled
import com.sosauce.cutemusic.logic.rememberSortASC
import com.sosauce.cutemusic.screens.landscape.MainScreenLandscape
import com.sosauce.cutemusic.ui.theme.GlobalFont

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavController,
    musics: List<Music>,
    viewModel: MusicViewModel,
    state: MusicState,
    onNavigate: () -> Unit
) {

    val config = LocalConfiguration.current

    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        MainScreenLandscape(
            musics = musics,
            viewModel = viewModel,
            navController = navController,
            state = state,
            onNavigate = { onNavigate() }
        )
    } else {
        MainScreenContent(
            navController = navController,
            viewModel = viewModel,
            musics = musics,
            state = state
        )
    }


}


@Composable
private fun MainScreenContent(
    navController: NavController,
    viewModel: MusicViewModel,
    musics: List<Music>,
    state: MusicState
) {
    val sort by rememberSortASC()
    val lazyListState = rememberLazyListState()
    val swipeGesturesEnabled by rememberIsSwipeEnabled()
    val displayMusics = when (sort) {
        true -> musics
        false -> musics.sortedByDescending { it.title }
    }

    Scaffold(
        topBar = {
            CuteSearchbar(
                viewModel = viewModel,
                musics = musics,
                onNavigate = { navController.navigate(Screen.Settings) }
            )
        },
        bottomBar = {
            BottomBar(
                navController = navController,
                viewModel
            )
        }
    ) { values ->
        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values),
                state = lazyListState
            ) {
                if (displayMusics.isEmpty()) {
                    item {
                        Text(
                            text = "No music found !",
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontFamily = GlobalFont
                        )
                    }
                } else {
                    items(displayMusics) { music ->
                        MusicListItem(
                            music,
                            onShortClick = { viewModel.play(music.uri) }
                        )
                    }
                }
            }


            if (displayMusics.isNotEmpty() && viewModel.playerState.value != PlayerState.STOPPED) {
                Surface(
                    modifier = if (swipeGesturesEnabled) {
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = values.calculateBottomPadding() + 5.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { navController.navigate(Screen.NowPlaying) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    change.consume()
                                    navController.navigate(Screen.NowPlaying)
                                }
                            }
                    } else {
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = values.calculateBottomPadding() + 5.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { navController.navigate(Screen.NowPlaying) }
                    },
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                ) {
                    MiniNowPlayingContent(
                        onHandlePlayerActions = viewModel::handlePlayerActions,
                        state = state,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun MusicListItem(
    music: Music,
    onShortClick: (Uri) -> Unit
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onShortClick(music.uri) },
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(art),
                contentDescription = "Artwork",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(45.dp)
                    .clip(RoundedCornerShape(15)),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    text = if (music.title.length >= 25) music.title.take(25) + "..." else music.title,
                    fontFamily = GlobalFont,
                    maxLines = 1
                )
                Text(
                    text = music.artist,
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

