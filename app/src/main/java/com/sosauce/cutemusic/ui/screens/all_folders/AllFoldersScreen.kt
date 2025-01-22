@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.all_folders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.blacklisted.FolderItem
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.ScreenSelection
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding

@Composable
fun SharedTransitionScope.AllFoldersScreen(
    musics: List<MediaItem>,
    onNavigationItemClicked: (Int, NavigationItem) -> Unit,
    selectedIndex: Int,
    onNavigate: (Screen) -> Unit,
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    isPlayerReady: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    var query by remember { mutableStateOf("") }
    val groupedMusics = remember(musics) {
        musics.groupBy {
            it.mediaMetadata.extras?.getString("path")
                ?.substring(0, it.mediaMetadata.extras?.getString("path")?.lastIndexOf('/') ?: 0)
        }
    }
    val groupedAndFilteredMusics = remember(query, musics) {
        if (query.isEmpty()) {
            groupedMusics
        } else groupedMusics.filter { it.key?.contains(query, true) != false }

    }
    var areMusicsVisible = remember { mutableStateMapOf<String, Boolean>() }
    var screenSelectionExpanded by remember { mutableStateOf(false) }
    var isSortedByASC by remember { mutableStateOf(true) } // I prolly should change this
    val float by animateFloatAsState(
        targetValue = if (isSortedByASC) 45f else 135f,
        label = "Arrow Icon Animation"
    )

    Box(Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {

            groupedAndFilteredMusics.onEachIndexed { index, (folder, musics) ->
                item(
                    key = folder
                ) {
                    val isExpanded = areMusicsVisible[folder] == true

                    val rotation by animateFloatAsState(
                        targetValue = if (isExpanded) 270f else 180f,
                        label = "Arrow Rotation"
                    )
                    val bottomDp by animateDpAsState(
                        targetValue = if (index == groupedAndFilteredMusics.keys.size - 1 || isExpanded) 24.dp else 4.dp,
                        label = ""
                    )
                    val topDp by animateDpAsState(
                        targetValue = if (index == 0 || isExpanded) 24.dp else 4.dp,
                        label = ""
                    )

                    FolderItem(
                        folder = folder ?: " No name",
                        topDp = topDp,
                        bottomDp = bottomDp,
                        modifier = Modifier.animateItem(),
                        actionButton = {
                            IconButton(
                                onClick = {
                                    areMusicsVisible[folder ?: "No name"] = !isExpanded
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBackIosNew,
                                    contentDescription = null,
                                    modifier = Modifier.rotate(rotation)
                                )
                            }
                        }
                    )
                }

                items(
                    items = musics,
                    key = { it.mediaId }
                ) { music ->
                    AnimatedVisibility(areMusicsVisible[folder] == true) {
                        Column(
                            modifier = Modifier
                                .animateItem()
                                .padding(
                                    start = 20.dp
                                )
                        ) {
                            MusicListItem(
                                onShortClick = {
                                    onHandlePlayerActions(
                                        PlayerActions.StartPlayback(
                                            it
                                        )
                                    )
                                },
                                music = music,
                                currentMusicUri = "",
                                showBottomSheet = false,
                                isPlayerReady = true,
                            )
                        }
                    }
                }

            }
        }

        CuteSearchbar(
            query = query,
            onQueryChange = { query = it },
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth(rememberSearchbarMaxFloatValue())
                .padding(
                    bottom = 5.dp,
                    end = rememberSearchbarRightPadding()
                )
                .align(rememberSearchbarAlignment()),
            placeholder = {
                CuteText(
                    text = stringResource(id = R.string.search) + " " + stringResource(
                        id = R.string.folders
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),

                    )
            },
            leadingIcon = {
                IconButton(
                    onClick = {
                        screenSelectionExpanded = true
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.folder_rounded),
                        contentDescription = null
                    )
                }


                DropdownMenu(
                    expanded = screenSelectionExpanded,
                    onDismissRequest = { screenSelectionExpanded = false },
                    modifier = Modifier
                        .width(180.dp)
                        .background(color = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    ScreenSelection(
                        onNavigationItemClicked = onNavigationItemClicked,
                        selectedIndex = selectedIndex
                    )
                }
            },
            trailingIcon = {
                Row {
                    IconButton(
                        onClick = {
                            isSortedByASC = !isSortedByASC
                            when (isSortedByASC) {
                                true -> { /* sort by ASC */
                                }

                                false -> { /* sort by DESC */
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowUpward,
                            contentDescription = null,
                            modifier = Modifier.rotate(float)
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
                }
            },
            currentlyPlaying = currentlyPlaying,
            onHandlePlayerActions = onHandlePlayerActions,
            isPlaying = isCurrentlyPlaying,
            animatedVisibilityScope = animatedVisibilityScope,
            isPlayerReady = isPlayerReady,
            onNavigate = { onNavigate(Screen.NowPlaying) },
            onClickFAB = { onHandlePlayerActions(PlayerActions.PlayRandom) }
        )
    }


}