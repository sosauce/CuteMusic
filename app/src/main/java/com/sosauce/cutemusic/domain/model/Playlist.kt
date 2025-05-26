package com.sosauce.cutemusic.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val emoji: String,
    val name: String,
    val musics: List<String> // List of songs ID aka mediaId
)
