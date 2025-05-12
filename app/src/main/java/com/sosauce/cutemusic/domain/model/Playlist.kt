package com.sosauce.cutemusic.domain.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val emoji: String,
    val name: String,
    val musics: List<String> // List of songs ID aka mediaId
)
