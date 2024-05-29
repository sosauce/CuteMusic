package com.sosauce.cutemusic.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.components.AboutCard
import com.sosauce.cutemusic.components.SwipeSwitch
import com.sosauce.cutemusic.components.ThemeManagement
import com.sosauce.cutemusic.logic.AppBar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    onPopBackStack: () -> Unit,
    onNavigate: () -> Unit
) {

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.settings),
                showBackArrow = true,
                showMenuIcon = false,
                onPopBackStack = { onPopBackStack() },
                onNavigate = { onNavigate() }
            )
        },
    ) { values ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AboutCard()
            ThemeManagement()
            SwipeSwitch()
        }
    }
}
