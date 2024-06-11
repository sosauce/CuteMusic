package com.sosauce.cutemusic.main

import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture

interface AppContainer {
    val controllerFuture: ListenableFuture<MediaController>
}

class DefaultAppContainer(
    override val controllerFuture: ListenableFuture<MediaController>
) : AppContainer