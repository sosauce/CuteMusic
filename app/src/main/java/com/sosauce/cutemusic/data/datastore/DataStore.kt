package com.sosauce.cutemusic.data.datastore

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.APPLY_LOOP
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.BLACKLISTED_FOLDERS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.FOLLOW_SYS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.HAS_SEEN_TIP
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SAF_TRACKS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SHOW_ALBUMS_TAB
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SHOW_ARTISTS_TAB
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SHOW_FOLDERS_TAB
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SHOW_X_BUTTON
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SNAP_SPEED_N_PITCH
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_AMOLED_MODE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_ART_THEME
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_CLASSIC_SLIDER
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_DARK_MODE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_SYSTEM_FONT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val PREFERENCES_NAME = "settings"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCES_NAME)

private data object PreferencesKeys {
    val USE_DARK_MODE = booleanPreferencesKey("use_dark_mode")
    val USE_AMOLED_MODE = booleanPreferencesKey("use_amoled_mode")
    val FOLLOW_SYS = booleanPreferencesKey("follow_sys")
    val USE_SYSTEM_FONT = booleanPreferencesKey("use_sys_font")
    val BLACKLISTED_FOLDERS = stringSetPreferencesKey("blacklisted_folders")
    val HAS_SEEN_TIP = booleanPreferencesKey("has_seen_tip")
    val SNAP_SPEED_N_PITCH = booleanPreferencesKey("snap_peed_n_pitch")
    val KILL_SERVICE = booleanPreferencesKey("kill_service")
    val USE_ART_THEME = booleanPreferencesKey("use_art_theme")
    val APPLY_LOOP = booleanPreferencesKey("apply_loop")
    val USE_CLASSIC_SLIDER = booleanPreferencesKey("use_classic_slider")
    val SHOW_X_BUTTON = booleanPreferencesKey("show_x_button")
    val SHOW_ALBUMS_TAB = booleanPreferencesKey("show_albums_tab")
    val SHOW_ARTISTS_TAB = booleanPreferencesKey("show_artists_tab")
    val SHOW_FOLDERS_TAB = booleanPreferencesKey("show_folders_tab")
    val SAF_TRACKS = stringSetPreferencesKey("saf_tracks")
}

@Composable
fun rememberUseDarkMode() =
    rememberPreference(key = USE_DARK_MODE, defaultValue = false)

@Composable
fun rememberUseAmoledMode() =
    rememberPreference(key = USE_AMOLED_MODE, defaultValue = false)

@Composable
fun rememberFollowSys() =
    rememberPreference(key = FOLLOW_SYS, defaultValue = true)

@Composable
fun rememberUseSystemFont() =
    rememberPreference(key = USE_SYSTEM_FONT, defaultValue = false)

@Composable
fun rememberAllBlacklistedFolders() =
    rememberPreference(key = BLACKLISTED_FOLDERS, defaultValue = emptySet())

@Composable
fun rememberHasSeenTip() =
    rememberPreference(key = HAS_SEEN_TIP, defaultValue = false)

@Composable
fun rememberSnapSpeedAndPitch() =
    rememberPreference(key = SNAP_SPEED_N_PITCH, defaultValue = false)

@Composable
fun rememberUseArtTheme() =
    rememberPreference(key = USE_ART_THEME, defaultValue = false)

//fun rememberKillService(context: Context) =
//    rememberNonComposablePreference(key = KILL_SERVICE, defaultValue = true, context = context)
@Composable
fun rememberShouldApplyLoop() =
    rememberPreference(key = APPLY_LOOP, defaultValue = false)

@Composable
fun rememberUseClassicSlider() =
    rememberPreference(key = USE_CLASSIC_SLIDER, defaultValue = false)

@Composable
fun rememberShowXButton() =
    rememberPreference(key = SHOW_X_BUTTON, defaultValue = true)

@Composable
fun rememberShowAlbumsTab() =
    rememberPreference(key = SHOW_ALBUMS_TAB, defaultValue = true)

@Composable
fun rememberShowArtistsTab() =
    rememberPreference(key = SHOW_ARTISTS_TAB, defaultValue = true)

@Composable
fun rememberShowFoldersTab() =
    rememberPreference(key = SHOW_FOLDERS_TAB, defaultValue = true)

@Composable
fun rememberAllSafTracks() =
    rememberPreference(key = SAF_TRACKS, defaultValue = emptySet())


suspend fun getBlacklistedFolder(context: Context): Set<String> {
    val preferences = context.dataStore.data.first()
    return preferences[BLACKLISTED_FOLDERS] ?: emptySet()
}

fun getSafTracks(context: Context): Flow<Set<String>> =

    context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("CuteError", "getSafTracks: ${exception.message}")
            } else throw exception
        }
        .map { preference ->
            preference[SAF_TRACKS] ?: emptySet()
        }

