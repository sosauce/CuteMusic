package com.sosauce.cutemusic.domain

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaConstants
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.ListenableFuture
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.presentation.MainActivity
import com.sosauce.cutemusic.presentation.widgets.WidgetBroadcastReceiver
import com.sosauce.cutemusic.presentation.widgets.WidgetCallback
import com.sosauce.cutemusic.utils.CUTE_MUSIC_ID
import com.sosauce.cutemusic.utils.PACKAGE
import com.sosauce.cutemusic.utils.WIDGET_NEW_DATA
import com.sosauce.cutemusic.utils.WIDGET_NEW_IS_PLAYING


@UnstableApi
class PlaybackService : MediaLibraryService(), MediaLibrarySession.Callback, Player.Listener,
    WidgetCallback {

    private var mediaLibrarySession: MediaLibrarySession? = null
    private val audioAttributes = AudioAttributes
        .Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val widgetReceiver = WidgetBroadcastReceiver()



    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
            putExtra(WIDGET_NEW_DATA, WIDGET_NEW_DATA)
            putExtra("title", mediaMetadata.title.toString())
            putExtra("artist", mediaMetadata.artist.toString())
            putExtra("artUri", mediaMetadata.artworkUri.toString())
        }

        sendBroadcast(intent)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)

        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
            putExtra(WIDGET_NEW_DATA, WIDGET_NEW_IS_PLAYING)
            putExtra("isPlaying", isPlaying)
        }

        sendBroadcast(intent)
    }


    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        super.onAudioSessionIdChanged(audioSessionId)
        println("Testing, service audiosession = $audioSessionId")
        mediaLibrarySession?.sessionExtras = Bundle().apply {
            putInt("audioSessionId", audioSessionId)
        }
    }



    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        mediaLibrarySession


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
        mediaLibrarySession = MediaLibrarySession
            .Builder(this, player, this)
            .setId(CUTE_MUSIC_ID)
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()





        IntentFilter(PACKAGE).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    widgetReceiver,
                    it,
                    RECEIVER_EXPORTED
                )
            } else {
                registerReceiver(widgetReceiver, it)
            }
        }
        widgetReceiver.startCallback(this)

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this).build().apply {
                setSmallIcon(R.drawable.music_note_rounded)
            }
        )

        player.addListener(this)

    }


    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        stopSelf()
        try {
            widgetReceiver.also {
                it.stopCallback()
                unregisterReceiver(it)
            }
        } catch (e: IllegalArgumentException) {
            super.onDestroy()
        }
        super.onDestroy()
    }

    // Android Auto support ?
    // https://github.com/androidx/media/blob/release/demos/session_automotive/src/main/java/androidx/media3/demo/session/automotive/AutomotiveService.kt
    @UnstableApi
    override fun onGetLibraryRoot(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {

        var responseParams = params

        if (session.isAutomotiveController(browser)) {
            responseParams = params ?: LibraryParams.Builder().build().apply {
                extras.putInt(
                    MediaConstants.EXTRAS_KEY_CONTENT_STYLE_BROWSABLE,
                    MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM
                )
                extras.putInt(
                    MediaConstants.EXTRAS_KEY_CONTENT_STYLE_PLAYABLE,
                    MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM
                )
            }
        }

        return super.onGetLibraryRoot(session, browser, responseParams)
    }


    @UnstableApi
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        widgetReceiver.also {
            it.stopCallback()
            unregisterReceiver(it)
        }
        pauseAllPlayersAndStopSelf()
    }


    override fun skipToNext() {
        mediaLibrarySession?.player?.seekToNextMediaItem()
    }

    override fun playOrPause() {

        if (mediaLibrarySession?.player?.isPlaying == true) {
            mediaLibrarySession?.player?.pause()
        } else {
            mediaLibrarySession?.player?.play()
        }
    }

    override fun skipToPrevious() {
        mediaLibrarySession?.player?.seekToPrevious()
    }
}
