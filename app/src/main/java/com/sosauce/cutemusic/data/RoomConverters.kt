package com.sosauce.cutemusic.data

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class MediaItemConverter {


    @TypeConverter
    fun mediaItemToString(mediaItems: List<String>): String {
        return Json.encodeToString(mediaItems)
    }

    @TypeConverter
    fun stringToMediaItem(string: String): List<String> {
        return Json.decodeFromString<List<String>>(string)
    }


}