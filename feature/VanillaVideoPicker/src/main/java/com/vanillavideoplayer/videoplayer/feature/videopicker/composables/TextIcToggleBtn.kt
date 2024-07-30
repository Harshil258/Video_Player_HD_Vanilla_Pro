package com.vanillavideoplayer.videoplayer.feature.videopicker.composables

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vanillavideoplayer.videoplayer.core.ui.theme.VideoPlayerTheme

@Preview
@Composable
fun TextIcToggleBtnPrev() {
    VideoPlayerTheme {
        Surface {
            TextIcToggleBtn(
                btnText = "Text",
                icVect = Icons.Rounded.Search
            )
        }
    }
}

@Composable
fun TextIcToggleBtn(
    btnText: String,
    icVect: ImageVector,
    md: Modifier = Modifier,
    boolSelected: Boolean = false,
    interactionMutableSource: MutableInteractionSource = MutableInteractionSource(),
    indicationVal: Indication? = null,
    onClick: (Boolean) -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = md
            .clip(MaterialTheme.shapes.small)
            .clickable(
                interactionSource = interactionMutableSource,
                indication = indicationVal,
                onClick = { onClick(!boolSelected) }
            )
            .padding(10.dp)

    ) {
        FilledIconToggleButton(
            checked = boolSelected,
            onCheckedChange = onClick,
            interactionSource = interactionMutableSource
        ) {
            Icon(imageVector = icVect, contentDescription = btnText)
        }
        Text(
            text = btnText,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

