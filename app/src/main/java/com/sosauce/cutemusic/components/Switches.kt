package com.sosauce.cutemusic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sosauce.cutemusic.logic.dataStore
import com.sosauce.cutemusic.logic.getAmoledModeSetting
import com.sosauce.cutemusic.logic.getDarkModeSetting
import com.sosauce.cutemusic.logic.getSwipeSetting
import com.sosauce.cutemusic.logic.saveAmoledModeSetting
import com.sosauce.cutemusic.logic.saveDarkModeSetting
import com.sosauce.cutemusic.logic.saveSwipeSetting
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.coroutines.launch

@Composable
fun SwipeSwitch() {
    val context = LocalContext.current
    val dataStore: DataStore<Preferences> = context.dataStore
    val swipeSetting by getSwipeSetting(dataStore).collectAsState(initial = true)
    val coroutineScope = rememberCoroutineScope()
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showAboutDialog) {
        AboutDialog(
            title = "Swipe Gestures",
            contentText = "When enabled, the now playing screen will be swipeable.\n\n-Top to Bottom to exit the screen\n-Right to Left to play next music\n-Left to Right to play previous song",
            onDismiss = { showAboutDialog = false }
        )

    }

    Column {
        Text(text = "Misc", fontFamily = GlobalFont, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp))

        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Use swipe gestures",
                        fontFamily = GlobalFont
                    )
                    IconButton(
                        onClick = { showAboutDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info Button"
                        )
                    }
                }
                Switch(
                    checked = swipeSetting,
                    onCheckedChange = { isChecked ->
                        // Update the setting when Switch state changes
                        coroutineScope.launch {
                            saveSwipeSetting(dataStore, isChecked)
                        }
                    }
                )
            }
        }
    }


}

@Composable
fun ThemeManagement() {
    val context = LocalContext.current
    val dataStore: DataStore<Preferences> = context.dataStore
    val darkMode by getDarkModeSetting(dataStore).collectAsState(initial = false)
    val amoled by getAmoledModeSetting(dataStore).collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    Column {
        Text(text = "Theme", fontFamily = GlobalFont, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp))
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Dark Mode",
                    fontFamily = GlobalFont
                )
                Switch(
                    checked = darkMode,
                    onCheckedChange = { isChecked ->
                        // Update the setting when Switch state changes
                        coroutineScope.launch {
                            saveDarkModeSetting(dataStore, isChecked)

                        }
                    }
                )
            }
        }
    }
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 4.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Amoled Mode",
                fontFamily = GlobalFont
            )
            Switch(
                checked = amoled,
                onCheckedChange = { isChecked ->
                    // Update the setting when Switch state changes
                    coroutineScope.launch {
                        saveAmoledModeSetting(dataStore, isChecked)
                    }
                }
            )
        }
    }
}