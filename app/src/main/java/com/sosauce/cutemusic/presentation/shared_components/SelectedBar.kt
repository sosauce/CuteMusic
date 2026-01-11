@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.presentation.multiselect.MultiSelectState
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding

@Composable
fun <T> SelectedBar(
    modifier: Modifier = Modifier,
    items: List<T>,
    multiSelectState: MultiSelectState<T>,
    onToggleAll: () -> Unit,
    actions: @Composable (RowScope.() -> Unit)
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth(rememberSearchbarMaxFloatValue())
            .padding(end = rememberSearchbarRightPadding())
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = multiSelectState::clearSelected,
                shapes = ButtonDefaults.shapes()
            ) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(5.dp))
                    Text("${multiSelectState.selectedItems.size}")
                }
            }
            TextButton(
                onClick = onToggleAll,
                shapes = ButtonDefaults.shapes()

            ) {

                val icon =
                    if (items.size == multiSelectState.selectedItems.size) R.drawable.unselect_all else R.drawable.select_all

                Icon(
                    painter = painterResource(icon),
                    contentDescription = null
                )
                Spacer(Modifier.width(5.dp))

                val text =
                    if (items.size == multiSelectState.selectedItems.size) "Unselect all" else "Select all"

                Text(text)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) { actions() }
    }
}