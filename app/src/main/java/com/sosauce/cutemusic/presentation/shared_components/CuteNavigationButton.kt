@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.utils.selfAlignHorizontally

@Composable
fun CuteNavigationButton(onNavigateUp: () -> Unit) {
    FloatingActionButton(
        onClick = onNavigateUp,
        modifier = Modifier
            .selfAlignHorizontally(Alignment.Start)
            .navigationBarsPadding()
            .padding(start = 15.dp),
        shape = MaterialShapes.Cookie9Sided.toShape(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
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


