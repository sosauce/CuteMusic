@file:OptIn(KoinExperimentalAPI::class)

package com.sosauce.cutemusic.domain

import android.app.Application
import com.sosauce.cutemusic.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

class App : Application(), KoinStartup {
    override fun onKoinStartup() = koinConfiguration {
        androidContext(this@App)
        modules(appModule)
    }
}