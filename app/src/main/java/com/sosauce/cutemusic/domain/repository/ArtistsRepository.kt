@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.cutemusic.domain.repository

import android.content.Context
import android.provider.MediaStore
import com.sosauce.cutemusic.data.AbstractTracksScanner
import com.sosauce.cutemusic.data.models.Album
import com.sosauce.cutemusic.data.models.Artist
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.random.Random

class ArtistsRepository(
    private val context: Context,
    private val abstractTracksScanner: AbstractTracksScanner
) {

    fun fetchLatestArtistTracks(artistName: String) = abstractTracksScanner.fetchLatestTracks(
        extraSelection = "${MediaStore.Audio.Media.ARTIST} = ?",
        extraSelectionArgs = arrayOf(artistName)
    )

    fun fetchArtists(): List<Artist> {

        val artists = mutableListOf<Artist>()

        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
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
            val numberAlbumsColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
            val numberTracksColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val artist = cursor.getString(artistColumn)
                val numberTracks = cursor.getInt(numberTracksColumn)
                val numberAlbums = cursor.getInt(numberAlbumsColumn)

                val artistInfo = Artist(
                    id = id,
                    name = artist,
                    albumId = getArtistAlbumId(id),
                    numberTracks = numberTracks,
                    numberAlbums = numberAlbums

                )
                artists.add(artistInfo)
            }
        }

        return artists.distinctBy { it.name }
    }

    fun fetchArtistDetails(artistName: String): Artist {
        context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
            ),
            "${MediaStore.Audio.Artists.ARTIST} = ?",
            arrayOf(artistName),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
            val nbAlbumsColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
            val nbTracksColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
            while (cursor.moveToFirst()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val nbAlbums = cursor.getInt(nbAlbumsColumn)
                val nbTracks = cursor.getInt(nbTracksColumn)

                return Artist(
                    id = id,
                    name = name,
                    albumId = getArtistAlbumId(id),
                    numberAlbums = nbAlbums,
                    numberTracks = nbTracks
                )
            }
        }

        return Artist(Random.nextLong())
    }

    private fun getArtistAlbumId(artistId: Long): Long {
        val uri = MediaStore.Audio.Artists.Albums.getContentUri("external", artistId)

        context.contentResolver.query(
            uri,
            arrayOf(MediaStore.Audio.Artists.Albums.ALBUM_ID),
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.ALBUM_ID)

            if (cursor.moveToFirst()) {
                return cursor.getLong(idColumn)
            }
        }

        return 0
    }

    fun fetchArtistAlbums(artistName: String): List<Album> {
        val albums = mutableListOf<Album>()

        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST
        )
        context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Audio.Albums.ARTIST} = ?",
            arrayOf(artistName),
            "${MediaStore.Audio.Albums.ALBUM} ASC",
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

        return albums.distinctBy { it.name }
    }

}