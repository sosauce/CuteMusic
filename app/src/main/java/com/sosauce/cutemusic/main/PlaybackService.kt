package com.sosauce.cutemusic.main

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.sosauce.cutemusic.data.datastore.rememberKillService

class PlaybackService : MediaLibraryService() {

    private var mediaLibrarySession: MediaLibrarySession? = null
    private var callback: MediaLibrarySession.Callback = object : MediaLibrarySession.Callback {}
    private val audioAttributes = AudioAttributes
        .Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val shouldKill by rememberKillService(this)


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        mediaLibrarySession

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
        mediaLibrarySession = MediaLibrarySession
            .Builder(this, player, callback)
            .build()
    }


    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        super.onDestroy()
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        if (shouldKill) {
            super.onTaskRemoved(rootIntent)
            mediaLibrarySession?.run {
                player.release()
                release()
                mediaLibrarySession = null
                stopSelf()
            }
        }
    }

}