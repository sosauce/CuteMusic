@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.blacklisted

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAllBlacklistedFolders
import com.sosauce.cutemusic.domain.model.Folder
import com.sosauce.cutemusic.ui.shared_components.AppBar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import java.io.File

@Composable
fun BlacklistedScreen(
    navController: NavController,
    folders: List<Folder>,
) {

    var blacklistedFolders by rememberAllBlacklistedFolders()

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.blacklisted_folders),
                showBackArrow = true,
                onPopBackStack = navController::navigateUp
            )
        }
    ) { values ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
        ) {
            folders.sortedBy { it.name }
                .groupBy { it.path in blacklistedFolders }
                .toSortedMap(compareByDescending { it })
                .forEach { (isBlacklisted, allFolders) ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 34.dp,
                                    vertical = 8.dp
                                )
                        ) {
                            CuteText(
                                text = if (isBlacklisted) stringResource(R.string.blacklisted) else stringResource(
                                    R.string.not_blacklisted
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    itemsIndexed(
                        items = allFolders,
                        key = { _, folder -> folder.path }
                    ) { index, folder ->
                        val topDp by animateDpAsState(
                            targetValue = if (index == 0) 24.dp else 4.dp,
                            label = "Top Dp"
                        )
                        val bottomDp by animateDpAsState(
                            targetValue = if (index == allFolders.size - 1) 24.dp else 4.dp,
                            label = "Bottom Dp"
                        )

                        FolderItem(
                            folder = folder.path,
                            topDp = topDp,
                            bottomDp = bottomDp,
                            modifier = Modifier.animateItem(),
                            actionButton = {
                                if (isBlacklisted) {
                                    IconButton(
                                        onClick = {
                                            blacklistedFolders =
                                                blacklistedFolders.toMutableSet().apply {
                                                    remove(folder.path)
                                                }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.trash_rounded_filled),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            blacklistedFolders =
                                                blacklistedFolders.toMutableSet().apply {
                                                    add(folder.path)
                                                }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
        }
    }
}


@Composable
fun FolderItem(
    modifier: Modifier = Modifier,
    folder: String,
    topDp: Dp,
    bottomDp: Dp,
    actionButton: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(
            topStart = topDp,
            topEnd = topDp,
            bottomStart = bottomDp,
            bottomEnd = bottomDp
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.folder_rounded),
                contentDescription = null,
                modifier = Modifier.size(33.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                CuteText(
                    text = File(folder).name,
                    fontSize = 18.sp
                )
                CuteText(
                    text = folder,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.basicMarquee()
                )
            }
            actionButton()
        }
    }
}