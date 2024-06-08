package com.sosauce.cutemusic.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.components.AboutCard
import com.sosauce.cutemusic.components.SwipeSwitch
import com.sosauce.cutemusic.components.ThemeManagement
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme

@Composable
fun SettingsScreen(
	onPopBackStack: () -> Unit,
	onNavigate: () -> Unit
) {
	Scaffold(
		topBar = {
			AppBar(
				title = stringResource(id = R.string.settings),
				showBackArrow = true,
				showMenuIcon = false,
				onPopBackStack = onPopBackStack,
				onNavigate = onNavigate
			)
		},
	) { values ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(values),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			AboutCard()
			ThemeManagement()
			SwipeSwitch()
		}
	}
}

@Preview
@Composable
private fun SettingsScreenPreview() = CuteMusicTheme {
	SettingsScreen(onPopBackStack = {}, onNavigate = {})
}
