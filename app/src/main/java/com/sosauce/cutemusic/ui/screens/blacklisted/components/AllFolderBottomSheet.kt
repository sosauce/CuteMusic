package com.sosauce.cutemusic.ui.screens.blacklisted.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.domain.model.Folder
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun AllFoldersBottomSheet(
    folders: List<Folder>,
    onClick: (path: String) -> Unit,
) {

    LazyColumn {
        items(
            items = folders,
            key = { it.path }
        ) {
            FoldersLayout(
                folder = it,
                onClick = { path ->
                    onClick(path)
                }
            )
        }
    }
}


@Composable
private fun FoldersLayout(
    folder: Folder,
    onClick: (path: String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                alpha = 0.5f
            )
        ),
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick(folder.path) }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
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