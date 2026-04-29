@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.setup

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.presentation.screens.setup.components.SetupBottomBar
import com.sosauce.chocola.utils.hasMusicPermission
import kotlinx.coroutines.launch

@Composable
fun SetupScreen(
    onNavigateToApp: () -> Unit
) {

    val pagerState = rememberPagerState { 2 }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var hasPermission by remember { mutableStateOf(context.hasMusicPermission()) }

    Scaffold(
        modifier = Modifier
            .padding(horizontal = 10.dp),
        bottomBar = {
            SetupBottomBar(
                hasPermission = hasPermission,
                isLastStep = pagerState.currentPage == pagerState.pageCount - 1,
                onGoToNextPage = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                onNavigateToApp = onNavigateToApp
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(paddingValues),
            userScrollEnabled = hasPermission
        ) { page ->
            when (page) {
                0 -> {
                    SetupPermissions(
                        hasPermission = hasPermission,
                        onUpdateHasPermission = { hasPermission = it }
                    )
                }

                1 -> {
                    SetupFolders()
                }
            }
        }

    }
}