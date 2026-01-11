package com.sosauce.cutemusic.data.states

import androidx.compose.runtime.Stable
import androidx.media3.common.Player
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.domain.model.Lyrics
import kotlinx.serialization.Serializable

@Serializable
@Stable
data class MusicState(
    val track: CuteTrack = CuteTrack(),
    val isPlaying: Boolean = false,
    val position: Long = 0L,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val isPlayerReady: Boolean = false,
    val sleepTimerRemainingDuration: Long = 0,
    val mediaIndex: Int = 0,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val shuffle: Boolean = false,
    val loadedMedias: List<CuteTrack> = emptyList(),
    val lyrics: List<Lyrics> = emptyList(),
    val audioSessionAudio: Int = 0
)
