package com.sosauce.cutemusic.di

import androidx.room.Room
import com.sosauce.cutemusic.data.AbstractTracksScanner
import com.sosauce.cutemusic.data.LyricsParser
import com.sosauce.cutemusic.data.playlist.MIGRATION_1_2
import com.sosauce.cutemusic.data.playlist.PlaylistDatabase
import com.sosauce.cutemusic.domain.repository.AlbumsRepository
import com.sosauce.cutemusic.domain.repository.ArtistsRepository
import com.sosauce.cutemusic.domain.repository.FoldersRepository
import com.sosauce.cutemusic.domain.repository.PlaylistsRepository
import com.sosauce.cutemusic.domain.repository.SafManager
import com.sosauce.cutemusic.presentation.screens.album.AlbumDetailsViewModel
import com.sosauce.cutemusic.presentation.screens.album.AlbumsViewModel
import com.sosauce.cutemusic.presentation.screens.artist.ArtistDetailsViewModel
import com.sosauce.cutemusic.presentation.screens.artist.ArtistsViewModel
import com.sosauce.cutemusic.presentation.screens.main.MainViewModel
import com.sosauce.cutemusic.presentation.screens.metadata.MetadataViewModel
import com.sosauce.cutemusic.presentation.screens.playlists.PlaylistDetailsViewModel
import com.sosauce.cutemusic.presentation.screens.playlists.PlaylistViewModel
import com.sosauce.cutemusic.presentation.screens.quickplay.QuickPlayViewModel
import com.sosauce.cutemusic.presentation.screens.settings.FoldersViewModel
import com.sosauce.cutemusic.presentation.screens.settings.SafViewModel
import com.sosauce.cutemusic.presentation.shared_components.MusicViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            context = androidApplication(),
            klass = PlaylistDatabase::class.java,
            name = "playlist.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
            .dao
    }



    singleOf(::AbstractTracksScanner)
    singleOf(::LyricsParser)
    singleOf(::FoldersRepository)
    singleOf(::SafManager)
    singleOf(::AlbumsRepository)
    singleOf(::ArtistsRepository)
    singleOf(::PlaylistsRepository)
    viewModelOf(::MusicViewModel)
    viewModelOf(::MetadataViewModel)
    viewModelOf(::PlaylistViewModel)
    viewModelOf(::PlaylistDetailsViewModel)
    viewModelOf(::QuickPlayViewModel)
    viewModelOf(::ArtistsViewModel)
    viewModelOf(::ArtistDetailsViewModel)
    viewModelOf(::AlbumsViewModel)
    viewModelOf(::AlbumDetailsViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::FoldersViewModel)
    viewModelOf(::SafViewModel)
}