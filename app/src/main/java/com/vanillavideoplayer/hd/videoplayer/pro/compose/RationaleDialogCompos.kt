package com.vanillavideoplayer.hd.videoplayer.pro.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R

@Composable
fun RationaleDialogCompos(
    text: String,
    modifier: Modifier = Modifier,
    onConfirmButtonClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.permission_request)
            )
        },
        text = {
            Text(text = text)
        },
        confirmButton = {
            Button(onClick = onConfirmButtonClick) {
                Text(stringResource(R.string.grant_permission))
            }
        }
    )
}
