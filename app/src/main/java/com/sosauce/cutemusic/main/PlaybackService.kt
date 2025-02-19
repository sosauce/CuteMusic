package com.sosauce.cutemusic.main

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.utils.CUTE_MUSIC_ID


class PlaybackService : MediaLibraryService(),
    MediaLibrarySession.Callback,
    Player.Listener {

    private var mediaLibrarySession: MediaLibrarySession? = null
    private val audioAttributes = AudioAttributes
        .Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()


    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        sendMusicBroadcast(mediaMetadata.title.toString())
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        mediaLibrarySession


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




//            .setBitmapLoader(object : BitmapLoader {
//
//                override fun supportsMimeType(mimeType: String): Boolean = true
//
//                override fun decodeBitmap(data: ByteArray): ListenableFuture<Bitmap> =
//                    throw UnsupportedOperationException()
//
//                override fun loadBitmap(uri: Uri): ListenableFuture<Bitmap> = throw UnsupportedOperationException()
//
//                override fun loadBitmapFromMetadata(metadata: MediaMetadata): ListenableFuture<Bitmap>? {
//                    val completer = SettableFuture.create<Bitmap>()
//                    val request = ImageRequest.Builder(this@PlaybackService)
//                        .data(
//                            if (metadata.artworkUri == Uri.parse("content://media/external/audio/media/1000000397/albumart")) {
//                                R.drawable.artist
//                            } else {
//                                metadata.artworkUri
//                            }
//                        )
//                        .target(
//                            onSuccess = { result ->
//                                completer.set((result as BitmapImage).bitmap)
//                            },
//                            onError = { _ ->
//                                completer.setException(Exception("Error"))
//                            }
//                        )
//                        .build()
//                    println("Art URI: ${metadata.artworkUri}")
//                    ImageLoader(this@PlaybackService).enqueue(request)
//
//                    return completer
//                }
//
//                }

//)
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()
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
        super.onDestroy()
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }


    companion object {
        private const val CURRENTLY_PLAYING_CHANGED = "CM_CUR_PLAY_CHANGED"
    }

    private fun sendMusicBroadcast(
        currentlyPlaying: String
    ) {
        Intent(CURRENTLY_PLAYING_CHANGED).apply {
            putExtra("currentlyPlaying", currentlyPlaying)
            sendBroadcast(this)
        }
    }


}
