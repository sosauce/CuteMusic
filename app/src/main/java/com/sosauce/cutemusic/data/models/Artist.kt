package com.sosauce.cutemusic.domain.model

/**
 * @param albumId Used to get artwork
 */
data class Artist(
    val id: Long,
    val name: String,
    val albumId: Long,
    val numberTracks: Int,
    val numberAlbums: Int
)
