package com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.SortByEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.SortOrderEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R

@Composable
fun SortOrderEnum.name(sortByEnum: SortByEnum): String {
    val res = when (sortByEnum) {
        SortByEnum.TITLE_SORT,
        SortByEnum.PATH_SORT -> when (this) {
            SortOrderEnum.SORT_ASCENDING -> R.string.a_z
            SortOrderEnum.SORT_DESCENDING -> R.string.z_a
        }

        SortByEnum.LENGTH_SORT -> when (this) {
            SortOrderEnum.SORT_ASCENDING -> R.string.shortest
            SortOrderEnum.SORT_DESCENDING -> R.string.longest
        }

        SortByEnum.SIZE_SORT -> when (this) {
            SortOrderEnum.SORT_ASCENDING -> R.string.smallest
            SortOrderEnum.SORT_DESCENDING -> R.string.largest
        }

        SortByEnum.DATE_SORT -> when (this) {
            SortOrderEnum.SORT_ASCENDING -> R.string.oldest
            SortOrderEnum.SORT_DESCENDING -> R.string.newest
        }
    }

    return stringResource(res)
}
