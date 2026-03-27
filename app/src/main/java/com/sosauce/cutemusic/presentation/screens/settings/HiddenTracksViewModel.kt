package com.sosauce.cutemusic.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.AbstractTracksScanner
import com.sosauce.cutemusic.data.datastore.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HiddenTracksViewModel(
    private val abstractTracksScanner: AbstractTracksScanner,
    private val userPreferences: UserPreferences
): ViewModel() {


    val hiddenTracks = abstractTracksScanner.fetchLatestTracks(null, null, true)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    fun unhideTrack(mediaId: String) {
        viewModelScope.launch {
            userPreferences.unhideTrack(mediaId)
        }
    }

}