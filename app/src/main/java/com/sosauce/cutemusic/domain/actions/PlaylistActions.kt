package com.sosauce.cutemusic.data.actions

import android.net.Uri
import com.sosauce.cutemusic.domain.model.Playlist

sealed interface PlaylistActions {

    data object CreatePlaylist : PlaylistActions
    data class UpdateStateName(val name: String) : PlaylistActions
    data class UpdateStateEmoji(val emoji: String) : PlaylistActions
    data class DeletePlaylist(val playlist: Playlist) : PlaylistActions
    data class UpsertPlaylist(val playlist: Playlist) :
        PlaylistActions // Modify a playlist basically

    data class ImportM3uPlaylist(val uri: Uri) : PlaylistActions
    data class ExportM3uPlaylist(
        val uri: Uri,
        val tracks: List<String>
    ) : PlaylistActions
}