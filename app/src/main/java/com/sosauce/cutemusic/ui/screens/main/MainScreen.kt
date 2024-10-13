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
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.material.icons.rounded.ArrowUpward
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberHasSeenTip
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.components.BottomSheetContent
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.ScreenSelection
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.SortingType
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding
import com.sosauce.cutemusic.utils.thenIf

@Composable
fun SharedTransitionScope.MainScreen(
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
    onDeleteMusic: (List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit,
    onHandleSorting: (SortingType) -> Unit,
    onHandleSearching: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val state = rememberLazyListState()
    var screenSelectionExpanded by remember { mutableStateOf(false) }
    var isSortedByASC by remember { mutableStateOf(true) } // I prolly should change this
    val float by animateFloatAsState(
        targetValue = if (isSortedByASC) 45f else 135f,
        label = "Arrow Icon Animation"
    )



    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = state
        ) {
            if (musics.isEmpty()) {
                item {
                    CuteText(
                        text = stringResource(id = R.string.no_musics_found),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                        )
                }
            } else {
                itemsIndexed(
                    items = musics,
                    key = { _, music -> music.mediaId }
                ) { index, music ->
                    Column(
                        modifier = Modifier
                            .animateItem()
                            .padding(
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
                            ),
                            onDeleteMusic = onDeleteMusic
                        )
                    }
                }
            }
        }


        Crossfade(
            targetState = state.canScrollForward || musics.size <= 15,
            label = "",
            modifier = Modifier.align(rememberSearchbarAlignment())
        ) { visible ->
            if (visible) {
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
                    onQueryChange = {
                        query = it
                        onHandleSearching(query)
                    },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth(rememberSearchbarMaxFloatValue())
                        .padding(
                            bottom = 5.dp,
                            end = rememberSearchbarRightPadding()
                        )
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
                            IconButton(
                                onClick = {
                                    isSortedByASC = !isSortedByASC
                                    when(isSortedByASC) {
                                        true -> { onHandleSorting(SortingType.ASCENDING) }
                                        false -> { onHandleSorting(SortingType.DESCENDING) }
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
                                onClick = { onNavigateTo(Screen.Settings) }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Settings,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    currentlyPlaying = currentlyPlaying,
                    onHandlePlayerActions = onHandlePlayerAction,
                    isPlaying = isCurrentlyPlaying,
                    animatedVisibilityScope = animatedVisibilityScope,
                    isPlaylistEmpty = isPlaylistEmpty,
                    onNavigate = { onNavigateTo(Screen.NowPlaying) }
                )
            }
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
    showBottomSheet: Boolean = false,
    onDeleteMusic: ((List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit)? = null
) {

    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    var isSheetOpen by remember { mutableStateOf(false) }
    val isPlaying = currentMusicUri == music.mediaMetadata.extras?.getString("uri")
    val bgColor by animateColorAsState(
        targetValue = if (isPlaying) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.background
        },
        label = "Background Color",
        animationSpec = tween(500)
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
                onDeleteMusic = onDeleteMusic!!
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
                .background(
                    color = bgColor,
                    shape = RoundedCornerShape(24.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
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
                IconButton(
                    onClick = { isSheetOpen = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = null
                    )
                }
            }
        }
}



