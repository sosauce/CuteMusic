package com.sosauce.cutemusic.data.datastore

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun <T> rememberPreference(
    key: Preferences.Key<T>,
    defaultValue: T,
): MutableState<T> {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val state by remember {
        context.dataStore.data
            .map { it[key] ?: defaultValue }
    }.collectAsStateWithLifecycle(defaultValue)

    return remember(state) {
        object : MutableState<T> {
            override var value: T
                get() = state
                set(value) {
                    coroutineScope.launch {
                        context.dataStore.edit {
                            it[key] = value
                        }
                    }
                }

            override fun component1() = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

fun <T> getPreference(
    key: Preferences.Key<T>,
    defaultValue: T,
    context: Context
): Flow<T> =
    context.dataStore.data
        .map { preference ->
            preference[key] ?: defaultValue
        }

suspend inline fun <reified T> saveCustomPreference(
    value: T,
    key: Preferences.Key<String>,
    context: Context
) {
    val json = Json.encodeToString(value)
    context.dataStore.edit { prefs ->
        prefs[key] = json
    }
}

inline fun <reified T> getCustomPreference(
    key: Preferences.Key<String>,
    defaultValue: T,
    context: Context
): Flow<T> {
    return context.dataStore.data.map { preferences ->
        val jsonString = preferences[key]

        jsonString?.let { string ->
            Json.decodeFromString<T>(string)
        } ?: defaultValue

    }.distinctUntilChanged()
}


@Composable
fun rememberIsLandscape(): Boolean {
    val config = LocalConfiguration.current

    return remember(config.orientation) {
        config.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}

