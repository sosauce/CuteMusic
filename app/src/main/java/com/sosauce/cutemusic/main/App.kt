package com.sosauce.cutemusic.main

import android.app.Application
import android.content.ComponentName
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.sosauce.cutemusic.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        val sessionToken =
            SessionToken(applicationContext, ComponentName(this, PlaybackService::class.java))
        val mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        container = DefaultAppContainer(mediaControllerFuture)
    }
}