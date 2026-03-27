package com.sosauce.cutemusic.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.HIDDEN_TRACKS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.LAST_MUSIC_STATE
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.MIN_TRACK_DURATION
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.PLAYLIST_SORT
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SAF_TRACKS
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.SORT_TRACKS_ASCENDING
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.TRACK_SORT
import com.sosauce.cutemusic.data.datastore.PreferencesKeys.WHITELISTED_FOLDERS
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.utils.PlaylistSort
import com.sosauce.cutemusic.utils.TrackSort
import com.sosauce.cutemusic.utils.copyMutate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class UserPreferences(
    private val context: Context
) {

    val getTrackSort = context.dataStore.data.map {
        val sort = it[TRACK_SORT] ?: 0
        TrackSort.entries[sort]
    }

    val getSortTracksAscending = context.dataStore.data.map {
        it[SORT_TRACKS_ASCENDING] ?: true
    }

    val getPlaylistSort = context.dataStore.data.map {
        val sort = it[PLAYLIST_SORT] ?: 0
        PlaylistSort.entries[sort]
    }

    fun getHiddenTracks() = context.dataStore.data.map {
        it[HIDDEN_TRACKS] ?: emptySet()
    }

    fun getWhitelistedFolders() = context.dataStore.data.map {
        it[WHITELISTED_FOLDERS] ?: emptySet()
    }

    fun getSafTracks() = context.dataStore.data.map {
        it[SAF_TRACKS] ?: emptySet()
    }

    suspend fun getSavedMusicState() = context.dataStore.data.map {
        val string = it[LAST_MUSIC_STATE] ?: ""
        try {
            Json.decodeFromString<MusicState>(string)
        } catch (e: IllegalArgumentException) {
            MusicState()
        }
    }.first()

    fun getMinTrackDuration() = context.dataStore.data.map {
        it[MIN_TRACK_DURATION] ?: 0
    }

    suspend fun saveSavedMusicState(musicState: MusicState) =
        context.dataStore.edit { it[LAST_MUSIC_STATE] = Json.encodeToString(musicState) }


    suspend fun unhideTrack(mediaId: String) {
        context.dataStore.edit {
            val alreadyHidden = it[HIDDEN_TRACKS] ?: emptySet()
            it[HIDDEN_TRACKS] = alreadyHidden.copyMutate { remove(mediaId) }
        }
    }

}