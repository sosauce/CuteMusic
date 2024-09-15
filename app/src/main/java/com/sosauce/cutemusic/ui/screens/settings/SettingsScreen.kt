package com.sosauce.cutemusic.ui.screens.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.settings.compenents.AboutCard
import com.sosauce.cutemusic.ui.screens.settings.compenents.Misc
import com.sosauce.cutemusic.ui.screens.settings.compenents.ThemeManagement
import com.sosauce.cutemusic.ui.shared_components.AppBar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    onPopBackStack: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.settings),
                showBackArrow = true,
                showMenuIcon = false,
                onPopBackStack = { onPopBackStack() },
                onNavigate = { onNavigate(it) }
            )
        },
        modifier = Modifier

    ) { values ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AboutCard()
            ThemeManagement()
            Misc(
                onNavigateTo = { onNavigate(it) }
            )
        }
    }
}
