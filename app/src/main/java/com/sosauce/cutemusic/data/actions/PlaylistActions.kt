package com.sosauce.cutemusic.data.actions

import com.sosauce.cutemusic.domain.model.Playlist

sealed interface PlaylistActions {

    data object CreatePlaylist: PlaylistActions
    data class DeletePlaylist(val playlist: Playlist): PlaylistActions
    data class UpsertPlaylist(val playlist: Playlist) : PlaylistActions // Modify a playlist basically

}