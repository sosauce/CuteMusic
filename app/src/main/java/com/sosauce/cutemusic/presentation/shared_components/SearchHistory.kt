package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun SearchHistory(
    modifier: Modifier = Modifier,
    onInsertToSearch: (String) -> Unit
) {

    val list = List(5) { it }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        LazyColumn(
            modifier = modifier
        ) {
            items(
                items = list
            ) { item ->
                CuteDropdownMenuItem(
                    onClick = {},
                    text = { CuteText(item.toString()) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { onInsertToSearch(item.toString()) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowOutward,
                                contentDescription = null,
                                modifier = Modifier.rotate(270f)
                            )
                        }
                    }
                )
            }
        }
    }

}