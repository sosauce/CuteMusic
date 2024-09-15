package com.sosauce.cutemusic.data.actions

sealed interface MetadataActions {
    data class LoadSong(
        val path: String
    ) : MetadataActions

    data class SaveChanges(
        val path: String
    ) : MetadataActions

    data object ClearState : MetadataActions
}