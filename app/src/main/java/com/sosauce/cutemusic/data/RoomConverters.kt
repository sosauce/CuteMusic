package com.sosauce.cutemusic.data

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class MediaItemConverter {


    @TypeConverter
    fun stringListToString(mediaItems: List<String>): String {
        return Json.encodeToString(mediaItems)
    }

    @TypeConverter
    fun stringToListString(string: String): List<String> {
        return Json.decodeFromString<List<String>>(string)
    }


}