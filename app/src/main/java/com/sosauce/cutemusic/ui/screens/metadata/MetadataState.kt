package com.sosauce.cutemusic.ui.screens.metadata

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class MetadataState(
    val mutablePropertiesMap: SnapshotStateList<String> = mutableStateListOf(),
    val songPath: String = ""
    //var art: Artwork? = null
)