package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CuteNavigationButton(
    modifier: Modifier = Modifier,
    playlistName: (@Composable () -> Unit)? = null,
    onNavigateUp: () -> Unit
) {
    SmallFloatingActionButton(
        onClick = onNavigateUp,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(if (playlistName != null) 5.dp else 0.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null
            )
            playlistName?.invoke()
        }
    }
}

@Composable
fun CuteActionButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = Icons.Rounded.Shuffle,
    action: () -> Unit
) {
    SmallFloatingActionButton(
        onClick = action,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
        )
    }
}