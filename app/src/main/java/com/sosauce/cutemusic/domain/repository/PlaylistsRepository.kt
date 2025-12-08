@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.cutemusic.domain.repository

import android.provider.MediaStore
import androidx.compose.ui.util.fastMap
import com.sosauce.cutemusic.data.AbstractTracksScanner
import kotlinx.coroutines.ExperimentalCoroutinesApi

class PlaylistsRepository(
    private val abstractTracksScanner: AbstractTracksScanner
) {
    fun fetchLatestPlaylistTracks(mediaIds: List<String>) = abstractTracksScanner.fetchLatestTracks(
        extraSelection = "${MediaStore.Audio.Media._ID} IN (${mediaIds.joinToString(",") { "?" }})",
        extraSelectionArgs = mediaIds.fastMap { it }.toTypedArray()
    )
}
