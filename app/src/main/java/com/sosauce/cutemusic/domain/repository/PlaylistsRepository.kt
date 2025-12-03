@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.cutemusic.domain.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.compose.ui.util.fastMap
import androidx.core.net.toUri
import com.sosauce.cutemusic.data.AbstractTracksScanner
import com.sosauce.cutemusic.data.datastore.getMinTrackDuration
import com.sosauce.cutemusic.data.datastore.getWhitelistedFolders
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.utils.observe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

class PlaylistsRepository(
    private val abstractTracksScanner: AbstractTracksScanner
) {
    fun fetchLatestPlaylistTracks(mediaIds: List<String>) = abstractTracksScanner.fetchLatestTracks(
        extraSelection = "${MediaStore.Audio.Media._ID} IN (${mediaIds.joinToString(",") { "?" }})",
        extraSelectionArgs = mediaIds.fastMap { it }.toTypedArray()
    )
}
