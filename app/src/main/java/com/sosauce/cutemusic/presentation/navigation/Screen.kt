package com.sosauce.cutemusic.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen() : NavKey {
    @Serializable
    data object Main : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object Albums : Screen()

    @Serializable
    data object Artists : Screen()

    @Serializable
    data object Playlists : Screen()

    @Serializable
    data object Setup : Screen()

    @Serializable
    data object Queue : Screen()

    @Serializable
    data class Lyrics(
        val trackPath: String
    ) : Screen()
    @Serializable
    data class AlbumsDetails(
        val name: String
    ) : Screen()

    @Serializable
    data class ArtistsDetails(
        val name: String
    ) : Screen()

    @Serializable
    data class PlaylistDetails(
        val id: Int
    ) : Screen()

    @Serializable
    data class MetadataEditor(
        val trackPath: String,
        val trackUri: String
    ) : Screen()
}