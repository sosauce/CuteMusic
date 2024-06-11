package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.dataStore
import com.sosauce.cutemusic.data.datastore.rememberSortASC
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun SortRadioButtons() {
    val options = listOf("Ascending", "Descending")
    val context = LocalContext.current
    val dataStore: DataStore<Preferences> = context.dataStore
    var sort by rememberSortASC()

    Column(
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { sort = !sort }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = sort,
                onClick = null
            )
            Text(
                text = stringResource(id = R.string.ascending),
                modifier = Modifier.padding(start = 15.dp),
                fontFamily = GlobalFont
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { sort = !sort }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = !sort,
                onClick = null
            )
            Text(
                text = stringResource(id = R.string.descending),
                modifier = Modifier.padding(start = 15.dp),
                fontFamily = GlobalFont
            )
        }
    }
}