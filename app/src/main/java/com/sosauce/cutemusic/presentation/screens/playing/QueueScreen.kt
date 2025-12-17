@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playing

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.presentation.shared_components.MusicListItem
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun QueueScreen(
    musicState: MusicState,
    onNavigateUp: () -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit
) {

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onHandlePlayerAction(
            PlayerActions.ReArrangeQueue(from.index, to.index)
        )
    }


    Scaffold(
        bottomBar = {
            CuteNavigationButton(onNavigateUp = onNavigateUp)
        }
    ) { paddingValues ->

        LazyColumn(
            contentPadding = paddingValues,
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = musicState.loadedMedias,
                key = { it.mediaId }
            ) { track ->
                ReorderableItem(reorderableLazyListState, key = track.mediaId) { isDragging ->
                    val scale by animateFloatAsState(
                        targetValue = if (isDragging) 1.05f else 1f
                    )
                    MusicListItem(
                        modifier = Modifier.scale(scale),
                        music = track,
                        musicState = musicState,
                        onNavigate = {},
                        onHandlePlayerActions = onHandlePlayerAction,
                        onShortClick = {
                            onHandlePlayerAction(
                                PlayerActions.Play(
                                    index = musicState.loadedMedias.indexOf(track),
                                    tracks = musicState.loadedMedias
                                )
                            )
                        },
//                        trailingContent = {
//                            IconButton(
//                                onClick = { onHandlePlayerAction(PlayerActions.RemoveFromQueue(track)) },
//                                shapes = IconButtonDefaults.shapes()
//                            ) {
//                                Icon(
//                                    painter = painterResource(R.drawable.close),
//                                    contentDescription = null
//                                )
//                            }
//                            IconButton(
//                                onClick = {},
//                                shapes = IconButtonDefaults.shapes(),
//                                modifier = Modifier.draggableHandle()
//                            ) {
//                                Icon(
//                                    painter = painterResource(R.drawable.drag_handle),
//                                    contentDescription = null
//                                )
//                            }
//                        }
                    )
                }

            }
        }

    }

}