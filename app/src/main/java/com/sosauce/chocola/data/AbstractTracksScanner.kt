@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.utils.observe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

/**
 * An abstract way of containing function related to scanning tracks, so any part of the app that needs to fetch tracks can use the same scanning rules
 */
class AbstractTracksScanner(
    private val context: Context,
    private val userPreferences: UserPreferences
) {
    fun fetchLatestTracks(
        extraSelection: String?,
        extraSelectionArgs: Array<String>?,
        onlyHiddenTracks: Boolean = false
    ): Flow<List<CuteTrack>> {



        val mediaStoreFlow = context.contentResolver.observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        val hiddenTracksFlow = userPreferences.getHiddenTracks()
        val whitelistedFoldersFlow = userPreferences.getWhitelistedFolders()
        val minTrackDurationFlow = userPreferences.getMinTrackDuration()

        println("hey uhm, $mediaStoreFlow, $hiddenTracksFlow, $whitelistedFoldersFlow, ${minTrackDurationFlow}")

        return combine(
            mediaStoreFlow,
            hiddenTracksFlow,
            whitelistedFoldersFlow,
            minTrackDurationFlow
        ) { _, hiddenTracks, whitelistedFolders, minTrackDuration ->
            fetchTracks(
                extraSelection = extraSelection,
                extraSelectionArgs = extraSelectionArgs,
                whitelistedFolders = whitelistedFolders,
                minTrackDuration = minTrackDuration,
                hiddenTracks = hiddenTracks,
                onlyHiddenTracks = onlyHiddenTracks
            )
        }.flowOn(Dispatchers.IO)
    }

    private fun fetchTracks(
        extraSelection: String?,
        extraSelectionArgs: Array<String>?,
        whitelistedFolders: Set<String>,
        minTrackDuration: Int,
        hiddenTracks: Set<String>,
        onlyHiddenTracks: Boolean
    ): List<CuteTrack> {
        val musics = mutableListOf<CuteTrack>()


        if (whitelistedFolders.isEmpty() || (onlyHiddenTracks && hiddenTracks.isEmpty())) return emptyList()

        val selection = buildString {
            append("${MediaStore.Audio.Media.DURATION} >= ? AND ")
            append("${MediaStore.Audio.Media.IS_MUSIC} != ? AND ")
            append("(")
            append(whitelistedFolders.joinToString(" OR ") { "${MediaStore.Audio.Media.DATA} LIKE ?" })
            append(")")
            extraSelection?.let {
                append(" AND ")
                append(it)
            }
            if (hiddenTracks.isNotEmpty()) {
                val placeholders = hiddenTracks.joinToString(",") { "?" }
                val onlyHidden = if (onlyHiddenTracks) "IN" else "NOT IN"
                append(" AND ${MediaStore.Audio.Media._ID} $onlyHidden ($placeholders)")
            }
        }
        val selectionArgs = mutableListOf<String>().apply {
            add("${minTrackDuration * 1000}")
            add("0")
            addAll(whitelistedFolders.map { "$it/%" })
            extraSelectionArgs?.let {
                addAll(it)
            }
            if (hiddenTracks.isNotEmpty()) {
                addAll(hiddenTracks)
            }
        }.toTypedArray()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.YEAR
        )


        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackNbColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

            while (cursor.moveToNext()) {


                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val artistId = cursor.getLong(artistIdColumn)
                val album = cursor.getString(albumColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val filePath = cursor.getString(folderColumn)
                val folder = filePath.substringBeforeLast('/')
                val size = cursor.getLong(sizeColumn)
                val duration = cursor.getLong(durationColumn)
                val trackNumber = cursor.getInt(trackNbColumn)
                val dateModified = cursor.getLong(dateModifiedColumn)
                val year = cursor.getInt(yearColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val artUri = "$uri/albumart".toUri()
                val mediaId = id.toString()

                musics.add(
                    CuteTrack(
                        mediaId = mediaId,
                        uri = uri,
                        artUri = artUri,
                        title = title,
                        artist = artist,
                        album = album,
                        albumId = albumId,
                        artistId = artistId,
                        durationMs = duration,
                        trackNumber = trackNumber,
                        year = year,
                        size = size,
                        folder = folder,
                        path = filePath,
                        isSaf = false,
                        dateModified = dateModified,
                        mediaItem = MediaItem.Builder().setUri(uri).setMediaId(mediaId).build()
                    )
                )
            }

        }
        return musics
    }

}