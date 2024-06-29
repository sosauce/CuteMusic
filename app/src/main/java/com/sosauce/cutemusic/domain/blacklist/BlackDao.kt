package com.sosauce.cutemusic.domain.blacklist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sosauce.cutemusic.domain.model.BlacklistedFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface BlackDao {

    @Insert // No need to use Upsert as adding two same folders is impossible
    suspend fun insertBlackFolder(blackFolder: BlacklistedFolder)

    @Delete
    suspend fun deleteBlackFolder(blackFolder: BlacklistedFolder)

    @Query("SELECT * FROM blacklistedfolder")
    fun getBlackFolders(): Flow<List<BlacklistedFolder>>
}