@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.cutemusic.domain.repository

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.sosauce.cutemusic.data.datastore.getMinTrackDuration
import com.sosauce.cutemusic.data.datastore.getWhitelistedFolders
import com.sosauce.cutemusic.utils.observe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

@SuppressLint("UnsafeOptInUsageError")
class MediaStoreHelper(
    private val context: Context
) {


    fun fetchLatestMusics(): Flow<List<MediaItem>> =
        context.contentResolver.observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapLatest { fetchMusics() }
            .flowOn(Dispatchers.IO)

    suspend fun fetchMusics(): List<MediaItem> {
        val musics = mutableListOf<MediaItem>()
        val whitelistedFolders = getWhitelistedFolders(context).first()

        if (whitelistedFolders.isEmpty()) return emptyList()


        val minTrackDuration = getMinTrackDuration(context).first()
        val selection = buildString {
            append("${MediaStore.Audio.Media.DURATION} >= ?")
            append(" AND ${MediaStore.Audio.Media.IS_MUSIC} != ?")
            append(" AND ")
            append(whitelistedFolders.joinToString(" AND ") { "${MediaStore.Audio.Media.DATA} LIKE ?" })
        }
        val selectionArgs = mutableListOf<String>().apply {
            add("${minTrackDuration * 1000}")
            add("0")
            addAll(whitelistedFolders.map { "$it%" })
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
                    MediaItem
                        .Builder()
                        .setUri(uri)
                        .setMediaId(mediaId)
                        .setMediaMetadata(
                            MediaMetadata
                                .Builder()
                                .setIsBrowsable(false)
                                .setIsPlayable(true)
                                .setTitle(title)
                                .setArtist(artist)
                                .setAlbumTitle(album)
                                .setArtworkUri(artUri)
                                .setDurationMs(duration)
                                .setTrackNumber(trackNumber)
                                .setRecordingYear(year)
                                .setExtras(
                                    Bundle()
                                        .apply {
                                            putString("folder", folder)
                                            putLong("size", size)
                                            putString("path", filePath)
                                            putLong("album_id", albumId)
                                            putLong("artist_id", artistId)
                                            putBoolean("is_saf", false)
                                            putLong("date_modified", dateModified)
                                        }
                                ).build()
                        )
                        .build()
                )
            }
        }

        return musics
    }

    fun deleteMusics(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        try {
            uris.forEach { uri ->
                context.contentResolver.delete(uri, null, null)
            }
        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intentSender = MediaStore.createDeleteRequest(
                    context.contentResolver,
                    uris
                ).intentSender

                intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
        } catch (e: Exception) {
            Log.e(
                ContentValues.TAG,
                "Error trying to delete song: ${e.message}"
            )
        }
    }

    fun editMusic(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intentSender = MediaStore.createWriteRequest(
                context.contentResolver,
                uris
            ).intentSender
            intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
        }
    }
}
