package com.sosauce.cutemusic.data.playlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.sosauce.cutemusic.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Upsert
    suspend fun upsertPlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("SELECT * FROM playlist ORDER BY name ASC")
    fun getPlaylists(): Flow<List<Playlist>>
//
//    @Query("UPDATE playlist SET name = :name, emoji = :emoji WHERE id =:id")
//    suspend fun updateNameAndEmoji(
//        id: Int,
//        name: String,
//        emoji: String
//    )



}