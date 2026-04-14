package com.sosauce.chocola.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EqualizerPreset(
    val name: String,
    val band: Short
)
