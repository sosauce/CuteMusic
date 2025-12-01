package com.sosauce.cutemusic.presentation.screens.settings.compenents

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberWhitelistedFolders
import com.sosauce.cutemusic.presentation.screens.settings.FoldersViewModel
import com.sosauce.cutemusic.utils.copyMutate
import org.koin.androidx.compose.koinViewModel

@Composable
fun FoldersView() {
    val folderViewmodel = koinViewModel<FoldersViewModel>()
    val folders by folderViewmodel.folders.collectAsStateWithLifecycle()
    var whitelistedFolders by rememberWhitelistedFolders()
    val (whitelisted, blacklisted) = folders.partition { it.path in whitelistedFolders }


    if (whitelisted.isNotEmpty()) {
        Text(
            text = "Whitelisted",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(
                    horizontal = 34.dp,
                    vertical = 8.dp
                )
        )
    }

    whitelisted.fastForEachIndexed { index,  folder ->
        FolderItem(
            folder = folder.path,
            topDp = if (index == 0) 24.dp else 4.dp,
            bottomDp = if (index == whitelisted.lastIndex) 24.dp else 4.dp,
            actionButton = {
                IconButton(
                    onClick = {
                        whitelistedFolders =
                            whitelistedFolders.copyMutate { remove(folder.path) }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = null
                    )
                }
            }
        )
    }
    if (blacklisted.isNotEmpty()) {
        Text(
            text = stringResource(R.string.blacklisted),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(
                    horizontal = 34.dp,
                    vertical = 8.dp
                )
        )
    }

    blacklisted.fastForEachIndexed { index, folder ->
        FolderItem(
            folder = folder.path,
            topDp = if (index == 0) 24.dp else 4.dp,
            bottomDp = if (index == blacklisted.lastIndex) 24.dp else 4.dp,
            actionButton = {
                IconButton(
                    onClick = {
                        whitelistedFolders =
                            whitelistedFolders.copyMutate { add(folder.path) }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add),
                        contentDescription = null
                    )
                }
            }
        )
    }
}