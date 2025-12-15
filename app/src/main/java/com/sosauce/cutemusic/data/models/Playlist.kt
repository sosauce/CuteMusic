package com.sosauce.cutemusic.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val emoji: String = "",
    val name: String = "",
    val musics: List<String> = emptyList(), // List of songs ID aka mediaId
    val color: Int = -1,
    val tags: List<String> = emptyList()
) {
    fun toCuteTrack(): CuteTrack {
        return CuteTrack(
            title = emoji,
            artist = name
        )
    }
}
