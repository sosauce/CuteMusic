package com.sosauce.cutemusic.audio.service

import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlayerService: MediaSessionService() { // Thanks to that one StackOverflow user for this 🥺

    lateinit var player: Player
    private lateinit var session: MediaSession

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(applicationContext).build()
        session = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = session

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        player.release()
        session.release()
        stopSelf()
    }

    override fun onDestroy() {
        player.release()
        session.release()
        super.onDestroy()
    }
}
