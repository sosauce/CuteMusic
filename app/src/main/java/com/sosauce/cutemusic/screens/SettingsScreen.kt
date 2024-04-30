package com.sosauce.cutemusic.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.sosauce.cutemusic.components.AboutCard
import com.sosauce.cutemusic.components.SwipeSwitch
import com.sosauce.cutemusic.components.ThemeManagement
import com.sosauce.cutemusic.logic.AppBar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(navController: NavController) {

    Scaffold(
        topBar = {
            AppBar(
                title = "Settings",
                showBackArrow = true,
                navController = navController,
                showMenuIcon = false,
                showSortIcon = false,
                viewModel = null,
                musics = null
            )
        },
    ) { values ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            AboutCard()
            ThemeManagement()
            SwipeSwitch()
        }
    }
}
