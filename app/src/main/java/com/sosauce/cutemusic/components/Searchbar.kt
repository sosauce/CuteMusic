@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.screens.MusicListItem
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun CuteSearchbar(
    viewModel: MusicViewModel,
    musics: List<Music>,
    onNavigate: () -> Unit
) {

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }


    SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(text = "Search", fontFamily = GlobalFont) },
            modifier = if (active) { Modifier.fillMaxWidth() } else { Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp) },
            leadingIcon = {
                if (active) {
                    IconButton(onClick = {
                        active = false
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
                if (active) {
                    IconButton(
                        onClick = { if (query.isNotEmpty()) query = ""}
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close"
                        )
                    }
                } else {
                    Row {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "More",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        IconButton(
                            onClick = { onNavigate() }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Close"
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .width(180.dp)
                                .background(color = MaterialTheme.colorScheme.surface)
                        ) {
                            SortRadioButtons()
                        }
                    }
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

                    itemsIndexed(filteredMusics) {index, item ->
                        val selectedItems = remember { mutableListOf<Int>() }
                        val music = filteredMusics[index]
                        val isSelected = selectedItems.contains(index)
                        MusicListItem(
                            music,
                            { viewModel.play(music.uri) },
                            { selectedItems.add(index) },
                            isSelected = isSelected
                        )
                    }
                }

            }
        }
}