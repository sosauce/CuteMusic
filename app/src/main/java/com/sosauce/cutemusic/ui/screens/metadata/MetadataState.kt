package com.sosauce.cutemusic.ui.screens.metadata

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class MetadataState(
    val mutablePropertiesMap: SnapshotStateList<String> = mutableStateListOf(),
    val songPath: String = "",
    val songUri: Uri = Uri.EMPTY
    //var art: Artwork? = null
)