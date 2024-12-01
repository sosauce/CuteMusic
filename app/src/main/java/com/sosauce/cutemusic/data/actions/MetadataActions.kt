package com.sosauce.cutemusic.data.actions

import android.net.Uri

sealed interface MetadataActions {

    data class LoadSong(
        val path: String,
        val uri: Uri
    ) : MetadataActions

    data class UpdateAudioArt(
        val newArtUri: Uri
    ) : MetadataActions

    data object SaveChanges : MetadataActions

    data object RemoveArtwork : MetadataActions

}