package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CuteNavigationButton(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit
) {
    SmallFloatingActionButton(
        onClick = onNavigateUp,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = null
        )
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