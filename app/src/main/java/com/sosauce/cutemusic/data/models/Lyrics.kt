package com.sosauce.cutemusic.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Lyrics(
    val timestamp: Long = 0L,
    val lineLyrics: String = "",
    val id: String = UUID.randomUUID().toString()
)
