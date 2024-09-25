package com.sosauce.cutemusic.main

import android.app.Application
import com.sosauce.cutemusic.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.java.KoinAndroidApplication
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}