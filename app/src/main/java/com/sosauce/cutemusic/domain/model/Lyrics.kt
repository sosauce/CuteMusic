package com.sosauce.cutemusic.domain.model

import java.util.UUID

data class Lyrics(
    val timestamp: Long = 0L,
    val lineLyrics: String = "",
    val id: String = UUID.randomUUID().toString()
)
