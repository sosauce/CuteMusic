@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.sosauce.cutemusic.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import com.sosauce.cutemusic.data.datastore.rememberHasSeenTip
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.datastore.rememberSortASC
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.components.BottomSheetContent
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.ScreenSelection
import com.sosauce.cutemusic.ui.shared_components.SortRadioButtons
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.thenIf

@Composable
fun SharedTransitionScope.MainScreen(
    navController: NavController,
    musics: List<MediaItem>,
    viewModel: MusicViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onLoadMetadata: ((String) -> Unit)? = null,
) {

    if (rememberIsLandscape()) {
        MainScreenLandscape(
            musics = musics,
            selectedIndex = viewModel.selectedItem,
            onNavigateTo = { navController.navigate(it) },
            currentlyPlaying = viewModel.currentlyPlaying,
            isCurrentlyPlaying = viewModel.isCurrentlyPlaying,
            onNavigationItemClicked = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            },
            viewModel = viewModel,
            animatedVisibilityScope = animatedVisibilityScope,
            onShortClick = {
                viewModel.itemClicked(it, musics)
            }
        )
    } else {
        MainScreenContent(
            musics = musics,
            selectedIndex = viewModel.selectedItem,
            onNavigateTo = { navController.navigate(it) },
            currentlyPlaying = viewModel.currentlyPlaying,
            isCurrentlyPlaying = viewModel.isCurrentlyPlaying,
            onShortClick = {
                viewModel.itemClicked(it, musics)
            },
            onNavigationItemClicked = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            },
            animatedVisibilityScope = animatedVisibilityScope,
            onLoadMetadata = onLoadMetadata,
            isPlaylistEmpty = viewModel.isPlaylistEmptyAndDataNotNull(),
            currentMusicUri = viewModel.currentMusicUri,
            onHandlePlayerAction = { viewModel.handlePlayerActions(it) },

            )
    }
}


@Composable
private fun SharedTransitionScope.MainScreenContent(
    musics: List<MediaItem>,
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
    onNavigateTo: (Screen) -> Unit,
    onShortClick: (String) -> Unit,
    onNavigationItemClicked: (Int, NavigationItem) -> Unit,
    selectedIndex: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onLoadMetadata: ((String) -> Unit)? = null,
    isPlaylistEmpty: Boolean,
    currentMusicUri: String,
    onHandlePlayerAction: (PlayerActions) -> Unit,
) {
    var sort by rememberSortASC()
    var query by remember { mutableStateOf("") }
    var sortExpanded by remember { mutableStateOf(false) }
    val state = rememberLazyListState()
    var screenSelectionExpanded by remember { mutableStateOf(false) }
    val displayMusics by remember(sort, musics, query) {
        derivedStateOf {
            if (query.isNotEmpty()) {
                musics.filter {
                    it.mediaMetadata.title?.contains(
                        other = query,
                        ignoreCase = true
                    ) == true
                }
            } else {
                if (sort) musics
                else musics.sortedByDescending { it.mediaMetadata.title.toString() }
            }

        }
    }
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = state
        ) {
            if (displayMusics.isEmpty()) {
                item {
                    CuteText(
                        text = stringResource(id = R.string.no_musics_found),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,

                        )
                }
            } else {
                itemsIndexed(
                    items = displayMusics,
                    key = { _, music -> music.mediaId }
                ) { index, music ->
                    Column(
                        modifier = Modifier.padding(
                            vertical = 2.dp,
                            horizontal = 4.dp
                        )
                    ) {
                        MusicListItem(
                            onShortClick = { onShortClick(music.mediaId) },
                            music = music,
                            onNavigate = { onNavigateTo(it) },
                            currentMusicUri = currentMusicUri,
                            onLoadMetadata = onLoadMetadata,
                            showBottomSheet = true,
                            modifier = Modifier.thenIf(
                                index == 0,
                                Modifier.statusBarsPadding()
                            )
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = state.canScrollForward || displayMusics.size <= 15,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val transition = rememberInfiniteTransition(label = "Infinite Color Change")
            val color by transition.animateColor(
                initialValue = LocalContentColor.current,
                targetValue = MaterialTheme.colorScheme.errorContainer,
                animationSpec = infiniteRepeatable(
                    tween(500),
                    repeatMode = RepeatMode.Reverse
                ),
                label = ""
            )
            var hasSeenTip by rememberHasSeenTip()

            CuteSearchbar(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth(0.85f)
                    .padding(bottom = 5.dp)
                    .sharedElement(
                        state = rememberSharedContentState(key = "searchbar"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 500)
                        }
                    ),
                placeholder = {
                    CuteText(
                        text = stringResource(id = R.string.search) + " " + stringResource(id = R.string.music),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),

                        )
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            screenSelectionExpanded = true
                            // Let's prevent writing to datastore everytime the user clicks ;)
                            if (!hasSeenTip) {
                                hasSeenTip = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = if (!hasSeenTip) color else LocalContentColor.current
                        )
                    }


                    DropdownMenu(
                        expanded = screenSelectionExpanded,
                        onDismissRequest = { screenSelectionExpanded = false },
                        modifier = Modifier
                            .width(180.dp)
                            .background(color = MaterialTheme.colorScheme.surface)
                    ) {
                        ScreenSelection(
                            onNavigationItemClicked = onNavigationItemClicked,
                            selectedIndex = selectedIndex
                        )
                    }
                },
                trailingIcon = {
                    Row {
                        IconButton(onClick = { sortExpanded = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Sort,
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = { onNavigateTo(Screen.Settings) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = null
                            )
                        }
                        DropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false },
                            modifier = Modifier
                                .width(180.dp)
                                .background(color = MaterialTheme.colorScheme.surface)
                        ) {
                            SortRadioButtons(
                                sort = sort,
                                onChangeSort = { sort = !sort }
                            )
                        }
                    }
                },
                currentlyPlaying = currentlyPlaying,
                onHandlePlayerActions = { onHandlePlayerAction(it) },
                isPlaying = isCurrentlyPlaying,
                animatedVisibilityScope = animatedVisibilityScope,
                isPlaylistEmpty = isPlaylistEmpty,
                onNavigate = { onNavigateTo(Screen.NowPlaying) }
            )
        }
    }
}


@Composable
fun MusicListItem(
    modifier: Modifier = Modifier,
    music: MediaItem,
    onShortClick: (String) -> Unit,
    onNavigate: (Screen) -> Unit = {},
    currentMusicUri: String,
    onLoadMetadata: ((String) -> Unit)? = null,
    showBottomSheet: Boolean = false
) {

    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    var isSheetOpen by remember { mutableStateOf(false) }
    val isPlaying = currentMusicUri == music.mediaMetadata.extras?.getString("uri")
    val bgColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.surfaceContainer, label = "Background Color"
    )

    if (isSheetOpen) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false },
        ) {
            BottomSheetContent(
                music = music,
                onNavigate = { onNavigate(it) },
                onDismiss = { isSheetOpen = false },
                onLoadMetadata = onLoadMetadata,
            )
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .combinedClickable(
                onClick = { onShortClick(music.mediaId) }
            )
            .then(
                if (isPlaying) {
                    Modifier.background(
                        color = bgColor,
                        shape = RoundedCornerShape(24.dp)
                    )
                } else {
                    Modifier
                }
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
                stringResource(R.string.artwork),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(45.dp),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                CuteText(
                    text = music.mediaMetadata.title.toString(),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
                CuteText(
                    text = music.mediaMetadata.artist.toString(),

                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                )
            }
        }

        if (showBottomSheet) {
            IconButton(onClick = { isSheetOpen = true }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }
        }
    }
}


