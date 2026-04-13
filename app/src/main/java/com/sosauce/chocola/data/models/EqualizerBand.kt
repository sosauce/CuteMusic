package com.sosauce.chocola.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EqualizerBand(
    val centerFrequencyMilliHertz: Int,
    val millibelsLevel: Short
)
