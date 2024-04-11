@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.screens.MusicListItem

@Composable
fun CuteSearchbar(
    onShowSearchbar: () -> Unit,
    viewModel: MusicViewModel,
    musics: List<Music>
) {

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text(text = "Search") },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            if (active) {
                IconButton(onClick = {
                    active = false
                    onShowSearchbar()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",

                    )
            }
        },
        trailingIcon = {
            IconButton(
                onClick = { if (query.isNotEmpty()) query = "" else onShowSearchbar()}
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Close"
                )
            }
        }

    ) {
        LazyColumn {
            fun filterMusics(musics: List<Music>, query: String): List<Music> {
                return musics.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }

            val filteredMusics = if (query.isNotEmpty()) filterMusics(musics, query) else null

            if (filteredMusics != null) {
                items(filteredMusics.size) { index ->
                    val music = filteredMusics[index]
                    MusicListItem(music) { viewModel.play(music.uri) }
                }
            }

        }
    }
}