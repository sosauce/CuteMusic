@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.sosauce.cutemusic.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.getMusicArt
import com.sosauce.cutemusic.components.BottomSheetContent
import com.sosauce.cutemusic.components.CuteSearchbar
import com.sosauce.cutemusic.components.MiniNowPlayingContent
import com.sosauce.cutemusic.logic.PreferencesKeys
import com.sosauce.cutemusic.logic.dataStore
import com.sosauce.cutemusic.logic.imageRequester
import com.sosauce.cutemusic.screens.landscape.MainScreenLandscape
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.coroutines.flow.map

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController, player: Player, musics: List<Music>, viewModel: MusicViewModel) {

    val config = LocalConfiguration.current

    if (config.orientation != Configuration.ORIENTATION_PORTRAIT) {
        MainScreenLandscape(musics, viewModel, player, navController)
    } else {
        MainScreenContent(navController, viewModel, musics, player)
    }

}



@Composable
private fun MainScreenContent(
    navController: NavController,
    viewModel: MusicViewModel,
    musics: List<Music>,
    player: Player
) {
    val context = LocalContext.current
    val dataStore = remember { context.dataStore }
    val sortState = remember { mutableStateOf<String?>(null) }
    val lazyListState = rememberLazyListState()



    LaunchedEffect(Unit) {
        val sortFlow = dataStore.data.map {preferences ->
            preferences[PreferencesKeys.SORT_ORDER]
        }

        sortFlow.collect { sort ->
            sortState.value = sort
        }
    }


    val displayMusics = when (sortState.value) {
        "Ascending" -> musics
        "Descending" -> musics.sortedByDescending { it.title }
        else -> musics
    }

            Scaffold(
                topBar = {
                         CuteSearchbar(
                             viewModel = viewModel,
                             musics = musics,
                             onNavigate = { navController.navigate("SettingsScreen") }
                         )
                },
//                bottomBar = {
//                    BottomBar(
//                        navController = navController
//                    )
//                }
            ) { values ->
                val selectedItems = remember { mutableListOf<Int>() }
                Box(modifier = Modifier.fillMaxSize()){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(values),
                        state = lazyListState
                    ) {
                        items(displayMusics) {music ->
                            val isSelected = selectedItems.contains(music.id.toInt())
                            MusicListItem(
                                music,
                                onShortClick =  { viewModel.play(music.uri) },
                                onSelected =  { selectedItems.add(music.id.toInt()) },
                                isSelected = isSelected
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = values.calculateBottomPadding() + 5.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { navController.navigate("NowPlaying") },
                        color = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        MiniNowPlayingContent(
                            onSeekNext = { player.seekToNextMediaItem() },
                            onSeekPrevious = { player.seekToPreviousMediaItem() },
                            onPlayOrPause = { if (viewModel.isPlayerPlaying) player.pause() else player.play() },
                            viewModel = viewModel
                        )
                    }
                }
            }
}


@SuppressLint("UnsafeOptInUsageError")
@Composable
fun MusicListItem(
    music: Music,
    onShortClick: (Uri) -> Unit,
    onSelected: () -> Unit,
    isSelected: Boolean
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
                .combinedClickable(
                    onClick = { onShortClick(music.uri) },
                    onLongClick = { onSelected() }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isSelected) {
                    AsyncImage(
                        model = imageRequester(
                            img = art ?: R.drawable.cute_music_icon,
                            context = context
                        ),
                        contentDescription = "Artwork",
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(45.dp)
                            .clip(RoundedCornerShape(15)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(art ?: R.drawable.cute_music_icon),
                        contentDescription = "Artwork",
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(45.dp)
                            .clip(RoundedCornerShape(15)),
                        contentScale = ContentScale.Crop,
                    )
                }

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

