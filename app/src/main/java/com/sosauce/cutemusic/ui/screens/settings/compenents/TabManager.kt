package com.sosauce.cutemusic.ui.screens.settings.compenents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberShowAlbumsTab
import com.sosauce.cutemusic.data.datastore.rememberShowArtistsTab
import com.sosauce.cutemusic.data.datastore.rememberShowFoldersTab
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun TabManager(
    onDismissRequest: () -> Unit,
    showAlbumsTab: Boolean,
    showArtistsTab: Boolean,
    showFoldersTab: Boolean
) {


    // These are here cuz I'm lazy having to create 3 parameters
    var showAlbumsTab2 by rememberShowAlbumsTab()
    var showArtistsTab2 by rememberShowArtistsTab()
    var showFoldersTab2 by rememberShowFoldersTab()

    val tabsAndShownState = remember(showAlbumsTab, showFoldersTab, showArtistsTab) {
        mapOf(
            "Albums" to showAlbumsTab,
            "Artists" to showArtistsTab,
            "Folders" to showFoldersTab,
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                CuteText(stringResource(R.string.okay))
            }
        },
        text = {
            Column {
                tabsAndShownState.forEach { (tabName, isShown) ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isShown,
                            onCheckedChange = {
                                when (tabName) {
                                    "Albums" -> showAlbumsTab2 = !showAlbumsTab
                                    "Artists" -> showArtistsTab2 = !showArtistsTab
                                    "Folders" -> showFoldersTab2 = !showFoldersTab
                                }
                            }
                        )
                        CuteText(tabName)
                    }

                }
            }
        },
        title = { CuteText(stringResource(R.string.manage_shown_tabs)) }
    )
}