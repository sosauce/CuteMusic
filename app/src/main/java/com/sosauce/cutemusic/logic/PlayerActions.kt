package com.sosauce.cutemusic.logic

sealed interface PlayerActions {
    object Play : PlayerActions
    object Pause : PlayerActions
    object SeekToNextMusic : PlayerActions
    object SeekToPreviousMusic : PlayerActions
    object ApplyLoop : PlayerActions
    object ApplyShuffle : PlayerActions
}