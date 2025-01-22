package com.sosauce.cutemusic.data.playlist

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class PlaylistState(
    val emoji: MutableState<String> = mutableStateOf(""),
    val name: MutableState<String> = mutableStateOf("")
)
