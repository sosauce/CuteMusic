package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistQueueSheet(
    viewModel: MusicViewModel,
    onDismiss: () -> Unit,
    controller: MediaController
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val queueItems by remember(controller) { mutableStateOf(controller.currentTimeline.queue) }

    
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = null
    ) {
        LazyColumn {
            items(queueItems) {  queue ->
                val item = queue.second
                QueueSong(item = queue.second)
            }
        }
    }

}

@Composable
private fun QueueSong(item: MediaItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.mediaMetadata.artworkUri,
            contentDescription = "artwork"
        )
        
        Column {
            Text(
                text = item.mediaMetadata.title.toString(),
                fontFamily = GlobalFont
            )
            Text(
                text = item.mediaMetadata.artist.toString(),
                fontFamily = GlobalFont
            )
        }
    }
}

inline val Timeline.queue: List<Pair<Int, MediaItem>>
    get() = List(windowCount) {
        it to getWindow(it, Timeline.Window()).mediaItem
    }
