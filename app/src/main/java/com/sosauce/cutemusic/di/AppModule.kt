package com.sosauce.cutemusic.di

import androidx.room.Room
import com.sosauce.cutemusic.data.playlist.PlaylistDatabase
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.domain.repository.MediaStoreHelperImpl
import com.sosauce.cutemusic.domain.repository.SafManager
import com.sosauce.cutemusic.main.quickplay.QuickPlayViewModel
import com.sosauce.cutemusic.ui.screens.metadata.MetadataViewModel
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single<MediaStoreHelper> {
        MediaStoreHelperImpl(androidContext())
    }

    single {
        Room.databaseBuilder(
            context = androidApplication(),
            klass = PlaylistDatabase::class.java,
            name = "playlist.db"
        ).build().dao
    }


    singleOf(::SafManager)
    viewModelOf(::MusicViewModel)
    viewModelOf(::MetadataViewModel)
    viewModelOf(::PlaylistViewModel)
    viewModelOf(::QuickPlayViewModel)
}