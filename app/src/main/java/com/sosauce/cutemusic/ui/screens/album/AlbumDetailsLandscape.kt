package com.sosauce.cutemusic.ui.screens.album

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun AlbumDetailsLandscape(
    album: Album,
    onNavigateUp: () -> Unit,
    postViewModel: PostViewModel,
    viewModel: MusicViewModel
) {

    val albumSongs by remember { mutableStateOf(postViewModel.albumSongs) }

        Scaffold(
            modifier = Modifier.padding(45.dp)
        ) { values ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                IconButton(
                                    onClick = { onNavigateUp() }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                                Text(
                                    text = album.name + " · ",
                                    fontFamily = GlobalFont,
                                    fontSize = 22.sp
                                )
                                Text(
                                    text = album.artist + " · ",
                                    fontFamily = GlobalFont,
                                    fontSize = 22.sp
                                )
//                                Text(
//                                    text = albumSongs.size.toString() + " songs",
//                                    fontFamily = GlobalFont,
//                                    fontSize = 22.sp
//                                )
                            }
                            Text(
                                text = albumSongs.size.toString() + " songs",
                                fontFamily = GlobalFont,
                                fontSize = 22.sp
                            )

                    }
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AsyncImage(
                            model = ImageUtils.getAlbumArt(album.id),
                            contentDescription = "Artwork",
                            modifier = Modifier
                                .size(280.dp)
                                .clip(RoundedCornerShape(10)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        LazyColumn {
                            items(albumSongs, key = { it.id }) {  music ->
                                AlbumSong(music = music, onShortClick = { viewModel.itemClicked(music.uri) })
                            }
                        }
                    }
                }
        }

}