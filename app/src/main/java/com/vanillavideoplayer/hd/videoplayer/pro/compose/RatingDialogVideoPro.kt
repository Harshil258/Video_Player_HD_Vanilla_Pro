package com.vanillavideoplayer.hd.videoplayer.pro.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.videoplayer.core.ui.preview.LightDarkPrev
import com.vanillavideoplayer.videoplayer.core.ui.theme.NunitoBold
import com.vanillavideoplayer.videoplayer.core.ui.theme.VideoPlayerTheme

@LightDarkPrev
@Composable
fun RatingDialogVideoPro(
    onDismiss: () -> Unit = {},
    onRate: (Int) -> Unit = {}
) {
    VideoPlayerTheme {
        val rate = remember {
            mutableIntStateOf(5)
        }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Rate Our App!",
                    fontWeight = FontWeight.Bold,
                    fontFamily = NunitoBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Text(
                        "Please take a moment to rate our app.", modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { index ->
                            Icon(
                                imageVector = if (index <= rate.intValue) VanillaIcons.StarFilled else VanillaIcons.StarOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable {
                                        rate.intValue = index
                                    }
                                    .weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onRate(rate.intValue) }
                ) {
                    Text("Rate")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

