package com.sosauce.cutemusic.data.datastore

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
    }.collectAsStateWithLifecycle(initialValue = defaultValue)

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

fun <T> rememberNonComposablePreference(
    key: Preferences.Key<T>,
    defaultValue: T,
    context: Context
): MutableState<T> {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    var state by mutableStateOf(defaultValue)

    coroutineScope.launch {
        context.dataStore.data
            .map { preferences -> preferences[key] ?: defaultValue }
            .collect { newValue ->
                state = newValue
            }
    }

    return object : MutableState<T> {
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

@Composable
fun rememberIsLandscape(): Boolean {
    val config = LocalConfiguration.current

    return remember(config.orientation) {
        config.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}

