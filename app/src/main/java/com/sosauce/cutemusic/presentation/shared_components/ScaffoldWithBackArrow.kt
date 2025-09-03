package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.datastore.rememberShowBackButton

@Composable
fun ScaffoldWithBackArrow(
    backArrowVisible: Boolean,
    onNavigateUp: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val showBackButton by rememberShowBackButton()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { pv ->
        Box(Modifier.fillMaxSize()) {
            content(pv)
            AnimatedVisibility(
                visible = backArrowVisible,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .align(Alignment.BottomStart),
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                if (showBackButton) {
                    CuteNavigationButton(Modifier.navigationBarsPadding()) { onNavigateUp() }
                }
            }
        }
    }
}