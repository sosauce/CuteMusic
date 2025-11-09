@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.cutemusic.domain.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.core.net.toUri
import com.sosauce.cutemusic.data.datastore.getBlacklistedFolder
import com.sosauce.cutemusic.data.datastore.getMinTrackDuration
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.utils.observe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

class PlaylistsRepository(
    private val context: Context
) {

    fun fetchLatestPlaylistTracks(mediaIds: List<String>): Flow<List<CuteTrack>> =
        context.contentResolver.observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapLatest { fetchPlaylistTracks(mediaIds) }
            .flowOn(Dispatchers.IO)

    private suspend fun fetchPlaylistTracks(mediaIds: List<String>): List<CuteTrack> {

        if (mediaIds.isEmpty()) return emptyList()

        val musics = mutableListOf<CuteTrack>()


        val blacklistedFolders = getBlacklistedFolder(context)
        val minTrackDuration = getMinTrackDuration(context).first()
        val selection = buildString {
            append("${MediaStore.Audio.Media._ID} IN (${mediaIds.joinToString(",") { "?" }})")
            append(" AND ${MediaStore.Audio.Media.DURATION} >= ?")
            append(" AND ${MediaStore.Audio.Media.IS_MUSIC} != ?")
            if (blacklistedFolders.isNotEmpty()) {
                append(" AND ")
                append(blacklistedFolders.joinToString(" AND ") { "${MediaStore.Audio.Media.DATA} NOT LIKE ?" })
            }
        }
        val selectionArgs = mutableListOf<String>().apply {
            addAll(mediaIds.map { it })
            add("${minTrackDuration * 1000}")
            add("0")
            if (blacklistedFolders.isNotEmpty()) {
                addAll(blacklistedFolders.map { "$it%" })
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
            null
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
                        dateModified = dateModified
                    )
                )
            }

        }

        return musics
    }

}
