package com.sosauce.cutemusic.audio

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getMusics(contentResolver: ContentResolver): List<Music> = withContext(Dispatchers.IO) {
    val musics = mutableListOf<Music>()

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
    )

    contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        "${MediaStore.Audio.Media.IS_MUSIC} != 0",
        null,
        "${MediaStore.Audio.Media.TITLE} ASC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
        val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
        val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val title = cursor.getString(titleColumn)
            val artist = cursor.getString(artistColumn)
            val album = cursor.getString(albumColumn)
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )
            val music = Music(id, title, artist, album, uri)

            musics.add(music)
        }
    }

    return@withContext musics
}

suspend fun getAlbums(contentResolver: ContentResolver): List<Album> = withContext(Dispatchers.IO) {
    val albums = mutableListOf<Album>()

    val projection = arrayOf(
        MediaStore.Audio.Albums._ID,
        MediaStore.Audio.Albums.ALBUM,
        MediaStore.Audio.Albums.ARTIST,
        MediaStore.Audio.Albums.NUMBER_OF_SONGS
    )

    contentResolver.query(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        "${MediaStore.Audio.Albums.ALBUM} ASC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndex(MediaStore.Audio.Albums._ID)
        val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
        val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)
        val numberOfSongsColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val album = cursor.getString(albumColumn)
            val artist = cursor.getString(artistColumn)
            val numberOfSongs = cursor.getInt(numberOfSongsColumn)
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )

            val albumInfo = Album(id, album, artist, numberOfSongs, uri)
            albums.add(albumInfo)
        }
    }

    return@withContext albums
}


