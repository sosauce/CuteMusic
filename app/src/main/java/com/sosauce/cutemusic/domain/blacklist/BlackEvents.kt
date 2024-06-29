package com.sosauce.cutemusic.domain.blacklist

import com.sosauce.cutemusic.domain.model.BlacklistedFolder

sealed interface BlackEvent {

    data class DeleteBlack(val blackFolder: BlacklistedFolder): BlackEvent
    data class AddBlack(
        val name: String,
        val path: String
    ): BlackEvent
}