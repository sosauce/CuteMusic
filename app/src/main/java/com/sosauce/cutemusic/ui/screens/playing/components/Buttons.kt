package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun LoopButton(
    onClick: (Boolean) -> Unit
) {
    var shouldLoop by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            shouldLoop = !shouldLoop
            onClick(shouldLoop)
        }
    ) {
        Icon(
            imageVector = Icons.Rounded.Loop,
            contentDescription = "loop button",
            tint = if (shouldLoop) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ShuffleButton(
    onClick: (Boolean) -> Unit
) {
    var shouldShuffle by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            shouldShuffle = !shouldShuffle
            onClick(shouldShuffle)
        }
    ) {
        Icon(
            imageVector = Icons.Rounded.Shuffle,
            contentDescription = "shuffle button",
            tint = if (shouldShuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}