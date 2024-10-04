@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

package com.sosauce.cutemusic.ui.screens.artist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.album.AlbumCard
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel

@Composable
fun ArtistDetails(
    artist: Artist,
    navController: NavController,
    viewModel: MusicViewModel,
    postViewModel: PostViewModel,
    onNavigate: (Screen) -> Unit,
) {

    val artistSongs by remember { mutableStateOf(postViewModel.artistSongs) }
    val artistAlbums by remember { mutableStateOf(postViewModel.artistAlbums) }

    if (rememberIsLandscape()) {
        ArtistDetailsLandscape(
            onNavigateUp = navController::navigateUp,
            artistAlbums = artistAlbums,
            artistSongs = artistSongs,
            onClickPlay = { viewModel.itemClicked(it, listOf()) },
            onNavigate = { navController.navigate(it) },
            chargePVMAlbumSongs = { postViewModel.albumSongs(it) },
            artist = artist,
            currentMusicUri = viewModel.currentMusicUri
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CuteText(
                                text = artist.name + " · ",
                                fontSize = 20.sp
                            )
                            CuteText(
                                text = "${artistSongs.size} ${if (artistSongs.size <= 1) "song" else "songs"}",

                                fontSize = 20.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = navController::navigateUp
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back arrow"
                            )
                        }
                    }
                )
            }
        ) { values ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
            ) {
                Column {
                    LazyRow {
                        items(items = artistAlbums, key = { it.id }) { album ->
                            AlbumCard(
                                album = album,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(15.dp))
                                    .clickable {
                                        postViewModel.albumSongs(album.name)
                                        onNavigate(Screen.AlbumsDetails(album.id))
                                    }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn {
                        items(artistSongs) { music ->
                            MusicListItem(
                                music = music,
                                onShortClick = { viewModel.itemClicked(it, listOf()) },
                                currentMusicUri = viewModel.currentMusicUri
                            )
                        }
                    }

                }
            }
        }
    }
}