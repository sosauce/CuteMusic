package com.sosauce.cutemusic.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.domain.repository.SafManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SafViewModel(
    private val safManager: SafManager
) : ViewModel() {

    private val _safTrack = MutableStateFlow(emptyList<CuteTrack>())
    val safTracks = _safTrack.asStateFlow()

    init {
        viewModelScope.launch {
            safManager.fetchLatestSafTracks().collectLatest { tracks ->
                _safTrack.update { tracks }
            }
        }
    }

}