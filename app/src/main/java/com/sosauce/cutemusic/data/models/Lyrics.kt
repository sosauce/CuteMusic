package com.sosauce.cutemusic.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.random.Random

@Serializable
data class Lyrics(
    val timestamp: Int = 0,
    val lineLyrics: String = "",
    val id: Int = Random.nextInt()
)
