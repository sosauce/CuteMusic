package com.sosauce.cutemusic.data.models

/**
 * @param albumId Used to get artwork
 */
data class Artist(
    val id: Long = 0,
    val name: String = "",
    val albumId: Long = 0,
    val numberTracks: Int = 0,
    val numberAlbums: Int = 0
)
