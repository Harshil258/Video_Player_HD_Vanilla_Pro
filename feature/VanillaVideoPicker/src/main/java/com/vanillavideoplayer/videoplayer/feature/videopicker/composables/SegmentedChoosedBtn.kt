package com.vanillavideoplayer.videoplayer.feature.videopicker.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedChoosedBtnUi(
    lbOne: @Composable RowScope.() -> Unit, lbTwo: @Composable RowScope.() -> Unit, modifier: Modifier = Modifier,
    icOne: @Composable (() -> Unit)? = null,
    icTwo: @Composable (() -> Unit)? = null,
    choosedChip: SelectedChipEnum,
    mtColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    choosedColor: Color = MaterialTheme.colorScheme.primary,
    onClick: (SelectedChipEnum) -> Unit = { },
    mediaPaddingVal: PaddingValues = PaddingValues(10.dp)
) {
    val outlineShapeOne = RoundedCornerShape(
        topStart = 8.dp,
        bottomStart = 8.dp,
        topEnd = 0.dp,
        bottomEnd = 0.dp
    )
    val outlineShapeTwo = RoundedCornerShape(
        topStart = 0.dp,
        bottomStart = 0.dp,
        topEnd = 8.dp,
        bottomEnd = 8.dp
    )

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .selectableGroup()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 1.dp,
                    color = if (choosedChip == SelectedChipEnum.CHIP_ONE) choosedColor else mtColor,
                    shape = outlineShapeOne
                )
                .clip(outlineShapeOne)
                .selectable(
                    selected = choosedChip == SelectedChipEnum.CHIP_ONE,
                    onClick = { onClick(SelectedChipEnum.CHIP_ONE) },
                    role = Role.RadioButton
                )
                .padding(mediaPaddingVal)
        ) {
            icOne?.invoke()
            if (icOne != null) Spacer(modifier = Modifier.width(8.dp))
            lbOne()
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 1.dp,
                    color = if (choosedChip == SelectedChipEnum.CHIP_TWO) choosedColor else mtColor,
                    shape = outlineShapeTwo
                )
                .clip(outlineShapeTwo)
                .selectable(
                    selected = choosedChip == SelectedChipEnum.CHIP_TWO,
                    onClick = { onClick(SelectedChipEnum.CHIP_TWO) },
                    role = Role.RadioButton
                )
                .padding(mediaPaddingVal)
        ) {
            icTwo?.invoke()
            if (icTwo != null) Spacer(modifier = Modifier.width(8.dp))
            lbTwo()
        }
    }
}
