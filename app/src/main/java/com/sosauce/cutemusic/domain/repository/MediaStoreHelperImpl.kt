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
import androidx.media3.common.util.UnstableApi
import com.sosauce.cutemusic.data.datastore.getBlacklistedFolder
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.domain.model.Folder
import com.sosauce.cutemusic.utils.observe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

@SuppressLint("UnsafeOptInUsageError")
class MediaStoreHelperImpl(
    private val context: Context
) : MediaStoreHelper {


    private fun getBlacklistedFoldersAsync(): Set<String> =
        runBlocking { getBlacklistedFolder(context) }


    private val blacklistedFolders = getBlacklistedFoldersAsync()
    private val selection =
        blacklistedFolders.joinToString(" AND ") { "${MediaStore.Audio.Media.DATA} NOT LIKE ?" }
    private val selectionArgs = blacklistedFolders.map { "$it%" }.toTypedArray()


    override fun fetchLatestMusics(): Flow<List<MediaItem>> =
        context.contentResolver.observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .map { fetchMusics() }

    override fun fetchLatestAlbums(): Flow<List<Album>> =
        context.contentResolver.observe(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI)
            .map { fetchAlbums() }

    override fun fetchLatestArtists(): Flow<List<Artist>> =
        context.contentResolver.observe(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI)
            .map { fetchArtists() }

    override fun fetchLatestFoldersWithMusics(): Flow<List<Folder>> =
        context.contentResolver.observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .map { fetchFoldersWithMusics() }

    @UnstableApi
    override fun fetchMusics(): List<MediaItem> {


        val musics = mutableListOf<MediaItem>()

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
            MediaStore.Audio.Media.IS_FAVORITE,
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
            val isFavColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_FAVORITE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val artistId = cursor.getLong(artistIdColumn)
                val album = cursor.getString(albumColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val filePath = cursor.getString(folderColumn)
                val folder = filePath.substring(0, filePath.lastIndexOf('/'))
                val size = cursor.getLong(sizeColumn)
                val duration = cursor.getLong(durationColumn)
                val trackNumber = cursor.getInt(trackNbColumn)
                val isFavorite = cursor.getInt(isFavColumn) // 1 = is favorite, 0 = no
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
                                .setExtras(
                                    Bundle()
                                        .apply {
                                            putString("folder", folder)
                                            putLong("size", size)
                                            putString("path", filePath)
                                            putString("uri", uri.toString())
                                            putLong("album_id", albumId)
                                            putLong("artist_id", artistId)
                                            putBoolean("is_saf", false)
                                            putString("mediaId", mediaId)
                                            putInt("isFavorite", isFavorite)
                                        }).build()
                        )
                        .build()
                )
            }
        }

        return musics
    }


    override fun fetchAlbums(): List<Album> {
        val albums = mutableListOf<Album>()

        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
        )

        context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Audio.Albums.ALBUM} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val album = cursor.getString(albumColumn)
                val artist = cursor.getString(artistColumn)
                val albumInfo = Album(id, album, artist)

                if (albums.none { it.name == albumInfo.name }) {
                    if (musics.map { it.mediaMetadata.extras?.getLong("album_id") }.contains(id)) {
                        albums.add(albumInfo)
                    }
                }
            }
        }

        return albums
    }

    override fun fetchArtists(): List<Artist> {
        val artists = mutableListOf<Artist>()

        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
        )

        context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Audio.Artists.ARTIST} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val artist = cursor.getString(artistColumn)

                val artistInfo = Artist(
                    id = id,
                    name = artist
                )
                if (musics.map { it.mediaMetadata.extras?.getLong("artist_id") }.contains(id)) {
                    artists.add(artistInfo)
                }
            }
        }

        return artists
    }


    // Only gets folder with musics in them
    override fun fetchFoldersWithMusics(): List<Folder> {

        val folders = mutableListOf<Folder>()

        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
        )

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use {
            val folderPaths = mutableSetOf<String>()
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val filePath = it.getString(dataIndex)
                val folderPath = filePath.substring(0, filePath.lastIndexOf('/'))
                folderPaths.add(folderPath)
            }
            folderPaths.forEach { path ->
                val folderName = path.substring(path.lastIndexOf('/') + 1)
                folders.add(
                    Folder(
                        name = folderName,
                        path = path,
                    )
                )
            }
        }
        return folders
    }

    override suspend fun deleteMusics(
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
                "Error trying to delete song: ${e.message} ${e.stackTrace.joinToString()}"
            )
        }
    }

    override suspend fun editMusic(
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

    // Caching music to not re-query them in Music and Post ViewModels
    override val musics: List<MediaItem> = fetchMusics()
    override val albums: List<Album> = fetchAlbums()
    override val artists: List<Artist> = fetchArtists()
    override val folders: List<Folder> = fetchFoldersWithMusics()
}
