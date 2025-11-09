package com.sosauce.cutemusic.presentation.screens.setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.presentation.screens.setup.components.SetupBottomBar
import com.sosauce.cutemusic.presentation.screens.setup.components.SetupHeader

@Composable
fun SetupScreen() {

    val pagerState = rememberPagerState { 2 }
    val animatedProgress by animateFloatAsState((pagerState.settledPage + 0.5f).coerceIn(0f, 1f))

    Scaffold(
        modifier = Modifier.padding(horizontal = 10.dp),
        topBar = {
            SetupHeader(
                progress = { animatedProgress }
            )
        },
        bottomBar = {
            SetupBottomBar(true)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState
            ) { page ->
                AnimatedContent(
                    targetState = page,
                    transitionSpec = { slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut() }
                ) {
                    when (it) {
                        0 -> SetupPermissions()
                        1 -> SetupPermissions()
                    }
                }
            }
        }

    }
}