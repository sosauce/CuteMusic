package com.sosauce.cutemusic.ui.screens.blacklisted.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.domain.model.Folder

@Composable
fun AllFoldersBottomSheet(
    folders: List<Folder>,
    onClick: (name: String, path: String) -> Unit,
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
    ) {
        items(
            items = folders,
            key = { it.path }
            ) {
            FoldersLayout(
                folder = it,
                onClick = { name, path ->
                    onClick(path, name)
                }
            )
        }
    }
}


@Composable
private fun FoldersLayout(
    folder: Folder,
    onClick: (name: String, path: String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .clickable { onClick(folder.path, folder.name) }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = folder.name
            )
        }
    }
}