package com.sosauce.cutemusic.ui.screens.blacklisted.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.domain.model.Folder
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun AllFoldersBottomSheet(
    folders: List<Folder>,
    onClick: (path: String) -> Unit,
) {

    LazyColumn {
        itemsIndexed(
            items = folders,
            key = { _, folder -> folder.path }
        ) { index, folder ->
            FoldersLayout(
                folder = folder,
                onClick = { path ->
                    onClick(path)
                },
                topDp = if (index == 0) 24.dp else 4.dp,
                bottomDp = if (index == folders.size - 1) 24.dp else 4.dp
            )
        }
    }
}


@Composable
private fun FoldersLayout(
    folder: Folder,
    onClick: (path: String) -> Unit,
    topDp: Dp,
    bottomDp: Dp,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                alpha = 0.5f
            )
        ),
        modifier = Modifier.padding(5.dp),
        shape = RoundedCornerShape(
            topStart = topDp,
            topEnd = topDp,
            bottomStart = bottomDp,
            bottomEnd = bottomDp
        ),
        onClick = { onClick(folder.path) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(33.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {
                CuteText(
                    text = folder.name
                )
                CuteText(
                    text = folder.path,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }
    }
}