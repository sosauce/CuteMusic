package com.sosauce.cutemusic.data.datastore

import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.APPLY_LOOP
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.APPLY_SHUFFLE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.BLACKLISTED_FOLDERS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.CAROUSEL
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.GROUP_BY_FOLDERS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.HAS_SEEN_TIP
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.MEDIA_INDEX_TO_MEDIA_ID
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.NP_ART_SHAPE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.NUMBER_OF_ALBUM_GRIDS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.PITCH
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SAF_TRACKS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SHOW_BACK_BUTTON
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SHOW_SHUFFLE_BUTTON
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SHOW_X_BUTTON
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SLIDER_STYLE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SNAP_SPEED_N_PITCH
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SPEED
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.THEME
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.THUMBLESS_SLIDER
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_ART_THEME
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_SYSTEM_FONT
import com.sosauce.cutemusic.utils.CuteTheme
import com.sosauce.cutemusic.utils.LastPlayed
import com.sosauce.cutemusic.utils.SliderStyle
import kotlinx.coroutines.flow.first

private const val PREFERENCES_NAME = "settings"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCES_NAME)

data object PreferencesKeys {
    val THEME = stringPreferencesKey("theme")
    val USE_SYSTEM_FONT = booleanPreferencesKey("use_sys_font")
    val BLACKLISTED_FOLDERS = stringSetPreferencesKey("blacklisted_folders")
    val HAS_SEEN_TIP = booleanPreferencesKey("has_seen_tip")
    val SNAP_SPEED_N_PITCH = booleanPreferencesKey("snap_peed_n_pitch")
    val KILL_SERVICE = booleanPreferencesKey("kill_service")
    val USE_ART_THEME = booleanPreferencesKey("use_art_theme")
    val APPLY_LOOP = booleanPreferencesKey("apply_loop")
    val APPLY_SHUFFLE = booleanPreferencesKey("apply_shuffle")
    val SHOW_X_BUTTON = booleanPreferencesKey("show_x_button")
    val SHOW_SHUFFLE_BUTTON = booleanPreferencesKey("show_shuffle_button")
    val SHOW_BACK_BUTTON = booleanPreferencesKey("show_back_button")
    val SAF_TRACKS = stringSetPreferencesKey("saf_tracks")
    val GROUP_BY_FOLDERS = booleanPreferencesKey("GROUP_BY_FOLDERS")
    val CAROUSEL = booleanPreferencesKey("CAROUSEL")
    val SPEED = floatPreferencesKey("SPEED")
    val PITCH = floatPreferencesKey("PITCH")
    val MEDIA_INDEX_TO_MEDIA_ID = stringPreferencesKey("MEDIA_INDEX_TO_MEDIA_ID")
    val NUMBER_OF_ALBUM_GRIDS = intPreferencesKey("NUMBER_OF_ALBUM_GRIDS")
    val NP_ART_SHAPE = stringPreferencesKey("NP_ART_SHAPE")
    val SLIDER_STYLE = stringPreferencesKey("SLIDER_STYLE")
    val THUMBLESS_SLIDER = booleanPreferencesKey("THUMBLESS_SLIDER")
}


@Composable
fun rememberAppTheme() =
    rememberPreference(key = THEME, defaultValue = CuteTheme.SYSTEM)

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

@Composable
fun rememberShouldApplyLoop() =
    rememberPreference(key = APPLY_LOOP, defaultValue = false)

@Composable
fun rememberShouldApplyShuffle() =
    rememberPreference(key = APPLY_SHUFFLE, defaultValue = false)

@Composable
fun rememberShowXButton() =
    rememberPreference(key = SHOW_X_BUTTON, defaultValue = true)

@Composable
fun rememberShowShuffleButton() =
    rememberPreference(key = SHOW_SHUFFLE_BUTTON, defaultValue = true)

@Composable
fun rememberShowBackButton() =
    rememberPreference(key = SHOW_BACK_BUTTON, defaultValue = true)

@Composable
fun rememberAllSafTracks() =
    rememberPreference(key = SAF_TRACKS, defaultValue = emptySet())

@Composable
fun rememberGroupByFolders() =
    rememberPreference(key = GROUP_BY_FOLDERS, defaultValue = false)

@Composable
fun rememberCarousel() =
    rememberPreference(key = CAROUSEL, defaultValue = false)

@Composable
fun rememberSpeed() =
    rememberPreference(key = SPEED, defaultValue = 1.0f)

@Composable
fun rememberPitch() =
    rememberPreference(key = PITCH, defaultValue = 1.0f)

@Composable
fun rememberAlbumGrids() =
    rememberPreference(key = NUMBER_OF_ALBUM_GRIDS, defaultValue = 2)

@Composable
fun rememberNpArtShape() =
    rememberPreference(key = NP_ART_SHAPE, defaultValue = RoundedCornerShape(5).toString())

@Composable
fun rememberSliderStyle() =
    rememberPreference(key = SLIDER_STYLE, defaultValue = SliderStyle.WAVY)

@Composable
fun rememberThumblessSlider() =
    rememberPreference(key = THUMBLESS_SLIDER, defaultValue = false)

fun getShouldLoop(context: Context) =
    getPreference(key = APPLY_LOOP, defaultValue = false, context = context)

fun getShouldShuffle(context: Context) =
    getPreference(key = APPLY_SHUFFLE, defaultValue = false, context = context)


fun getSpeed(context: Context) =
    getPreference(key = SPEED, defaultValue = 1.0f, context = context)

fun getPitch(context: Context) =
    getPreference(key = PITCH, defaultValue = 1.0f, context = context)

fun getSafTracks(context: Context) =
    getPreference(key = SAF_TRACKS, defaultValue = emptySet(), context = context)

suspend fun saveMediaIndexToMediaIdMap(pair: LastPlayed, context: Context) =
    saveCustomPreference(value = pair, key = MEDIA_INDEX_TO_MEDIA_ID, context = context)

fun getMediaIndexToMediaIdMap(context: Context) =
    getCustomPreference(
        key = MEDIA_INDEX_TO_MEDIA_ID,
        defaultValue = LastPlayed("", 0L),
        context = context
    )


suspend fun getBlacklistedFolder(context: Context): Set<String> {
    val preferences = context.dataStore.data.first()
    return preferences[BLACKLISTED_FOLDERS] ?: emptySet()
}

