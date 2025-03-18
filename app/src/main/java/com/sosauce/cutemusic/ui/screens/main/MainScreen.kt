@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.sosauce.cutemusic.ui.screens.main

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberAllSafTracks
import com.sosauce.cutemusic.data.datastore.rememberGroupByFolders
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.ui.shared_components.SafMusicListItem
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import java.io.File

@Composable
fun SharedTransitionScope.MainScreen(
    musics: List<MediaItem>,
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
    onNavigate: (Screen) -> Unit,
    onShortClick: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
    isPlayerReady: Boolean,
    currentMusicUri: String,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onDeleteMusic: (List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit,
    onChargeAlbumSongs: (String) -> Unit,
    onChargeArtistLists: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val state = rememberLazyListState()
    var isSortedByASC by remember { mutableStateOf(true) } // I prolly should change this
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val groupByFolders by rememberGroupByFolders()
    val showCuteSearchbar by remember {
        derivedStateOf {
            if (musics.isEmpty()) {
                true
            } else if (
            // Are both the first and last element visible ?
                state.layoutInfo.visibleItemsInfo.firstOrNull()?.index == 0 &&
                state.layoutInfo.visibleItemsInfo.lastOrNull()?.index == musics.size - 1
            ) {
                true
            } else {
                state.layoutInfo.visibleItemsInfo.lastOrNull()?.index != musics.size - 1
            }
        }
    }
    val displayMusics by remember(isSortedByASC, musics, query) {
        derivedStateOf {
            if (query.isNotEmpty()) {
                musics.filter {
                    it.mediaMetadata.title?.contains(
                        other = query,
                        ignoreCase = true
                    ) == true
                }
            } else {
                if (isSortedByASC) musics
                else musics.sortedByDescending { it.mediaMetadata.title.toString() }
            }

        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = state,
                contentPadding = paddingValues
            ) {
                if (groupByFolders) {
                    displayMusics.groupBy {
                        File(
                            it.mediaMetadata.extras?.getString("folder") ?: ""
                        ).name
                    }
                        .forEach { (folderName, allMusics) ->
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(0.95f)
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceContainer,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .align(Alignment.Center)
                                            .padding(vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.folder_rounded),
                                            contentDescription = null,
                                            modifier = Modifier.padding(start = 5.dp)
                                        )
                                        Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                                        CuteText(folderName)
                                    }
                                }
                            }
                            items(
                                items = allMusics,
                                key = { it.mediaId }
                            ) { music ->
                                Column(
                                    modifier = Modifier
                                        .animateItem()
                                        .padding(
                                            vertical = 2.dp,
                                            horizontal = 4.dp
                                        )
                                ) {
                                    LocalMusicListItem(
                                        onShortClick = { onShortClick(music.mediaId) },
                                        music = music,
                                        onNavigate = { onNavigate(it) },
                                        currentMusicUri = currentMusicUri,
                                        onLoadMetadata = onLoadMetadata,
                                        showBottomSheet = true,
                                        onDeleteMusic = onDeleteMusic,
                                        onChargeAlbumSongs = onChargeAlbumSongs,
                                        onChargeArtistLists = onChargeArtistLists,
                                        isPlayerReady = isPlayerReady
                                    )
                                }
                            }
                        }
                } else {
                    if (displayMusics.isEmpty()) {
                        item {
                            CuteText(
                                text = stringResource(id = R.string.no_musics_found),
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(
                            items = displayMusics,
                            key = { it.mediaId }
                        ) { music ->
                            Column(
                                modifier = Modifier
                                    .animateItem()
                                    .padding(
                                        vertical = 2.dp,
                                        horizontal = 4.dp
                                    )
                            ) {
                                if (music.mediaMetadata.extras?.getBoolean("is_saf") == false) {
                                    LocalMusicListItem(
                                        onShortClick = { onShortClick(music.mediaId) },
                                        music = music,
                                        onNavigate = { onNavigate(it) },
                                        currentMusicUri = currentMusicUri,
                                        onLoadMetadata = onLoadMetadata,
                                        showBottomSheet = true,
                                        onDeleteMusic = onDeleteMusic,
                                        onChargeAlbumSongs = onChargeAlbumSongs,
                                        onChargeArtistLists = onChargeArtistLists,
                                        isPlayerReady = isPlayerReady
                                    )
                                } else {
                                    var safTracks by rememberAllSafTracks()

                                    SafMusicListItem(
                                        onShortClick = { onShortClick(music.mediaId) },
                                        music = music,
                                        currentMusicUri = currentMusicUri,
                                        showBottomSheet = true,
                                        isPlayerReady = isPlayerReady,
                                        onDeleteFromSaf = {
                                            safTracks = safTracks.toMutableSet().apply {
                                                remove(music.mediaMetadata.extras?.getString("uri"))
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Crossfade(
                targetState = showCuteSearchbar,
                label = "",
                modifier = Modifier.align(Alignment.BottomCenter)
            ) { visible ->
                if (visible) {
                    CuteSearchbar(
                        query = query,
                        onQueryChange = { query = it },
                        trailingIcon = {
                            Row {
                                IconButton(
                                    onClick = { sortMenuExpanded = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.Sort,
                                        contentDescription = null
                                    )
                                }
                                IconButton(
                                    onClick = { onNavigate(Screen.Settings) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = null
                                    )
                                }
                                SortingDropdownMenu(
                                    expanded = sortMenuExpanded,
                                    onDismissRequest = { sortMenuExpanded = false },
                                    isSortedByASC = isSortedByASC,
                                    onChangeSorting = { isSortedByASC = it }
                                )
                            }
                        },
                        currentlyPlaying = currentlyPlaying,
                        onHandlePlayerActions = onHandlePlayerAction,
                        isPlaying = isCurrentlyPlaying,
                        animatedVisibilityScope = animatedVisibilityScope,
                        isPlayerReady = isPlayerReady,
                        onNavigate = onNavigate,
                        fab = {
                            CuteActionButton(
                                modifier = Modifier.sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "fab"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            ) { onHandlePlayerAction(PlayerActions.PlayRandom) }
                        },
                    )
                }
            }
        }
    }
}




