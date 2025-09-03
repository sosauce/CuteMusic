@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.main

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.media3.common.MediaItem
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberAllSafTracks
import com.sosauce.cutemusic.data.datastore.rememberGroupByFolders
import com.sosauce.cutemusic.data.datastore.rememberHiddenFolders
import com.sosauce.cutemusic.data.datastore.rememberTrackSort
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.presentation.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.CuteText
import com.sosauce.cutemusic.presentation.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.presentation.shared_components.NoResult
import com.sosauce.cutemusic.presentation.shared_components.NoXFound
import com.sosauce.cutemusic.presentation.shared_components.RoundedCheckbox
import com.sosauce.cutemusic.presentation.shared_components.SafMusicListItem
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.TrackSort
import com.sosauce.cutemusic.utils.addOrRemove
import com.sosauce.cutemusic.utils.copyMutate
import com.sosauce.cutemusic.utils.ordered
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.showCuteSearchbar
import java.io.File

// https://medium.com/@gregkorossy/hacking-lazylist-in-android-jetpack-compose-38afacb3df67
@Composable
fun SharedTransitionScope.MainScreen(
    musics: List<MediaItem>,
    musicState: MusicState,
    currentScreen: NavKey,
    onNavigate: (Screen) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onHandleMediaItemAction: (MediaItemActions) -> Unit
) {


    val textFieldState = rememberTextFieldState()
    val state = rememberLazyListState()
    var hiddenFolders by rememberHiddenFolders()
    var isSortedByASC by rememberSaveable { mutableStateOf(true) }
    var groupByFolders by rememberGroupByFolders()
    var trackSort by rememberTrackSort()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = state,
                contentPadding = paddingValues
            ) {
                if (groupByFolders) {
                    val categories = musics
                        .groupBy { it.mediaMetadata.extras?.getString("folder") ?: "" }
                        .toSortedMap()
                        .map {
                            Category(
                                name = it.key,
                                tracks = it.value
                            )
                        }


                    categories.fastForEach { category ->
                        item {
                            val iconRotation by animateFloatAsState(
                                targetValue = if (category.name in hiddenFolders) 90f else 0f
                            )

                            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 5.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            hiddenFolders =
                                                hiddenFolders.copyMutate { addOrRemove(category.name) }
                                        }
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
                                        text = File(category.name).name,
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
                        if (category.name !in hiddenFolders) {
                            items(
                                items = category.tracks,
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
                                        onShortClick = {
                                            onHandlePlayerAction(
                                                PlayerActions.StartPlayback(
                                                    music.mediaId
                                                )
                                            )
                                        },
                                        music = music,
                                        musicState = musicState,
                                        onNavigate = { onNavigate(it) },
                                        onLoadMetadata = onLoadMetadata,
                                        onHandleMediaItemAction = onHandleMediaItemAction,
                                        onHandlePlayerActions = onHandlePlayerAction
                                    )
                                }
                            }
                        }
                    }
                } else {
                    if (musics.isEmpty()) {
                        item {
                            NoXFound(
                                headlineText = R.string.no_musics_found,
                                bodyText = R.string.no_music_desc,
                                icon = R.drawable.music_note_rounded
                            )
                        }
                    } else {

                        val orderedMusics = musics.ordered(
                            sort = TrackSort.entries[trackSort],
                            ascending = isSortedByASC,
                            query = textFieldState.text.toString()
                        )

                        if (orderedMusics.isEmpty()) {
                            item { NoResult() }
                        } else {
                            items(
                                items = orderedMusics,
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
                                            onShortClick = {
                                                onHandlePlayerAction(
                                                    PlayerActions.StartPlayback(
                                                        music.mediaId
                                                    )
                                                )
                                            },
                                            music = music,
                                            musicState = musicState,
                                            onNavigate = { onNavigate(it) },
                                            onLoadMetadata = onLoadMetadata,
                                            onHandleMediaItemAction = onHandleMediaItemAction,
                                            onHandlePlayerActions = onHandlePlayerAction
                                        )
                                    } else {
                                        var safTracks by rememberAllSafTracks()

                                        SafMusicListItem(
                                            onShortClick = {
                                                onHandlePlayerAction(
                                                    PlayerActions.StartPlayback(
                                                        music.mediaId
                                                    )
                                                )
                                            },
                                            music = music,
                                            currentMusicUri = musicState.uri,
                                            isPlayerReady = musicState.isPlayerReady,
                                            onDeleteFromSaf = {
                                                safTracks = safTracks.copyMutate {
                                                    remove(
                                                        music.mediaMetadata.extras?.getString("uri")
                                                    )
                                                }
                                            }
                                        )
                                    }
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
                    textFieldState = textFieldState,
                    currentScreen = currentScreen,
                    musicState = musicState,
                    showSearchField = !state.isScrollInProgress,
                    sortingMenu = {
                        SortingDropdownMenu(
                            isSortedByASC = isSortedByASC,
                            onChangeSorting = { isSortedByASC = it },
                            sortingOptions = {
                                CuteDropdownMenuItem(
                                    onClick = { groupByFolders = !groupByFolders },
                                    text = { CuteText(stringResource(R.string.group_tracks)) },
                                    leadingIcon = {
                                        RoundedCheckbox(
                                            checked = groupByFolders,
                                            onCheckedChange = null
                                        )
                                    }
                                )
                                CuteDropdownMenuItem(
                                    onClick = { trackSort = 0 },
                                    text = { CuteText(stringResource(R.string.title)) },
                                    leadingIcon = {
                                        RadioButton(
                                            selected = trackSort == 0,
                                            onClick = null
                                        )
                                    }
                                )
                                CuteDropdownMenuItem(
                                    onClick = { trackSort = 1 },
                                    text = { CuteText(stringResource(R.string.artist)) },
                                    leadingIcon = {
                                        RadioButton(
                                            selected = trackSort == 1,
                                            onClick = null
                                        )
                                    }
                                )
                                CuteDropdownMenuItem(
                                    onClick = { trackSort = 2 },
                                    text = { CuteText(stringResource(R.string.album)) },
                                    leadingIcon = {
                                        RadioButton(
                                            selected = trackSort == 2,
                                            onClick = null
                                        )
                                    }
                                )

                                CuteDropdownMenuItem(
                                    onClick = { trackSort = 3 },
                                    text = { CuteText(stringResource(R.string.year)) },
                                    leadingIcon = {
                                        RadioButton(
                                            selected = trackSort == 3,
                                            onClick = null
                                        )
                                    }
                                )

                                CuteDropdownMenuItem(
                                    onClick = { trackSort = 4 },
                                    text = { CuteText(stringResource(R.string.date_modified)) },
                                    leadingIcon = {
                                        RadioButton(
                                            selected = trackSort == 4,
                                            onClick = null
                                        )
                                    }
                                )
                            }
                        )
                    },
                    onHandlePlayerActions = onHandlePlayerAction,
                    onNavigate = onNavigate,
                    fab = {
                        SmallFloatingActionButton(
                            onClick = { onHandlePlayerAction(PlayerActions.PlayRandom) },
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.FAB),
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shuffle,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}

private data class Category(
    val name: String,
    val tracks: List<MediaItem>
)




