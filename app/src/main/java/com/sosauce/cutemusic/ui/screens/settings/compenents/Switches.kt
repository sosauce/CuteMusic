package com.sosauce.cutemusic.ui.screens.settings.compenents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.sosauce.cutemusic.ui.customs.restart
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun SwipeSwitch() {
    val context = LocalContext.current
//    var showAboutDialog by remember { mutableStateOf(false) }

//    if (showAboutDialog) {
//        AboutDialog(
//            title = stringResource(id = R.string.swipe_gestures),
//            contentText = stringResource(id = R.string.about_swipe_gestures),
//            onDismiss = { showAboutDialog = false }
//        )
//
//    }
    Column {
        Text(
            text = stringResource(id = R.string.misc),
            fontFamily = GlobalFont,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )
        TextSettingsCards(
            text = stringResource(id = R.string.restart_app),
            tipText = stringResource(id = R.string.restart_app_why),
            onClick = { context.restart() }
        )
    }
}

@Composable
fun ThemeManagement() {
    var darkMode by rememberUseDarkMode()
    var amoledMode by rememberUseAmoledMode()
    var followSys by rememberFollowSys()

    Column {
        Text(
            text = stringResource(id = R.string.theme),
            fontFamily = GlobalFont,
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
        AnimatedVisibility(
            visible = !followSys,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut()
        ) {
            SettingsCards(
                checked = darkMode,
                onCheckedChange = { darkMode = !darkMode },
                topDp = 4.dp,
                bottomDp = 4.dp,
                text = stringResource(id = R.string.dark_mode)
            )
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
