package com.sosauce.cutemusic.main

import android.content.Intent
import android.media.MediaDescription
import android.media.browse.MediaBrowser
import android.net.Uri
import android.os.Bundle
import android.service.media.MediaBrowserService
import androidx.media3.common.util.UnstableApi
import com.sosauce.cutemusic.domain.repository.MediaStoreHelperImpl
import com.sosauce.cutemusic.utils.ROOT_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@UnstableApi
class AutoPlaybackService : MediaBrowserService() {


    private val mediaStoreHelper by lazy { MediaStoreHelperImpl(this) }
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)


    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot = BrowserRoot(ROOT_ID, null)

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowser.MediaItem?>?>
    ) {

        val mediaItems: MutableList<MediaBrowser.MediaItem> = mutableListOf()

        if (ROOT_ID == parentId) {
            scope.launch {
                mediaStoreHelper.fetchLatestMusics().collectLatest { list ->
                    list.forEach { mediaItem ->
                        mediaItems.add(
                            MediaBrowser.MediaItem(
                                MediaDescription.Builder()
                                    .setMediaId(mediaItem.mediaId)
                                    .setTitle(mediaItem.mediaMetadata.title ?: "No title")
                                    .setIconUri(mediaItem.mediaMetadata.artworkUri ?: Uri.EMPTY)
                                    .build(),
                                MediaBrowser.MediaItem.FLAG_PLAYABLE
                            )
                        )
                    }
                }
                result.sendResult(mediaItems)
            }
        } else result.sendResult(listOf())
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        job.cancel()
    }
}