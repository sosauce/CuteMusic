package com.sosauce.cutemusic.data.actions

sealed interface PlayerActions {
    data object PlayOrPause : PlayerActions
    data object SeekToNextMusic : PlayerActions
    data object SeekToPreviousMusic : PlayerActions
    data class SeekTo(val position: Long) : PlayerActions
    data class RewindTo(val position: Long) : PlayerActions
}
