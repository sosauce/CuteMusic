package com.sosauce.cutemusic.logic

import android.content.Context
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
    val THEME = stringPreferencesKey("theme")
    val SWIPE_GESTURES = booleanPreferencesKey("swipe_gestures")
    val REPEAT_MODE_ONE = booleanPreferencesKey("repeat_mode_one")
}

suspend fun saveTheme(dataStore: DataStore<Preferences>, theme: String) {
    dataStore.edit { settings ->
        settings[PreferencesKeys.THEME] = theme
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