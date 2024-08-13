package com.sosauce.cutemusic.ui.shared_components

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
import com.sosauce.cutemusic.ui.theme.GlobalFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuteSearchbar(
    musics: List<MediaItem>,
    onNavigate: (Screen) -> Unit,
    onClick: (String) -> Unit,
    
) {

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val filteredList by remember(query) {
        derivedStateOf {
            if (query.isEmpty()) {
                emptyList()
            } else {
                musics.filter { it.mediaMetadata.title.toString().contains(query, ignoreCase = true) }
            }
        }
    }


        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.search),
                    fontFamily = GlobalFont
                ) },
            modifier = if (active) {
                Modifier.fillMaxWidth()
            } else {
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            },
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
                        contentDescription = stringResource(id = R.string.search),

                        )
                }
            },
            trailingIcon = {
                if (active) {
                    IconButton(
                        onClick = { if (query.isNotEmpty()) query = "" }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(id = R.string.close)
                        )
                    }
                } else {
                    Row {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = stringResource(id = R.string.more),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        IconButton(
                            onClick = { onNavigate(Screen.Settings) }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = stringResource(id = R.string.settings)
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
                    itemsIndexed(
                        items = filteredList,
                        key = { _, item -> item.mediaId }
                    ) { index, _ ->
                        val music = filteredList[index]
                        MusicListItem(
                            music = music,
                            onNavigate = { onNavigate(Screen.MetadataEditor(music.mediaId)) },
                            onShortClick = { onClick(it) },
                            
                        )
                    }
            }
        }
}