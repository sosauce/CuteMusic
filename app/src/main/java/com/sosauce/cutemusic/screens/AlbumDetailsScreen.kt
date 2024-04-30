package com.sosauce.cutemusic.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.sosauce.cutemusic.logic.AppBar

@Composable
fun AlbumDetailsScreen(
    navController: NavController
) {
    AlbumDetailsScreenContent(
        navController = navController,
        title = ""
    )
}

@Composable
private fun AlbumDetailsScreenContent(
    navController: NavController,
    title: String
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = title,
                    showBackArrow = false,
                    showMenuIcon = false,
                    navController = navController,
                    showSortIcon = false,
                    viewModel = null,
                    musics = null
                )
            }
        ) { values ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().background(Color.Red)) {
                Text(text = "")
            }
        }
    }
}