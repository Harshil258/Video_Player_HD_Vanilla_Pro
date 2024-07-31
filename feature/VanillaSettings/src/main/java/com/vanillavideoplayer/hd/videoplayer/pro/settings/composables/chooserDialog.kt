package com.vanillavideoplayer.hd.videoplayer.pro.settings.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaCancelButton
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerDialog

@Composable
fun chooserDialog(
    header: String,
    onDismissDialog: () -> Unit,
    optionsLazyList: LazyListScope.() -> Unit
) {
    VanillaPlayerDialog(
        onDismissRequest = onDismissDialog,
        title = {
            Text(text = header)
        },
        content = {
            HorizontalDivider()
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.selectableGroup()
            ) { optionsLazyList() }
            HorizontalDivider()
        },
        dismissButton = { VanillaCancelButton(onClick = onDismissDialog) },
        confirmButton = { }
    )
}
