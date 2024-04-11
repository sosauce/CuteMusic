package com.sosauce.cutemusic.logic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sosauce.cutemusic.ui.theme.GlobalFont


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    navController: NavController,
    showBackArrow: Boolean,
    showMenuIcon: Boolean,
    showSearchIcon: Boolean,
    onSearchBarStateChanged: ((Boolean) -> Unit?)?
) {
    var expanded by remember { mutableStateOf(false) }
    var showSearchbar by remember { mutableStateOf(false) }
    val iconsColor = MaterialTheme.colorScheme.onBackground

    LaunchedEffect(showSearchbar) {
        if (onSearchBarStateChanged != null) {
            onSearchBarStateChanged(showSearchbar)
        }
    }


    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = GlobalFont,
                maxLines = 1
            ) },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back arrow"
                    )
                }
            }
        },
        actions = {
                if (showSearchIcon) {
                    IconButton(
                        onClick = { showSearchbar = !showSearchbar }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = iconsColor
                        )
                    }
                }
                if (showMenuIcon) {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = iconsColor
                        )
                    }
                }


            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(180.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Settings", fontFamily = GlobalFont) },
                    onClick = { navController.navigate("SettingsScreen"); expanded = false },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    })
                DropdownMenuItem(
                    text = { Text(text = "About", fontFamily = GlobalFont) },
                    onClick = { navController.navigate("AboutScreen"); expanded = false },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Info, contentDescription = "About"
                        )
                    }
                )

            }
        }
    )
}
