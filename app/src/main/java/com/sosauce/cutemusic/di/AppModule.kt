package com.sosauce.cutemusic.di

import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        MediaStoreHelper(androidContext())
    }
    viewModel {
        PostViewModel(get())
    }
}