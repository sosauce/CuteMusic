package com.sosauce.cutemusic.di

import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.domain.repository.MediaStoreHelperImpl
import com.sosauce.cutemusic.domain.repository.SafManager
import com.sosauce.cutemusic.ui.screens.metadata.MetadataViewModel
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<MediaStoreHelper> {
        MediaStoreHelperImpl(androidContext())
    }

    single {
        SafManager(androidContext())
    }

    viewModel {
        PostViewModel(get(), get())
    }
    viewModel {
        MusicViewModel(androidApplication(), get(), get())
    }
    viewModel {
        MetadataViewModel(androidApplication())
    }
}