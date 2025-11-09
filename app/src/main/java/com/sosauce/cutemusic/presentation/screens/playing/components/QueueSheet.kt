@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.shared_components.QueueMusicListItem
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun QueueSheet(
    onDismissRequest: () -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    musicState: MusicState
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onHandlePlayerAction(
            PlayerActions.ReArrangeQueue(from.index, to.index)
        )
    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(true),
    ) {
        LazyColumn(state = lazyListState) {
            items(
                items = musicState.loadedMedias,
                key = { it.mediaId }
            ) { music ->
                ReorderableItem(reorderableLazyListState, key = music.mediaId) { isDragging ->
                    val scale by animateFloatAsState(
                        targetValue = if (isDragging) 1.05f else 1f
                    )
                    QueueMusicListItem(
                        modifier = Modifier.scale(scale),
                        music = music,
                        currentMusicUri = musicState.uri,
                        onHandlePlayerActions = onHandlePlayerAction
                    )
                }
            }
        }
    }
}