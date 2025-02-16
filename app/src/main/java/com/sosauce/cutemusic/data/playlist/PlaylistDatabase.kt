package com.sosauce.cutemusic.data.playlist

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sosauce.cutemusic.data.MediaItemConverter
import com.sosauce.cutemusic.domain.model.Playlist

@Database(
    entities = [Playlist::class],
    version = 1
)
@TypeConverters(MediaItemConverter::class)
abstract class PlaylistDatabase : RoomDatabase() {
    abstract val dao: PlaylistDao
}