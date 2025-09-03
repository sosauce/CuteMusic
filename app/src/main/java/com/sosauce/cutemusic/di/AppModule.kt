package com.sosauce.cutemusic.di

import androidx.room.Room
import com.sosauce.cutemusic.data.playlist.PlaylistDatabase
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.domain.repository.MediaStoreHelperImpl
import com.sosauce.cutemusic.domain.repository.SafManager
import com.sosauce.cutemusic.presentation.screens.metadata.MetadataViewModel
import com.sosauce.cutemusic.presentation.screens.playlists.PlaylistViewModel
import com.sosauce.cutemusic.presentation.screens.quickplay.QuickPlayViewModel
import com.sosauce.cutemusic.presentation.shared_components.MusicViewModel
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