@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.main.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.screens.main.Category
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import java.io.File

@Composable
fun FolderHeader(
    category: Category,
    isHidden: Boolean,
    onToggleVisibility: () -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,

    ) {
    val iconRotation by animateFloatAsState(
        targetValue = if (isHidden) 90f else 0f
    )

    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onToggleVisibility() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onHandlePlayerAction(
                        PlayerActions.Play(
                            index = 0,
                            tracks = category.tracks
                        )
                    )
                },
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(
                    painter = painterResource(R.drawable.widget_play),
                    contentDescription = null
                )
            }
            Icon(
                painter = painterResource(R.drawable.folder_rounded),
                contentDescription = null
            )
            Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
            Text(
                text = File(category.name).name,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = null,
                modifier = Modifier.rotate(iconRotation)
            )
        }
    }
}