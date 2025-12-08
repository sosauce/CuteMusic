package com.sosauce.cutemusic.data.states

import androidx.compose.runtime.Stable
import androidx.media3.common.Player
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.domain.model.Lyrics

@Stable
data class MusicState(
    val track: CuteTrack = CuteTrack(),
//    val title: String = "",
//    val artist: String = "",
//    val artistId: Long = 0,
//    val art: Uri? = null,
    val isPlaying: Boolean = false,
    val position: Long = 0L,
//    val duration: Long = 0L,
//    val uri: String = "",
//    val path: String = "",
//    val album: String = "",
//    val albumId: Long = 0,
//    val size: Long = 0,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val isPlayerReady: Boolean = false,
    //val sleepTimerActive: Boolean = false,
    val sleepTimerRemainingDuration: Long = 0,
    //val mediaId: String = "",
    val mediaIndex: Int = 0,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val shuffle: Boolean = false,
    val loadedMedias: List<CuteTrack> = emptyList(),
    val lyrics: List<Lyrics> = emptyList(),
    val audioSessionAudio: Int = 0
)
