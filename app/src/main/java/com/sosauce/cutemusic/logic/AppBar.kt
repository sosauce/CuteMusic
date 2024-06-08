package com.sosauce.cutemusic.logic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sosauce.cutemusic.components.SortRadioButtons
import com.sosauce.cutemusic.ui.theme.GlobalFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
	title: String,
	showBackArrow: Boolean,
	showMenuIcon: Boolean,
	onPopBackStack: (() -> Unit)? = null,
	onNavigate: () -> Unit
) {
	var expanded by remember { mutableStateOf(false) }
	CenterAlignedTopAppBar(
		title = {
			Text(
				text = title,
				fontFamily = GlobalFont,
				maxLines = 1
			)
		},
		navigationIcon = {
			if (showBackArrow) {
				IconButton(onClick = { onPopBackStack?.invoke() }) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowBack,
						contentDescription = "Back arrow"
					)
				}
			}
		},
		actions = {
			if (showMenuIcon) {
				IconButton(onClick = onNavigate) {
					Icon(
						imageVector = Icons.Outlined.Settings,
						contentDescription = "More",
						tint = MaterialTheme.colorScheme.onBackground
					)
				}
			}

			DropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false },
			) {
				SortRadioButtons()
			}
		}
	)
}
