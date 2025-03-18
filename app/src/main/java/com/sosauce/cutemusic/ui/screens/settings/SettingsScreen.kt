package com.sosauce.cutemusic.ui.screens.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.settings.compenents.AboutCard
import com.sosauce.cutemusic.ui.screens.settings.compenents.Misc
import com.sosauce.cutemusic.ui.screens.settings.compenents.ThemeManagement
import com.sosauce.cutemusic.ui.screens.settings.compenents.UISettings
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { pv ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = pv
            ) {
                item {
                    AboutCard()
                    Spacer(Modifier.height(10.dp))
                    ThemeManagement()
                    UISettings()
                    Misc(onNavigate = onNavigate)
                }
            }
            CuteNavigationButton(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
            ) { onNavigateUp() }
        }
    }
}
