package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.data.playlist.PlaylistDao
import com.sosauce.cutemusic.data.playlist.PlaylistState
import com.sosauce.cutemusic.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val dao: PlaylistDao
) : ViewModel() {


    val allPlaylists = dao.getPlaylists()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf()
        )
    private val _state = MutableStateFlow(PlaylistState())
    val state = _state.asStateFlow()
//
//    val state = combine(_state, allPlaylists) { innerState, playlists ->
//        innerState.copy(
//
//        )
//    }


    fun handlePlaylistActions(action: PlaylistActions) {
        when (action) {
            is PlaylistActions.DeletePlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.deletePlaylist(action.playlist)
                }
            }

            is PlaylistActions.CreatePlaylist -> {
                // When creating a playlist, user cannot add musics, they need to do it afterwards
                val name = if (state.value.name.value.isBlank()) {
                    "Playlist ${allPlaylists.value.size + 1}"
                } else {
                    state.value.name.value
                }
                val playlist = Playlist(
                    emoji = state.value.emoji.value,
                    name = name,
                    musics = listOf()
                )
                viewModelScope.launch(Dispatchers.IO) {
                    dao.upsertPlaylist(playlist)
                }

                _state.update {
                    it.copy(
                        emoji = mutableStateOf(""),
                        name = mutableStateOf("")
                    )
                }
            }

            is PlaylistActions.UpsertPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.upsertPlaylist(action.playlist)
                }
            }
        }
    }
}