package com.sosauce.cutemusic.di

import androidx.room.Room
import com.sosauce.cutemusic.domain.blacklist.BlacklistedDatabase
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        MediaStoreHelper(androidContext())
    }
    single {
        Room.databaseBuilder(
            context = androidApplication(),
            klass = BlacklistedDatabase::class.java,
            name = "blacklistedfolders.db"
        ).build().dao
    }
    viewModel {
        PostViewModel(get(), get())
    }
}