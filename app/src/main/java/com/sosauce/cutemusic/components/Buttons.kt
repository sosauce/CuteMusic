package com.sosauce.cutemusic.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.sosauce.cutemusic.logic.rememberIsLoopEnabled
import com.sosauce.cutemusic.logic.rememberIsShuffleEnabled

@Composable
fun LoopButton() {
    var shouldLoop by rememberIsLoopEnabled()

    IconButton(
        onClick = { shouldLoop = !shouldLoop }
    ) {
        Icon(
            imageVector = Icons.Outlined.Loop,
            contentDescription = "loop button",
            tint = if (shouldLoop) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ShuffleButton() {
    var shouldShuffle by rememberIsShuffleEnabled()

    IconButton(
        onClick = { shouldShuffle = !shouldShuffle }
    ) {
        Icon(
            imageVector = Icons.Outlined.Shuffle,
            contentDescription = "shuffle button",
            tint = if (shouldShuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}