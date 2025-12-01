@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.sosauce.cutemusic.R

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
            painter = painterResource(R.drawable.back),
            contentDescription = null
        )
    }

}

@Composable
fun CuteActionButton(
    modifier: Modifier = Modifier,
    icon: Int = R.drawable.shuffle,
    action: () -> Unit
) {
    FloatingActionButton(
        onClick = action,
        shape = MaterialShapes.Cookie9Sided.toShape(),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null
        )
    }
}


