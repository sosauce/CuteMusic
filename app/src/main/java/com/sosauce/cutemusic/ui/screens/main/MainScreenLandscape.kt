package com.sosauce.cutemusic.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberSortASC
import com.sosauce.cutemusic.domain.model.Music
import com.sosauce.cutemusic.ui.customs.textCutter
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationRail
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PlayerState
import com.sosauce.cutemusic.ui.theme.GlobalFont

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenLandscape(
    musics: List<Music>,
    viewModel: MusicViewModel,
    navController: NavController
) {

    val sort by rememberSortASC()
    val displayMusics = when (sort) {
        true -> musics
        false -> musics.sortedByDescending { it.name }
    }


    Scaffold(
        topBar = {
            Row(modifier = Modifier.padding(start = 80.dp)) {
                CuteSearchbar(
                    musics = musics,
                    onNavigate = { navController.navigate(Screen.Settings) },
                    onClick = { viewModel.playAtIndex(it) }
                )
            }
        }
    ) { values ->
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(values)
                        .padding(start = 80.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    items(displayMusics) {music ->
                        MusicListItem(music) {
                            viewModel.populateLists()
                            viewModel.playAtIndex(music.uri)
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            bottom = values.calculateBottomPadding() + 10.dp,
                            end = values.calculateEndPadding(
                                layoutDirection = LayoutDirection.Rtl
                            ) + 10.dp
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { navController.navigate(Screen.NowPlaying) },
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                ) {
                    if (musics.isNotEmpty() && viewModel.playerState.value == PlayerState.PLAYING) {
                        MiniNowPlayingLandscape(
                            onHandlePlayerActions = viewModel::handlePlayerActions,
                            viewModel = viewModel
                        )
                    }
                }


            }
        CuteNavigationRail(
            selectedIndex = viewModel.selectedItem,
            onNavigationItemClicked = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            }
        )
        }
    }

@Composable
private fun MiniNowPlayingLandscape(
    onHandlePlayerActions: (PlayerActions) -> Unit,
    viewModel: MusicViewModel
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = textCutter(viewModel.currentlyPlaying, 18),
            fontFamily = GlobalFont,
            // modifier = Modifier.animateContentSize()
        )
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp)
        ) {
            IconButton(
                onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.FastRewind,
                    contentDescription = stringResource(id = R.string.previous_song)
                )
            }
            IconButton(
                onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) }
            ) {
                Icon(
                    imageVector = if (viewModel.isCurrentlyPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                    contentDescription = stringResource(id = R.string.play_pause_button)
                )
            }
            IconButton(
                onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.FastForward,
                    contentDescription = stringResource(id = R.string.next_button)
                )
            }
        }
    }
}