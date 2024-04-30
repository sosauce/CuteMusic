package com.sosauce.cutemusic.logic

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PreferencesKeys {
    val SORT_ORDER = stringPreferencesKey("sort_order")
    val USE_DARK_MODE = booleanPreferencesKey("use_dark_mode")
    val REMEMBER_LOOP = booleanPreferencesKey("remember_loop")
    val USE_AMOLED_MODE = booleanPreferencesKey("use_amoled_mode")
    val SWIPE_GESTURES = booleanPreferencesKey("swipe_gestures")
    val REPEAT_MODE_ONE = booleanPreferencesKey("repeat_mode_one")
}

suspend fun saveSort(dataStore: DataStore<Preferences>, sort: String) {
    dataStore.edit { settings ->
        settings[PreferencesKeys.SORT_ORDER] = sort
    }
}

suspend fun saveDarkModeSetting(dataStore: DataStore<Preferences>, enabled: Boolean) {
    dataStore.edit { preferences ->
        preferences[PreferencesKeys.USE_DARK_MODE] = enabled
    }
}

@Composable
fun getDarkModeSetting(dataStore: DataStore<Preferences>): Flow<Boolean> {
    return dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USE_DARK_MODE] ?: false
    }
}

suspend fun saveLoopSetting(dataStore: DataStore<Preferences>, enabled: Boolean) {
    dataStore.edit { preferences ->
        preferences[PreferencesKeys.REMEMBER_LOOP] = enabled
    }
}

fun getLoopSetting(dataStore: DataStore<Preferences>): Flow<Boolean> {
    return dataStore.data.map { preferences ->
        preferences[PreferencesKeys.REMEMBER_LOOP] ?: false
    }
}

suspend fun saveAmoledModeSetting(dataStore: DataStore<Preferences>, enabled: Boolean) {
    dataStore.edit { preferences ->
        preferences[PreferencesKeys.USE_AMOLED_MODE] = enabled
    }
}

fun getAmoledModeSetting(dataStore: DataStore<Preferences>): Flow<Boolean> {
    return dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USE_AMOLED_MODE] ?: false
    }
}

suspend fun saveSwipeSetting(dataStore: DataStore<Preferences>, enabled: Boolean) {
    dataStore.edit { preferences ->
        preferences[PreferencesKeys.SWIPE_GESTURES] = enabled
    }
}

fun getSwipeSetting(dataStore: DataStore<Preferences>): Flow<Boolean> {
    return dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SWIPE_GESTURES] ?: false
    }
}

suspend fun saveRepeat(dataStore: DataStore<Preferences>, enabled: Boolean) {
    dataStore.edit { preferences ->
        preferences[PreferencesKeys.REPEAT_MODE_ONE] = enabled
    }
}

fun getRepeat(dataStore: DataStore<Preferences>): Flow<Boolean> {
    return dataStore.data.map { preferences ->
        preferences[PreferencesKeys.REPEAT_MODE_ONE] ?: false
    }
}