package com.sosauce.chocola.presentation.screens.lyrics

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.LyricsParser
import com.sosauce.chocola.domain.model.Lyrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LyricsViewModel(
    private val trackPath: String,
    private val lyricsParser: LyricsParser
): ViewModel() {

    private val _state = MutableStateFlow(LyricsState(true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(
                    isLoading = false,
                    lyrics = lyricsParser.parseLyrics(trackPath)
                )
            }
        }
    }

    fun loadLrcFile(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val lyrics = lyricsParser.parseLyrics(uri.path ?: return@launch)
            _state.update {
                it.copy(lyrics = lyrics)
            }
        }
    }
}

data class LyricsState(
    val isLoading: Boolean = false,
    val lyrics: List<Lyrics> = emptyList()
)