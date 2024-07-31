package com.vanillavideoplayer.hd.videoplayer.pro.settings.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SubtitlePrefs(
    md: Modifier = Modifier,
    string: String,
    viewPadding: PaddingValues = PaddingValues(start = 18.dp, top = 24.dp, bottom = 12.dp),
    mtColor: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text = string,
        modifier = md
            .fillMaxWidth()
            .padding(viewPadding),
        color = mtColor,
        style = MaterialTheme.typography.labelLarge
    )
}
