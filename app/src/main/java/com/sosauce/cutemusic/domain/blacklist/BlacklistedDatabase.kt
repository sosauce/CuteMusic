package com.sosauce.cutemusic.domain.blacklist

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sosauce.cutemusic.domain.model.BlacklistedFolder

@Database(
    entities = [BlacklistedFolder::class],
    version = 1
)
abstract class BlacklistedDatabase: RoomDatabase() {
    abstract val dao: BlackDao
}