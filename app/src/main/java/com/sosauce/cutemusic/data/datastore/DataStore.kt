package com.sosauce.cutemusic.data.datastore

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.FOLLOW_SYS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SORT_ORDER
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_AMOLED_MODE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_DARK_MODE

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data object PreferencesKeys {
    val SORT_ORDER = booleanPreferencesKey("sort_order")
    val USE_DARK_MODE = booleanPreferencesKey("use_dark_mode")
    val USE_AMOLED_MODE = booleanPreferencesKey("use_amoled_mode")
    val FOLLOW_SYS = booleanPreferencesKey("follow_sys")
}

@Composable
fun rememberSortASC() =
    rememberPreference(key = SORT_ORDER, defaultValue = true)

@Composable
fun rememberUseDarkMode() =
    rememberPreference(key = USE_DARK_MODE, defaultValue = false)

@Composable
fun rememberUseAmoledMode() =
    rememberPreference(key = USE_AMOLED_MODE, defaultValue = false)

@Composable
fun rememberFollowSys() =
    rememberPreference(key = FOLLOW_SYS, defaultValue = true)

