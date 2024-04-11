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

