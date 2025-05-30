package com.sosauce.cutemusic.data.actions

import androidx.media3.common.MediaItem

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
    data class SeekToMusicIndex(val index: Int) : PlayerActions

    /**
     * @param mediaId If set to null, it means we want to play a random song
     */
    data class StartAlbumPlayback(
        val albumName: String,
        val mediaId: String?
    ) : PlayerActions

    /**
     * @param mediaId If set to null, it means we want to play a random song
     */
    data class StartArtistPlayback(
        val artistName: String,
        val mediaId: String?
    ) : PlayerActions

    data class StartPlaylistPlayback(
        val playlistSongsId: List<String>,
        val mediaId: String?
    ) : PlayerActions

    data class UpdateCurrentPosition(
        val position: Long
    ) : PlayerActions

    data class SetSleepTimer(
        val hours: Int,
        val minutes: Int
    ) : PlayerActions

    data class ReArrangeQueue(
        val from: Int,
        val to: Int
    ) : PlayerActions

    data class RemoveFromQueue(
        val mediaId: String
    ) : PlayerActions

    data class AddToQueue(
        val mediaItem: MediaItem
    ) : PlayerActions
}
