package com.sosauce.cutemusic.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavController
import com.sosauce.cutemusic.components.SettingBottomSheet
import com.sosauce.cutemusic.components.SwipeSwitch
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.logic.PreferencesKeys
import com.sosauce.cutemusic.logic.dataStore
import com.sosauce.cutemusic.logic.saveTheme
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(navController: NavController) {

    val title = remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }

    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false }
        ) {
                SettingBottomSheet(title = title.value) { ThemeRadioButtons() }
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Settings",
                showBackArrow = true,
                navController = navController,
                showMenuIcon = false,
                showSearchIcon = false,
                onSearchBarStateChanged = null
            )
        },
    ) { values ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        title.value = "Themes"
                        isSheetOpen = true
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Theme",
                    fontFamily = GlobalFont,
                    modifier = Modifier.padding(16.dp)
                )
            }
            SwipeSwitch()
        }
    }
}

@Composable
fun ThemeRadioButtons() {
    val options = listOf("Dark", "Light", "Amoled")
    val context = LocalContext.current
    val dataStore: DataStore<Preferences> = context.dataStore
    val themeFlow: Flow<String?> =
        dataStore.data.map { preferences -> preferences[PreferencesKeys.THEME] }
    val theme by themeFlow.collectAsState(initial = null)

    Column(
        verticalArrangement = Arrangement.Center
    ) {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = theme == option,
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                saveTheme(dataStore, option)
                            }
                        }
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = theme == option,
                    onClick = null
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 16.dp),
                    fontFamily = GlobalFont
                )
            }
        }
    }
}