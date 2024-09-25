package com.sosauce.cutemusic.di

import android.content.ComponentName
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import com.sosauce.cutemusic.main.PlaybackService
import com.sosauce.cutemusic.ui.screens.metadata.MetadataViewModel
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        MediaStoreHelper(androidContext())
    }
    single {
        MediaController.Builder(
            androidContext(),
            SessionToken(
                androidContext(),
                ComponentName(androidContext(), PlaybackService::class.java)
            )
        ).buildAsync()
    }
    viewModel {
        PostViewModel(get())
    }
    viewModel {
        MusicViewModel(get())
    }
    viewModel {
        MetadataViewModel(androidApplication())
    }
}