@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.datastore.rememberSortASC
import com.sosauce.cutemusic.ui.customs.textCutter
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.components.BottomSheetContent
import com.sosauce.cutemusic.ui.screens.main.components.MiniNowPlayingContent
import com.sosauce.cutemusic.ui.shared_components.BottomBar
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.PlayerState
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.ImageUtils


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavController,
    musics: List<MediaItem>,
    viewModel: MusicViewModel,
    
) {

    val isLandscape = rememberIsLandscape()

    if (isLandscape) {
        MainScreenLandscape(
            musics = musics,
            viewModel = viewModel,
            navController = navController,
            
        )
    } else {
        MainScreenContent(
            musics = musics,
            playerState = viewModel.playerState.value,
            bottomBarIndex = viewModel.selectedItem,
            onNavigateTo = { navController.navigate(it) },
            currentlyPlaying = viewModel.currentlyPlaying,
            isCurrentlyPlaying = viewModel.isCurrentlyPlaying,
            onHandlePlayerActions = viewModel::handlePlayerActions,
            onShortClick = {
                viewModel.itemClicked(it)
            },
            onBottomBarNavigation = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            },
            
        )
    }


}


@Composable
private fun MainScreenContent(
    musics: List<MediaItem>,
    playerState: PlayerState,
    bottomBarIndex: Int,
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
    onBottomBarNavigation: (Int, NavigationItem) -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onShortClick: (String) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    
) {
    val sort by rememberSortASC()
    val lazyListState = rememberLazyListState()
    val displayMusics by remember(sort, musics) {
        derivedStateOf {
            if (sort) musics
            else musics.sortedByDescending{ it.mediaMetadata.title.toString() }
        }
    }
    val showMiniCard by remember(musics, playerState) {
        derivedStateOf {
            displayMusics.isNotEmpty() && playerState == PlayerState.PLAYING
        }
    }


    Scaffold(
        topBar = {
            CuteSearchbar(
                musics = musics,
                onNavigate = { onNavigateTo(it) },
                onClick = { onShortClick(it) },
                

            )
        },
        bottomBar = {
            BottomBar(
                selectedIndex = bottomBarIndex,
                onNavigationItemClicked = onBottomBarNavigation
                    )
                }
    ) { values ->
        Box(modifier = Modifier.fillMaxSize()) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values),
                    state = lazyListState,
                    ) {
                    if (displayMusics.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(id = R.string.no_musics_found),
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
                                onShortClick = { onShortClick(music.mediaId) },
                                music = music,
                                onNavigate = { onNavigateTo(it) },
                                
                            )
                        }
                    }
                }


            if (showMiniCard) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = values.calculateBottomPadding() + 5.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onNavigateTo(Screen.NowPlaying) },
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                ) {
                    MiniNowPlayingContent(
                        onHandlePlayerActions = onHandlePlayerActions,
                        currentlyPlaying = currentlyPlaying,
                        isCurrentlyPlaying = isCurrentlyPlaying
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
    music: MediaItem,
    onShortClick: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    
) {

    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    var isSheetOpen by remember { mutableStateOf(false) }

    if (isSheetOpen) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false }
        ) {
            BottomSheetContent(
                music = music,
                onNavigate = { onNavigate(it) },
                onDismiss = { isSheetOpen = false }
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onShortClick(music.mediaId) }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageUtils.imageRequester(
                    img = music.mediaMetadata.artworkUri,
                    context = context
                ),
                contentDescription = "Artwork",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(45.dp),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    text = textCutter(music.mediaMetadata.title.toString(), 25),
                    fontFamily = GlobalFont,
                    maxLines = 1
                )
                Text(
                    text = music.mediaMetadata.artist.toString(),
                    fontFamily = GlobalFont,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
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

