package com.sosauce.cutemusic.ui.screens.settings.compenents

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberFollowSys
import com.sosauce.cutemusic.data.datastore.rememberShowAlbumsTab
import com.sosauce.cutemusic.data.datastore.rememberShowArtistsTab
import com.sosauce.cutemusic.data.datastore.rememberShowFoldersTab
import com.sosauce.cutemusic.data.datastore.rememberShowXButton
import com.sosauce.cutemusic.data.datastore.rememberUseAmoledMode
import com.sosauce.cutemusic.data.datastore.rememberUseClassicSlider
import com.sosauce.cutemusic.data.datastore.rememberUseDarkMode
import com.sosauce.cutemusic.data.datastore.rememberUseSystemFont
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun Misc(
    onNavigate: (Screen) -> Unit
) {
    //var killService by remember { rememberKillService(context) }
    val context = LocalContext.current

    Column {
        CuteText(
            text = stringResource(id = R.string.misc),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )
        TextSettingsCards(
            text = stringResource(id = R.string.blacklisted_folders),
            onClick = { onNavigate(Screen.Blacklisted) },
            modifier = Modifier
                .padding(
                    top = 25.dp,
                    start = 15.dp,
                    bottom = 25.dp
                )
                .fillMaxWidth(),
            topDp = 24.dp,
            bottomDp = 24.dp
        )
//        TextSettingsCards(
//            text = stringResource(id = R.string.saf_manager),
//            onClick = { onNavigate(Screen.Saf) },
//            modifier = Modifier
//                .padding(
//                    top = 25.dp,
//                    start = 15.dp,
//                    bottom = 25.dp
//                )
//                .fillMaxWidth(),
//            topDp = 4.dp,
//            bottomDp = 24.dp
//        )
//        SettingsCards(
//            checked = killService,
//            onCheckedChange = { killService = !killService },
//            topDp = 4.dp,
//            bottomDp = 4.dp,
//            text = "Kill Service"
//        )

    }
}

@Composable
fun ThemeManagement() {
    var darkMode by rememberUseDarkMode()
    var amoledMode by rememberUseAmoledMode()
    var followSys by rememberFollowSys()

    Column {
        CuteText(
            text = stringResource(id = R.string.theme),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )
        SettingsCards(
            checked = followSys,
            onCheckedChange = { followSys = !followSys },
            topDp = 24.dp,
            bottomDp = 4.dp,
            text = stringResource(id = R.string.follow_sys)
        )
        AnimatedContent(
            targetState = !followSys,
            label = "",
            transitionSpec = {
                (slideInHorizontally() + fadeIn()).togetherWith(slideOutHorizontally() + fadeOut())
            }
        ) { isVisible ->
            if (isVisible) {
                SettingsCards(
                    checked = darkMode,
                    onCheckedChange = { darkMode = !darkMode },
                    topDp = 4.dp,
                    bottomDp = 4.dp,
                    text = stringResource(id = R.string.dark_mode)
                )
            }

        }
        SettingsCards(
            checked = amoledMode,
            onCheckedChange = { amoledMode = !amoledMode },
            topDp = 4.dp,
            bottomDp = 24.dp,
            text = stringResource(id = R.string.amoled_mode)
        )
    }
}

@Composable
fun UISettings() {
    var useClassicSlider by rememberUseClassicSlider()
    var useSystemFont by rememberUseSystemFont()
    var showXButton by rememberShowXButton()
    var showAlbumsTab by rememberShowAlbumsTab()
    var showArtistsTab by rememberShowArtistsTab()
    var showFoldersTab by rememberShowFoldersTab()

    var showTabManager by remember { mutableStateOf(false) }

    if (showTabManager) {
        TabManager(
            onDismissRequest = { showTabManager = false },
            showArtistsTab = showArtistsTab,
            showAlbumsTab = showAlbumsTab,
            showFoldersTab = showFoldersTab
        )
    }

    Column {
        CuteText(
            text = stringResource(id = R.string.UI),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )
        TextSettingsCards(
            text = stringResource(id = R.string.manage_shown_tabs),
            onClick = { showTabManager = true },
            modifier = Modifier
                .padding(
                    top = 25.dp,
                    start = 15.dp,
                    bottom = 25.dp
                )
                .fillMaxWidth(),
            topDp = 24.dp,
            bottomDp = 4.dp
        )
        SettingsCards(
            checked = useClassicSlider,
            onCheckedChange = { useClassicSlider = !useClassicSlider },
            topDp = 4.dp,
            bottomDp = 4.dp,
            text = stringResource(id = R.string.classic_slider),
        )
//        SettingsCards(
//            checked = useArtTheme,
//            onCheckedChange = { useArtTheme = !useArtTheme },
//            topDp = 4.dp,
//            bottomDp = 4.dp,
//            text = stringResource(id = R.string.use_art),
//            optionalDescription = {
//                CuteText(
//                    text = "CuteSearchbar will have the current artwork as it's background.",
//                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
//                    fontSize = 12.sp
//
//                )
//            }
//        )
        SettingsCards(
            checked = showXButton,
            onCheckedChange = { showXButton = !showXButton },
            topDp = 4.dp,
            bottomDp = 4.dp,
            text = stringResource(id = R.string.show_close_button)
        )
        SettingsCards(
            checked = useSystemFont,
            onCheckedChange = { useSystemFont = !useSystemFont },
            topDp = 4.dp,
            bottomDp = 24.dp,
            text = stringResource(id = R.string.use_sys_font)
        )
    }
}
