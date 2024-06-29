package com.sosauce.cutemusic.domain.blacklist

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.sosauce.cutemusic.domain.model.BlacklistedFolder

data class BlackState (
    val blacklistedFolders: List<BlacklistedFolder> = emptyList(),
    val name: MutableState<String> = mutableStateOf(""),
    val path: MutableState<String> = mutableStateOf("")
)