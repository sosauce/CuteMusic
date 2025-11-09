package com.sosauce.cutemusic.domain.actions

import android.net.Uri
import com.sosauce.cutemusic.data.models.Playlist

sealed interface PlaylistActions {

    data class CreatePlaylist(val playlist: Playlist) : PlaylistActions
    data class DeletePlaylist(val playlist: Playlist) : PlaylistActions
    data class UpsertPlaylist(val playlist: Playlist) :
        PlaylistActions // Modify a playlist basically

    data class ImportM3uPlaylist(val uri: Uri) : PlaylistActions
    data class ExportM3uPlaylist(
        val uri: Uri,
        val tracks: List<String>
    ) : PlaylistActions
}