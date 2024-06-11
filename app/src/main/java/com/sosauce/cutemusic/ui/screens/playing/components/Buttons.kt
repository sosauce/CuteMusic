package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import com.sosauce.cutemusic.data.datastore.rememberIsLoopEnabled
import com.sosauce.cutemusic.data.datastore.rememberIsShuffleEnabled

@Composable
fun LoopButton(
    onClick: (Boolean) -> Unit
) {
    var shouldLoop by rememberIsLoopEnabled()


    IconButton(
        onClick = {
            onClick(shouldLoop)
            shouldLoop = !shouldLoop
        }
    ) {
        Icon(
            imageVector = Icons.Outlined.Loop,
            contentDescription = "loop button",
            tint = if (!shouldLoop) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ShuffleButton(
    onClick: (Boolean) -> Unit
) {
    var shouldShuffle by rememberIsShuffleEnabled()

    IconButton(
        onClick = {
            onClick(shouldShuffle)
            shouldShuffle = !shouldShuffle
        }
    ) {
        Icon(
            imageVector = Icons.Outlined.Shuffle,
            contentDescription = "shuffle button",
            tint = if (shouldShuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}