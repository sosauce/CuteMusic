@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.sosauce.cutemusic.ui.screens.main

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberAllSafTracks
import com.sosauce.cutemusic.data.datastore.rememberGroupByFolders
import com.sosauce.cutemusic.data.datastore.rememberHiddenFolders
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.ui.shared_components.SafMusicListItem
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.addOrRemove
import com.sosauce.cutemusic.utils.copyMutate
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.showCuteSearchbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun SharedTransitionScope.MainScreen(
    musics: List<MediaItem>,
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
    onNavigate: (Screen) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
    isPlayerReady: Boolean,
    currentMusicUri: String,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onHandleMediaItemAction: (MediaItemActions) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    val state = rememberLazyListState()
    var hiddenFolders by rememberHiddenFolders()
    var isSortedByASC by rememberSaveable { mutableStateOf(true) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var groupByFolders by rememberGroupByFolders()
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
                    displayMusics.groupBy { it.mediaMetadata.extras?.getString("folder") ?: "" }
                        .forEach { (folder, allMusics) ->

                            item(
                                key = folder
                            ) {
                                val iconRotation by animateFloatAsState(
                                    targetValue = if (folder in hiddenFolders) 90f else 0f
                                )

                                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { hiddenFolders = hiddenFolders.copyMutate { addOrRemove(folder) } }
                                            .padding(
                                                horizontal = 25.dp,
                                                vertical = 8.dp
                                            ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.folder_rounded),
                                            contentDescription = null
                                        )
                                        Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                                        CuteText(
                                            text = File(folder).name,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                            contentDescription = null,
                                            modifier = Modifier.rotate(iconRotation)
                                        )
                                    }
                                }

                            }
                            items(
                                items = allMusics,
                                key = { it.mediaId }
                            ) { music ->
                                AnimatedVisibility(
                                    visible = folder !in hiddenFolders,
                                    enter = slideInHorizontally { -it },
                                    exit = slideOutHorizontally { -it }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .animateItem()
                                            .padding(
                                                vertical = 2.dp,
                                                horizontal = 4.dp
                                            )
                                    ) {
                                        LocalMusicListItem(
                                            onShortClick = { onHandlePlayerAction(PlayerActions.StartPlayback(music.mediaId)) },
                                            music = music,
                                            onNavigate = { onNavigate(it) },
                                            currentMusicUri = currentMusicUri,
                                            onLoadMetadata = onLoadMetadata,
                                            isPlayerReady = isPlayerReady,
                                            onHandleMediaItemAction = onHandleMediaItemAction,
                                            onHandlePlayerActions = onHandlePlayerAction
                                        )
                                    }
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
                                        onShortClick = { onHandlePlayerAction(PlayerActions.StartPlayback(music.mediaId)) },
                                        music = music,
                                        onNavigate = { onNavigate(it) },
                                        currentMusicUri = currentMusicUri,
                                        onLoadMetadata = onLoadMetadata,
                                        isPlayerReady = isPlayerReady,
                                        onHandleMediaItemAction = onHandleMediaItemAction,
                                        onHandlePlayerActions = onHandlePlayerAction
                                    )
                                } else {
                                    var safTracks by rememberAllSafTracks()

                                    SafMusicListItem(
                                        onShortClick = { onHandlePlayerAction(PlayerActions.StartPlayback(music.mediaId)) },
                                        music = music,
                                        currentMusicUri = currentMusicUri,
                                        isPlayerReady = isPlayerReady,
                                        onDeleteFromSaf = {
                                            safTracks = safTracks.copyMutate { remove(music.mediaMetadata.extras?.getString("uri")) }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = state.showCuteSearchbar,
                modifier = Modifier.align(rememberSearchbarAlignment()),
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                CuteSearchbar(
                    query = query,
                    onQueryChange = { query = it },
                    isScrolling = state.isScrollInProgress,
                    trailingIcon = {
                        IconButton(
                            onClick = { sortMenuExpanded = true }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Sort,
                                contentDescription = stringResource(R.string.sort),
                            )
                        }
                        SortingDropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false },
                            isSortedByASC = isSortedByASC,
                            onChangeSorting = { isSortedByASC = it },
                            additionalActions = {
                                CuteDropdownMenuItem(
                                    onClick = { groupByFolders = !groupByFolders },
                                    text = { CuteText(stringResource(R.string.group_tracks)) },
                                    leadingIcon = {
                                        Checkbox(
                                            checked = groupByFolders,
                                            onCheckedChange = null
                                        )
                                    }
                                )
                            }
                        )
                    },
                    currentlyPlaying = currentlyPlaying,
                    onHandlePlayerActions = onHandlePlayerAction,
                    isPlaying = isCurrentlyPlaying,
                    isPlayerReady = isPlayerReady,
                    onNavigate = onNavigate,
                    fab = {
                        CuteActionButton(
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.FAB),
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
                            )
                        ) { onHandlePlayerAction(PlayerActions.PlayRandom) }
                    }
                )
            }
        }
    }
}




