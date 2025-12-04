package com.sosauce.cutemusic.domain.actions

import com.sosauce.cutemusic.data.models.CuteTrack

sealed interface PlayerActions {
    data object PlayOrPause : PlayerActions
    data object SeekToNextMusic : PlayerActions
    data object SeekToPreviousMusic : PlayerActions
    data object RestartSong : PlayerActions
    data object PlayRandom : PlayerActions
    data object StopPlayback : PlayerActions
    data object Shuffle : PlayerActions
    data object ChangeRepeatMode : PlayerActions
    data class SeekTo(val position: Long) : PlayerActions
    data class SeekToSlider(val position: Long) : PlayerActions
    data class RewindTo(val position: Long) : PlayerActions
    data class SeekToMusicIndex(val index: Int) : PlayerActions
    data class SetSpeed(val speed: Float) : PlayerActions
    data class SetPitch(val pitch: Float) : PlayerActions

    data class Play(
        val index: Int,
        val tracks: List<CuteTrack>,
        val random: Boolean = false
    ) : PlayerActions
    data class UpdateCurrentPosition(
        val position: Long
    ) : PlayerActions

    data class SetSleepTimer(
        val hours: Long,
        val minutes: Long
    ) : PlayerActions

    data class ReArrangeQueue(
        val from: Int,
        val to: Int
    ) : PlayerActions

    data class RemoveFromQueue(
        val track: CuteTrack
    ) : PlayerActions

    data class AddToQueue(
        val cuteTrack: CuteTrack
    ) : PlayerActions
}
