package com.sosauce.cutemusic.logic

sealed interface PlayerActions {
    data object PlayOrPause : PlayerActions
    data object SeekToNextMusic : PlayerActions
    data object SeekToPreviousMusic : PlayerActions
    data class SeekTo(val position: Long) : PlayerActions
}
