@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.cutemusic.domain.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.core.net.toUri
import com.sosauce.cutemusic.data.datastore.getMinTrackDuration
import com.sosauce.cutemusic.data.datastore.getWhitelistedFolders
import com.sosauce.cutemusic.data.models.Album
import com.sosauce.cutemusic.data.models.Artist
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.utils.observe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlin.random.Random

class ArtistsRepository(
    private val context: Context
) {


    fun fetchLatestArtistTracks(artistName: String): Flow<List<CuteTrack>> =
        context.contentResolver.observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapLatest { fetchArtistTracks(artistName) }
            .flowOn(Dispatchers.IO)

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

    private suspend fun fetchArtistTracks(artistName: String): List<CuteTrack> {
        val musics = mutableListOf<CuteTrack>()
        val whitelistedFolders = getWhitelistedFolders(context).first()

        if (whitelistedFolders.isEmpty()) return emptyList()

        val minTrackDuration = getMinTrackDuration(context).first()
        val selection = buildString {
            append("${MediaStore.Audio.Media.ARTIST} = ?")
            append(" AND ${MediaStore.Audio.Media.DURATION} >= ?")
            append(" AND ${MediaStore.Audio.Media.IS_MUSIC} != ?")
            append(" AND ")
            append(whitelistedFolders.joinToString(" AND ") { "${MediaStore.Audio.Media.DATA} LIKE ?" })
        }
        val selectionArgs = mutableListOf<String>().apply {
            add(artistName)
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