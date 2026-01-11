@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.cutemusic.domain.repository

import android.provider.MediaStore
import com.sosauce.cutemusic.data.AbstractTracksScanner
import com.sosauce.cutemusic.data.models.CuteTrack
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

class PlaylistsRepository(
    private val abstractTracksScanner: AbstractTracksScanner
) {
    fun fetchLatestPlaylistTracks(mediaIds: List<String>): Flow<List<CuteTrack>> {
        val selection = "${MediaStore.Audio.Media._ID} IN (${mediaIds.joinToString(",") { "?" }})"
        println("🔍 Querying for IDs: $mediaIds")
        println("🔍 Selection String: $selection")

        return abstractTracksScanner.fetchLatestTracks(
            extraSelection = selection,
            extraSelectionArgs = mediaIds.toTypedArray()
        )
    }
}
