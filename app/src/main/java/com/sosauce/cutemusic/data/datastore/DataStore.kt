package com.sosauce.cutemusic.data.datastore

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.APPLY_LOOP
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.BLACKLISTED_FOLDERS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.FOLLOW_SYS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.HAS_SEEN_TIP
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SHOW_X_BUTTON
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SNAP_SPEED_N_PITCH
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SORT_ORDER
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SORT_ORDER_ALBUMS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SORT_ORDER_ARTISTS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_AMOLED_MODE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_ART_THEME
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_CLASSIC_SLIDER
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_DARK_MODE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_SYSTEM_FONT

private const val PREFERENCES_NAME = "settings"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCES_NAME)

private data object PreferencesKeys {
    val SORT_ORDER = booleanPreferencesKey("sort_order")
    val SORT_ORDER_ARTISTS = booleanPreferencesKey("sort_order_artists")
    val SORT_ORDER_ALBUMS = booleanPreferencesKey("sort_order_albums")
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
}

@Composable
fun rememberSortASC() =
    rememberPreference(key = SORT_ORDER, defaultValue = true)

@Composable
fun rememberSortASCArtists() =
    rememberPreference(key = SORT_ORDER_ARTISTS, defaultValue = true)

@Composable
fun rememberSortASCAlbums() =
    rememberPreference(key = SORT_ORDER_ALBUMS, defaultValue = true)

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
