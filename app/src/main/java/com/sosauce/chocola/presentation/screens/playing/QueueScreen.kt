@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalUuidApi::class)

package com.sosauce.chocola.presentation.screens.playing

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.sosauce.chocola.R
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.shared_components.CuteListItemDefaults
import com.sosauce.chocola.presentation.shared_components.CuteNavigationButton
import com.sosauce.chocola.presentation.shared_components.MusicListItem
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
            itemsIndexed(
                items = musicState.loadedMedias,
                key = { index, track -> "${track.mediaId}_$index"  }
            ) { index, track ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = "${track.mediaId}_$index"
                ) { isDragging ->
                    val scale by animateFloatAsState(
                        targetValue = if (isDragging) 1.01f else 1f
                    )
                    MusicListItem(
                        modifier = Modifier.scale(scale),
                        track = track,
                        shape = CuteListItemDefaults.getItemShape(index, musicState.loadedMedias.lastIndex),
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
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
                        trailingContent = {
                            IconButton(
                                onClick = { onHandlePlayerAction(PlayerActions.RemoveFromQueue(track)) },
                                shapes = IconButtonDefaults.shapes()
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = null
                                )
                            }
                            IconButton(
                                onClick = {},
                                shapes = IconButtonDefaults.shapes(),
                                modifier = Modifier.draggableHandle()
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.drag_handle),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }

            }
        }

    }

}