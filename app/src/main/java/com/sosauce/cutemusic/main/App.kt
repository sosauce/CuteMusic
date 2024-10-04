package com.sosauce.cutemusic.main

import android.app.Application
import com.sosauce.cutemusic.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup.onKoinStartup

class App : Application() {
    init {
        @Suppress("OPT_IN_USAGE")
        onKoinStartup {
            androidContext(this@App)
            modules(appModule)
        }
    }
}