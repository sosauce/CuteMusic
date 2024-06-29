package com.sosauce.cutemusic.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BlacklistedFolder (
    val name: String,
    val path: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)