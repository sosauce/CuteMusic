package com.sosauce.cutemusic.ui.screens.blacklisted

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.domain.blacklist.BlackEvent
import com.sosauce.cutemusic.domain.blacklist.BlackState
import com.sosauce.cutemusic.domain.model.Folder
import com.sosauce.cutemusic.ui.screens.blacklisted.components.AllFoldersBottomSheet
import com.sosauce.cutemusic.ui.shared_components.AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlacklistedScreen(
    state: BlackState,
    navController: NavController,
    folders: List<Folder>,
    onEvents: (BlackEvent) -> Unit,
    blacklistedFolderNames: Set<String>
) {
    var isSheetOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSheetOpen = false },
            modifier = Modifier.fillMaxHeight()
        ) {
            AllFoldersBottomSheet(
                folders = folders,
                onClick = { name, path ->
                    if (path in blacklistedFolderNames) {
                        Toast.makeText(context, context.resources.getText(R.string.alrdy_blacklisted), Toast.LENGTH_SHORT).show()
                    } else {
                        state.name.value = name
                        state.path.value = path

                        onEvents(BlackEvent.AddBlack(
                            name = state.name.value,
                            path = state.path.value
                        ))
                        isSheetOpen = false
                    }
                }
            )
        }
    }

    BlacklistedScreenContent(
        onAddFolder = { isSheetOpen = true },
        onPopBackStack = navController::navigateUp,
        onEvents = onEvents,
        state = state
    )
}

@Composable
private fun BlacklistedScreenContent(
    state: BlackState,
    onAddFolder: () -> Unit,
    onPopBackStack: () -> Unit,
    onEvents: (BlackEvent) -> Unit
) {
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.blacklisted_folders),
                showBackArrow = true,
                showMenuIcon = false,
                onPopBackStack = { onPopBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddFolder() }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { values ->
        LazyColumn(
            modifier = Modifier.padding(values)
        ) {
            items(state.blacklistedFolders.size) { index ->
                BlackFolderItem(
                    state = state,
                    index = index,
                    onEvents = onEvents
                )
            }
        }
    }
}


@Composable
private fun BlackFolderItem(
    state: BlackState,
    index: Int,
    onEvents: (BlackEvent) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(
                start = 13.dp,
                end = 13.dp,
                bottom = 8.dp
            )
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
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
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = state.blacklistedFolders[index].name,
                    fontSize = 18.sp
                )
                Text(
                    text = state.blacklistedFolders[index].path,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
            IconButton(
                onClick = { onEvents(BlackEvent.DeleteBlack(state.blacklistedFolders[index])) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

}