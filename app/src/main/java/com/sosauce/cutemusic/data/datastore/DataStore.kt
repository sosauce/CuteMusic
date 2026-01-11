package com.sosauce.cutemusic.data.datastore

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.ALBUM_SORT
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.ARTIST_SORT
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.ARTWORK_SHAPE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.ART_AS_BACKGROUND
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.CAROUSEL
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.GROUP_BY_FOLDERS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.HAS_BEEN_THROUGH_SETUP
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.HAS_SEEN_TIP
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.HIDDEN_FOLDERS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.MIN_TRACK_DURATION
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.NUMBER_OF_ALBUM_GRIDS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.PAUSE_ON_MUTE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.PLAYLIST_SORT
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SAF_TRACKS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SHOW_SHUFFLE_BUTTON
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SLIDER_STYLE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SNAP_SPEED_N_PITCH
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SORT_TRACKS_ASCENDING
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.THEME
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.THUMBLESS_SLIDER
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.TRACK_SORT
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_ART_THEME
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_EXPRESSIVE_PALETTE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.USE_SYSTEM_FONT
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.WHITELISTED_FOLDERS
import com.sosauce.cutemusic.utils.ArtworkShape
import com.sosauce.cutemusic.utils.CuteTheme
import com.sosauce.cutemusic.utils.SliderStyle

private const val PREFERENCES_NAME = "settings"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCES_NAME)

data object PreferencesKeys {
    val THEME = stringPreferencesKey("theme")
    val USE_SYSTEM_FONT = booleanPreferencesKey("use_sys_font")
    val WHITELISTED_FOLDERS = stringSetPreferencesKey("WHITELISTED_FOLDERS")
    val HAS_SEEN_TIP = booleanPreferencesKey("has_seen_tip")
    val SNAP_SPEED_N_PITCH = booleanPreferencesKey("snap_peed_n_pitch")
    val KILL_SERVICE = booleanPreferencesKey("kill_service")
    val USE_ART_THEME = booleanPreferencesKey("use_art_theme")
    val SHOW_X_BUTTON = booleanPreferencesKey("show_x_button")
    val SHOW_SHUFFLE_BUTTON = booleanPreferencesKey("show_shuffle_button")
    val SAF_TRACKS = stringSetPreferencesKey("saf_tracks")
    val GROUP_BY_FOLDERS = booleanPreferencesKey("GROUP_BY_FOLDERS")
    val CAROUSEL = booleanPreferencesKey("CAROUSEL")
    val MEDIA_INDEX_TO_MEDIA_ID = stringPreferencesKey("MEDIA_INDEX_TO_MEDIA_ID")
    val NUMBER_OF_ALBUM_GRIDS = intPreferencesKey("NUMBER_OF_ALBUM_GRIDS")
    val SLIDER_STYLE = stringPreferencesKey("SLIDER_STYLE")
    val THUMBLESS_SLIDER = booleanPreferencesKey("THUMBLESS_SLIDER")
    val HIDDEN_FOLDERS = stringSetPreferencesKey("HIDDEN_FOLDERS")
    val ART_AS_BACKGROUND = booleanPreferencesKey("ART_AS_BACKGROUND")
    val ALBUM_SORT = intPreferencesKey("ALBUM_SORT")
    val TRACK_SORT = intPreferencesKey("TRACK_SORT")
    val ARTIST_SORT = intPreferencesKey("ARTIST_SORT")
    val PAUSE_ON_MUTE = booleanPreferencesKey("PAUSE_ON_MUTE")
    val USE_EXPRESSIVE_PALETTE = booleanPreferencesKey("USE_EXPRESSIVE_PALETTE")
    val MIN_TRACK_DURATION = intPreferencesKey("MIN_TRACK_DURATION")
    val PLAYLIST_SORT = intPreferencesKey("PLAYLIST_SORT")
    val ARTWORK_SHAPE = stringPreferencesKey("ARTWORK_SHAPE")
    val HAS_BEEN_THROUGH_SETUP = booleanPreferencesKey("HAS_BEEN_THROUGH_SETUP")
    val SORT_TRACKS_ASCENDING = booleanPreferencesKey("SORT_TRACKS_ASCENDING")
    val LAST_MUSIC_STATE = stringPreferencesKey("LAST_MUSIC_STATE")
}


@Composable
fun rememberAppTheme() =
    rememberPreference(key = THEME, defaultValue = CuteTheme.SYSTEM)

@Composable
fun rememberUseSystemFont() =
    rememberPreference(key = USE_SYSTEM_FONT, defaultValue = false)

@Composable
fun rememberSnapSpeedAndPitch() =
    rememberPreference(key = SNAP_SPEED_N_PITCH, defaultValue = false)

@Composable
fun rememberUseArtTheme() =
    rememberPreference(key = USE_ART_THEME, defaultValue = false)

@Composable
fun rememberShowShuffleButton() =
    rememberPreference(key = SHOW_SHUFFLE_BUTTON, defaultValue = true)

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
fun rememberAlbumGrids() =
    rememberPreference(key = NUMBER_OF_ALBUM_GRIDS, defaultValue = 2)

@Composable
fun rememberArtworkShape() =
    rememberPreference(key = ARTWORK_SHAPE, defaultValue = ArtworkShape.CLASSIC)

@Composable
fun rememberSliderStyle() =
    rememberPreference(key = SLIDER_STYLE, defaultValue = SliderStyle.WAVY)

@Composable
fun rememberThumblessSlider() =
    rememberPreference(key = THUMBLESS_SLIDER, defaultValue = false)

@Composable
fun rememberHiddenFolders() =
    rememberPreference(key = HIDDEN_FOLDERS, defaultValue = emptySet())

@Composable
fun rememberUseArtAsBackground() =
    rememberPreference(key = ART_AS_BACKGROUND, defaultValue = false)

@Composable
fun rememberAlbumSort() =
    rememberPreference(key = ALBUM_SORT, defaultValue = 0)

@Composable
fun rememberTrackSort() =
    rememberPreference(key = TRACK_SORT, defaultValue = 0)

@Composable
fun rememberPlaylistSort() =
    rememberPreference(key = PLAYLIST_SORT, defaultValue = 0)

@Composable
fun rememberArtistSort() =
    rememberPreference(key = ARTIST_SORT, defaultValue = 0)

@Composable
fun rememberPauseOnMute() =
    rememberPreference(key = PAUSE_ON_MUTE, defaultValue = false)

@Composable
fun rememberUseExpressivePalette() =
    rememberPreference(key = USE_EXPRESSIVE_PALETTE, defaultValue = false)

@Composable
fun rememberMinTrackDuration() =
    rememberPreference(key = MIN_TRACK_DURATION, defaultValue = 0)

@Composable
fun rememberWhitelistedFolders() =
    rememberPreference(key = WHITELISTED_FOLDERS, defaultValue = emptySet())

@Composable
fun rememberHasBeenThroughSetup() =
    rememberPreference(key = HAS_BEEN_THROUGH_SETUP, defaultValue = false)

@Composable
fun rememberHasSeenTip() =
    rememberPreference(key = HAS_SEEN_TIP, defaultValue = false)

@Composable
fun rememberSortTracksAscending() =
    rememberPreference(key = SORT_TRACKS_ASCENDING, defaultValue = true)

suspend fun getPauseOnMute(context: Context) =
    getPreference(key = PAUSE_ON_MUTE, defaultValue = false, context = context)

fun getSafTracks(context: Context) =
    getPreferenceFlow(key = SAF_TRACKS, defaultValue = emptySet(), context = context)


suspend fun getMinTrackDuration(context: Context) =
    getPreference(key = MIN_TRACK_DURATION, defaultValue = 0, context = context)

suspend fun getWhitelistedFolders(context: Context) =
    getPreference(key = WHITELISTED_FOLDERS, defaultValue = emptySet(), context = context)


//suspend fun saveMediaIndexToMediaIdMap(pair: LastPlayed, context: Context) =
//    saveCustomPreference(value = pair, key = MEDIA_INDEX_TO_MEDIA_ID, context = context)

//suspend fun getMediaIndexToMediaIdMap(context: Context) =
//    getCustomPreference(
//        key = MEDIA_INDEX_TO_MEDIA_ID,
//        defaultValue = LastPlayed("", 0L),
//        context = context
//    )



