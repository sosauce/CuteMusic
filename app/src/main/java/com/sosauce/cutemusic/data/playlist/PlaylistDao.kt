package com.sosauce.cutemusic.data.playlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.sosauce.cutemusic.data.models.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Upsert
    suspend fun upsertPlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("SELECT * FROM playlist ORDER BY name ASC")
    fun getPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM playlist WHERE id = :id")
    fun getPlaylistDetails(id: Int): Playlist


}