package com.sosauce.cutemusic.data.actions

sealed interface PlayerActions {
    data object PlayOrPause : PlayerActions
    data object SeekToNextMusic : PlayerActions
    data object SeekToPreviousMusic : PlayerActions
    data object RestartSong : PlayerActions
    data object PlayRandom : PlayerActions
    data object StopPlayback : PlayerActions
    data class SeekTo(val position: Long) : PlayerActions
    data class SeekToSlider(val position: Long) : PlayerActions
    data class RewindTo(val position: Long) : PlayerActions
    data class StartPlayback(val mediaId: String) : PlayerActions
    data class StartAlbumPlayback(
        val albumName: String,
        val mediaId: String?
    ) : PlayerActions

    data class StartArtistPlayback(
        val artistName: String,
        val mediaId: String?
    ) : PlayerActions

    data class ApplyPlaybackSpeed(
        val speed: Float,
        val pitch: Float
    ) : PlayerActions

    data class UpdateCurrentPosition(
        val position: Long
    ) : PlayerActions

    data class SetSleepTimer(
        val hours: Int,
        val minutes: Int
    ) : PlayerActions
}
