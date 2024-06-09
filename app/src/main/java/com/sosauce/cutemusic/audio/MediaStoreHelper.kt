package com.sosauce.cutemusic.audio

import android.content.ContentResolver
import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

object MediaStoreHelper {

	suspend fun getMusics(
		contentResolver: ContentResolver,
		albumId: Long? = null,
		artistId: Long? = null
	): List<Music> =
		withContext(Dispatchers.IO) {
			Log.d("MediaStoreQuery", "Starting to query MediaStore for music")
			val musics = mutableListOf<Music>()

			val projection = arrayOf(
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.ARTIST
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
				val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
				val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
				val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

				while (cursor.moveToNext()) {
					val id = cursor.getLong(idColumn)
					val title = cursor.getString(titleColumn)
					val artist = cursor.getString(artistColumn)
					val uri = ContentUris.withAppendedId(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						id
					)
					musics.add(Music(id, title, artist, uri))
				}
			}
			Log.d("MediaStoreQuery", "Finished querying MediaStore for music")
			return@withContext musics
		}


	@Suppress("DEPRECATION")
	suspend fun getAlbums(contentResolver: ContentResolver, artistId: Long? = null): List<Album> =
		withContext(Dispatchers.IO) {
			val albums = mutableListOf<Album>()

			val projection = arrayOf(
				MediaStore.Audio.Albums._ID,
				MediaStore.Audio.Albums.ALBUM,
				MediaStore.Audio.Albums.ARTIST,
				MediaStore.Audio.Albums.NUMBER_OF_SONGS,
				MediaStore.Audio.Albums.ALBUM_ART
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
				val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
				val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
				val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
				val numberOfSongsColumn =
					cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
				val albumArtColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART)

				while (cursor.moveToNext()) {
					val id = cursor.getLong(idColumn)
					val album = cursor.getString(albumColumn)
					val artist = cursor.getString(artistColumn)
					val numberOfSongs = cursor.getInt(numberOfSongsColumn)
					val albumArt = cursor.getString(albumArtColumn)

					val albumInfo = Album(id, album, artist, numberOfSongs, albumArt)
					albums.add(albumInfo)
				}
			}


			val deferredResults = albums.map { album ->
				async(Dispatchers.IO) {
					val songs = getMusics(contentResolver, albumId = album.id).toPersistentList()
					val albumArt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
						getAlbumArt(contentResolver, album.id)
					} else {

						album.albumArt
					}
					album.copy(songs = songs, albumArt = albumArt)
				}
			}
			return@withContext deferredResults.awaitAll()
		}

	@OptIn(ExperimentalCoroutinesApi::class)
	suspend fun getArtists(contentResolver: ContentResolver): List<Artist> =
		withContext(Dispatchers.IO) {
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
				val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
				val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
				val numberOfAlbumsColumn =
					cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
				val numberOfTracksColumn =
					cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)

				while (cursor.moveToNext()) {
					val id = cursor.getLong(idColumn)
					val artist = cursor.getString(artistColumn)
					val numberOfAlbums = cursor.getInt(numberOfAlbumsColumn)
					val numberOfTracks = cursor.getInt(numberOfTracksColumn)

					val artistInfo = Artist(
						id = id,
						name = artist,
						numberOfSongs = numberOfTracks,
						numberOfAlbums = numberOfAlbums,
					)
					artists.add(artistInfo)
				}
			}

			val allAlbumsDeferred = async(Dispatchers.IO) { getAlbums(contentResolver) }
			val allMusicsDeferred = async(Dispatchers.IO) { getMusics(contentResolver) }

			awaitAll(allAlbumsDeferred, allMusicsDeferred)

			val deferredResults = artists.map { artist ->
				async(Dispatchers.Default) {
					val albums = allAlbumsDeferred.getCompleted()
						.filter { it.artist == artist.name }.toPersistentList()
					val songs = allMusicsDeferred.getCompleted()
						.filter { it.artist == artist.name }.toPersistentList()
					artist.copy(albums = albums, songs = songs)
				}
			}

			return@withContext deferredResults.awaitAll()
		}

}



