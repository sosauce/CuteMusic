package com.sosauce.cutemusic.audio

import android.content.ContentResolver
import android.content.ContentUris
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getMusics(contentResolver: ContentResolver, albumId: Long? = null, artistId: Long? = null): List<Music> =
    withContext(Dispatchers.IO) {
        val musics = mutableListOf<Music>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM
        )

        val selection = when {
            albumId != null -> "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.ALBUM_ID} = ?"
            artistId != null -> "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.ARTIST_ID} = ?"
            else -> "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        }

        val selectionArgs = when {
            albumId != null -> arrayOf(albumId.toString())
            artistId != null -> arrayOf(artistId.toString())
            else -> null
        }

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
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

suspend fun getAlbums(contentResolver: ContentResolver, artistId: Long? = null): List<Album> = withContext(Dispatchers.IO) {
    val albums = mutableListOf<Album>()

    val projection = arrayOf(
        MediaStore.Audio.Albums._ID,
        MediaStore.Audio.Albums.ALBUM,
        MediaStore.Audio.Albums.ARTIST,
        MediaStore.Audio.Albums.NUMBER_OF_SONGS
    )

    val selection = if (artistId != null) {
        "${MediaStore.Audio.Media.ARTIST_ID} = ?"
    } else {
        null
    }
    val selectionArgs = if (artistId != null) {
        arrayOf(artistId.toString())
    } else {
        null
    }

    contentResolver.query(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
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


            val albumInfo = Album(id, album, artist, numberOfSongs, null, listOf())
            val art = getAlbumArt(contentResolver, id)
            val containedSongs = getMusics(contentResolver, albumId = id)
            albumInfo.albumArt = art
            albumInfo.songs = containedSongs
            albums.add(albumInfo)
        }
    }

    return@withContext albums
}

suspend fun getArtists(contentResolver: ContentResolver): List<Artist> = withContext(Dispatchers.IO) {
    val artists = mutableListOf<Artist>()

    val projection = arrayOf(
        MediaStore.Audio.Artists._ID,
        MediaStore.Audio.Artists.ARTIST,
        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
        MediaStore.Audio.Artists.NUMBER_OF_TRACKS
    )

    contentResolver.query(
        MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        "${MediaStore.Audio.Artists.ARTIST} ASC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndex(MediaStore.Audio.Artists._ID)
        val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
        val artistName = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)
        val numberOfSongsColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val numberOfAlbums = cursor.getString(albumColumn)
            val name = cursor.getString(artistName)
            val numberOfSongs = cursor.getInt(numberOfSongsColumn)


            val artistInfo = Artist(
                id = id,
                name = name,
                numberOfSongs = numberOfSongs,
                songs = listOf(),
                albums = listOf(),
                numberOfAlbums = numberOfAlbums
            )
            val containedSongs = getMusics(contentResolver, artistId = id)
            val containedAlbum = getAlbums(contentResolver, artistId = id)
            artistInfo.songs = containedSongs
            artistInfo.albums = containedAlbum
            artists.add(artistInfo)
        }
    }

    return@withContext artists
}


