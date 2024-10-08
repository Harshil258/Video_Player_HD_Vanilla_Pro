package com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.composables

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ApplicationPrefData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.SortByEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.SortOrderEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaCancelButton
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaDoneButton
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerDialog
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.horizontalFadingEdge
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.extensions.name

enum class SelectedChipEnum {
    CHIP_ONE, CHIP_TWO
}

@Preview
@Composable
fun QuickSettingsPrevUi() {
    Surface {
        QuickSettingsDialogUi(applicationPrefData = ApplicationPrefData(),
            onDismiss = { },
            onCancel = {},
            updatePreferences = {})
    }
}

@Composable
fun QuickSettingsDialogUi(
    applicationPrefData: ApplicationPrefData,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    updatePreferences: (ApplicationPrefData) -> Unit,
) {
    var pref by remember { mutableStateOf(applicationPrefData) }

    VanillaPlayerDialog(onDismissRequest = onCancel, title = {
        Text(text = stringResource(R.string.quick_settings))
    }, content = {
        HorizontalDivider()
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            DialogSectorHeading(text = stringResource(R.string.sort))
            VidSortOptions(selectedSortByEnum = pref.sortByEnum,
                onOptionSelected = { pref = pref.copy(sortByEnum = it) })
            Spacer(modifier = Modifier.height(8.dp))
            SOSegmentedButton(selectedSortByEnum = pref.sortByEnum,
                selectedSortOrderEnum = pref.sortOrderEnum,
                onOptionSelected = { pref = pref.copy(sortOrderEnum = it) })
            HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

        }
    }, confirmButton = {
        VanillaDoneButton(onClick = {
            updatePreferences(pref)
            onDismiss()
        })
    }, dismissButton = {
        VanillaCancelButton(onClick = onCancel)
    })
}

@Composable
fun SOSegmentedButton(
    selectedSortByEnum: SortByEnum,
    selectedSortOrderEnum: SortOrderEnum,
    onOptionSelected: (SortOrderEnum) -> Unit,
) {
    SegmentedChoosedBtnUi(icOne = {
        Icon(
            imageVector = VanillaIcons.ArrowUpward,
            contentDescription = stringResource(R.string.ascending),
            modifier = Modifier.size(FilterChipDefaults.IconSize)
        )
    },
        lbOne = {
            Text(
                text = SortOrderEnum.SORT_ASCENDING.name(selectedSortByEnum),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        },
        icTwo = {
            Icon(
                imageVector = VanillaIcons.ArrowDownward,
                contentDescription = stringResource(R.string.descending),
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        },
        lbTwo = {
            Text(
                text = SortOrderEnum.SORT_DESCENDING.name(selectedSortByEnum),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        },
        choosedChip = if (selectedSortOrderEnum == SortOrderEnum.SORT_ASCENDING) SelectedChipEnum.CHIP_ONE else SelectedChipEnum.CHIP_TWO,
        onClick = {
            onOptionSelected(
                when (it) {
                    SelectedChipEnum.CHIP_ONE -> SortOrderEnum.SORT_ASCENDING
                    SelectedChipEnum.CHIP_TWO -> SortOrderEnum.SORT_DESCENDING
                }
            )
        })
}


@Composable
private fun DialogSectorHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}


@Composable
private fun VidSortOptions(
    selectedSortByEnum: SortByEnum,
    onOptionSelected: (SortByEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .horizontalFadingEdge(scrollState, 15.dp)
            .horizontalScroll(scrollState)
    ) {
        TextIcToggleBtn(btnText = stringResource(id = R.string.title),
            icVect = VanillaIcons.Title,
            boolSelected = selectedSortByEnum == SortByEnum.TITLE_SORT,
            onClick = { onOptionSelected(SortByEnum.TITLE_SORT) })
        TextIcToggleBtn(btnText = stringResource(id = R.string.duration),
            icVect = VanillaIcons.Length,
            boolSelected = selectedSortByEnum == SortByEnum.LENGTH_SORT,
            onClick = { onOptionSelected(SortByEnum.LENGTH_SORT) })
        TextIcToggleBtn(btnText = stringResource(id = R.string.date),
            icVect = VanillaIcons.Calendar,
            boolSelected = selectedSortByEnum == SortByEnum.DATE_SORT,
            onClick = { onOptionSelected(SortByEnum.DATE_SORT) })
        TextIcToggleBtn(btnText = stringResource(id = R.string.size),
            icVect = VanillaIcons.Size,
            boolSelected = selectedSortByEnum == SortByEnum.SIZE_SORT,
            onClick = { onOptionSelected(SortByEnum.SIZE_SORT) })
        TextIcToggleBtn(btnText = stringResource(id = R.string.location),
            icVect = VanillaIcons.Location,
            boolSelected = selectedSortByEnum == SortByEnum.PATH_SORT,
            onClick = { onOptionSelected(SortByEnum.PATH_SORT) })
    }
}