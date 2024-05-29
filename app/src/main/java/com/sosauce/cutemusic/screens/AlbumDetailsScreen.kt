@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.getMusicArt
import com.sosauce.cutemusic.components.BottomSheetContent
import com.sosauce.cutemusic.logic.imageRequester
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun SharedTransitionScope.AlbumDetailsScreen(
    album: Album,
    viewModel: MusicViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onPopBackStack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val context = LocalContext.current
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = album.name,
                            fontFamily = GlobalFont
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onPopBackStack() }) {
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
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = imageRequester(
                                img = album.albumArt,
                                context = context
                            ),
                            contentDescription = "Album Art",
                            modifier = Modifier
                                .aspectRatio(1 / 1f)
                                .padding(17.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .sharedElement(
                                    state = rememberSharedContentState(key = album.id),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ ->
                                        tween(durationMillis = 1000)
                                    }
                                ),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (album.artist.length >= 18) album.artist.take(18) + "..." + " · " else album.artist + " · ",
                                fontFamily = GlobalFont,
                                fontSize = 22.sp
                            )
                            Text(
                                text = "${album.numberOfSongs} ${if (album.numberOfSongs <= 1) "song" else "songs"}",
                                fontFamily = GlobalFont,
                                fontSize = 22.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    HorizontalDivider()
                    LazyColumn {
                        itemsIndexed(album.songs) { _, music ->
                            AlbumSong(music = music, onShortClick = { viewModel.play(music.uri) })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumSong(
    music: Music,
    onShortClick: (Uri) -> Unit
) {

    val sheetState = rememberModalBottomSheetState(false)
    val context = LocalContext.current
    var isSheetOpen by remember { mutableStateOf(false) }
    var art: ByteArray? by remember { mutableStateOf(byteArrayOf()) }

    LaunchedEffect(music.uri) {
        art = getMusicArt(context, music)
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false }
        ) {
            BottomSheetContent(music)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShortClick(music.uri) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    text = if (music.title.length >= 25) music.title.take(25) + "..." else music.title,
                    fontFamily = GlobalFont,
                    maxLines = 1
                )
                Text(
                    text = music.artist,
                    fontFamily = GlobalFont
                )
            }
        }
        IconButton(onClick = { isSheetOpen = true }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = null
            )
        }
    }
}
