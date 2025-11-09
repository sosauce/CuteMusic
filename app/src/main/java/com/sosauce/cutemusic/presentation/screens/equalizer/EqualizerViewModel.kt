package com.sosauce.cutemusic.presentation.screens.equalizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.CuteEqualizer
import kotlinx.coroutines.launch

class EqualizerViewModel(
    private val cuteEqualizer: CuteEqualizer
) : ViewModel() {

    fun setBandLevel(
        frequency: Int,
        level: Float
    ) {
        viewModelScope.launch {
            cuteEqualizer.setBandLevel(frequency, level)
        }
    }

    fun resetBands() {
        viewModelScope.launch {
            cuteEqualizer.resetBands()
        }
    }
}