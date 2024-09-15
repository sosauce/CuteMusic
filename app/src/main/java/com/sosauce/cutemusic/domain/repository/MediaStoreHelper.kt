package com.sosauce.cutemusic.domain.repository

import android.content.ContentUris
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.domain.model.Folder

class MediaStoreHelper(
    private val context: Context
) {
    fun getMusics(): List<MediaItem> {

        val musics = mutableListOf<MediaItem>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            //MediaStore.Audio.Media.IS_FAVORITE,
        )


        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            //val isFavColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_FAVORITE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val filePath = cursor.getString(folderColumn)
                val folder = filePath.substring(0, filePath.lastIndexOf('/'))
                val size = cursor.getLong(sizeColumn)
                //val isFavorite = cursor.getInt(isFavColumn) // 1 = is favorite, 0 = no
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                val artUri = ContentUris.appendId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon(), id
                )
                    .appendPath("albumart").build()

                musics.add(
                    MediaItem
                        .Builder()
                        .setUri(uri)
                        .setMediaId(id.toString())
                        .setMediaMetadata(
                            MediaMetadata
                                .Builder()
                                .setIsBrowsable(false)
                                .setIsPlayable(true)
                                .setTitle(title)
                                .setArtist(artist)
                                .setArtworkUri(artUri)
                                .setExtras(Bundle().apply {
                                    putLong("albumId", albumId)
                                    putString("folder", folder)
                                    putLong("size", size)
                                    putString("path", filePath)
                                    putString("uri", uri.toString())
                                    // putInt("isFavorite", isFavorite)
                                }).build()
                        ).build()
                )
            }
        }
        return musics
    }


    fun getAlbums(): List<Album> {
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
                albums.add(albumInfo)
            }
        }

        return albums
    }

    fun getArtists(): List<Artist> {
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
                artists.add(artistInfo)
            }
        }

        return artists
    }


    // Only gets folder with musics in them
    fun getFoldersWithMusics(): List<Folder> {

        val folders = mutableListOf<Folder>()

        val projection = arrayOf(
            MediaStore.Audio.Media.DATA
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
                        path = path
                    )
                )
            }
        }
        return folders
    }

}