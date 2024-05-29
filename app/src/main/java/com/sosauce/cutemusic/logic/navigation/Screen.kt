package com.sosauce.cutemusic.logic.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Main : Screen()

    @Serializable
    data object NowPlaying : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object Albums : Screen()

    @Serializable
    data object Artists : Screen()

    @Serializable
    data class AlbumsDetails(
        val id: Int
    ) : Screen()

    @Serializable
    data class ArtistsDetails(
        val id: Int
    ) : Screen()
}