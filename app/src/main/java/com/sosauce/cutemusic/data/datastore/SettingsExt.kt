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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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


fun <T> rememberPreferenceNonComposable(
    context: Context,
    key: Preferences.Key<T>,
    defaultValue: T,
): MutableState<T> {
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val state = context.dataStore.data
        .map { it[key] ?: defaultValue }
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), defaultValue)

    return object : MutableState<T> {
        override var value: T
            get() = state.value
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

