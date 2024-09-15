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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberFollowSys
import com.sosauce.cutemusic.data.datastore.rememberUseAmoledMode
import com.sosauce.cutemusic.data.datastore.rememberUseDarkMode
import com.sosauce.cutemusic.data.datastore.rememberUseSystemFont
import com.sosauce.cutemusic.ui.customs.restart
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun Misc(
    onNavigateTo: (Screen) -> Unit
) {
    val context = LocalContext.current
    var useSystemFont by rememberUseSystemFont()

    Column {
        CuteText(
            text = stringResource(id = R.string.misc),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )
        TextSettingsCards(
            text = stringResource(id = R.string.blacklisted_folders),
            onClick = { onNavigateTo(Screen.Blacklisted) },
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
            checked = useSystemFont,
            onCheckedChange = { useSystemFont = !useSystemFont },
            topDp = 4.dp,
            bottomDp = 4.dp,
            text = stringResource(id = R.string.use_sys_font)
        )
        TextSettingsCards(
            text = stringResource(id = R.string.restart_app),
            tipText = stringResource(id = R.string.restart_app_why),
            onClick = { context.restart() },
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            topDp = 4.dp,
            bottomDp = 24.dp
        )
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
            targetState = !followSys, label = "",
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
