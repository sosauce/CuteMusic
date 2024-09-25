package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun LoopButton(
    onClick: (Boolean) -> Unit,
    isLooping: Boolean
) {
    IconButton(
        onClick = {
            onClick(!isLooping)
        }
    ) {
        Icon(
            imageVector = Icons.Rounded.Loop,
            contentDescription = "loop button",
            tint = if (isLooping) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ShuffleButton(
    onClick: (Boolean) -> Unit,
    isShuffling: Boolean
) {

    IconButton(
        onClick = {
            onClick(!isShuffling)
        }
    ) {
        Icon(
            imageVector = Icons.Rounded.Shuffle,
            contentDescription = "shuffle button",
            tint = if (isShuffling) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}