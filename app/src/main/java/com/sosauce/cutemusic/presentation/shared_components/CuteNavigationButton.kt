@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CuteNavigationButton(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit
) {
    FloatingActionButton(
        onClick = onNavigateUp,
        modifier = modifier,
        shape = MaterialShapes.Cookie9Sided.toShape(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,

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
    FloatingActionButton(
        onClick = action,
        shape = MaterialShapes.Cookie9Sided.toShape(),
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
        )
    }
}


