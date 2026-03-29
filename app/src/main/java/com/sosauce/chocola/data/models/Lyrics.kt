package com.sosauce.chocola.domain.model

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class Lyrics(
    val timestamp: Int = 0,
    val lineLyrics: String = "",
    val id: Int = Random.nextInt()
)
