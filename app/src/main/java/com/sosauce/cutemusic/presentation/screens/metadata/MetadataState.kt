package com.sosauce.cutemusic.presentation.screens.metadata

import android.net.Uri
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.kyant.taglib.AudioProperties
import com.kyant.taglib.Metadata
import com.kyant.taglib.Picture

data class MetadataState(
    val mutablePropertiesMap: SnapshotStateMap<String, String> = mutableStateMapOf(),
    val songPath: String = "",
    val songUri: Uri = Uri.EMPTY,
    val metadata: Metadata? = null,
    val audioProperties: AudioProperties? = null,
    val art: Picture? = null,
    val newArtUri: Uri = Uri.EMPTY
)