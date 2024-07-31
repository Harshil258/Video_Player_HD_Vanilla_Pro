package com.vanillavideoplayer.hd.videoplayer.pro.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationPrefData(
    val sortByEnum: SortByEnum = SortByEnum.DATE_SORT,
    val sortOrderEnum: SortOrderEnum = SortOrderEnum.SORT_DESCENDING,
    val groupVideosByFolder: Boolean = false,
    val themeConfigEnum: ThemeConfigEnum = ThemeConfigEnum.THEME_SYSTEM,
    val useHighContrastDarkTheme: Boolean = true,
    val useDynamicColors: Boolean = true,
    val excludeFolders: List<String> = emptyList(),
    val showVideoTheme: VideoTheme = VideoTheme.THUMBNAIL_MEDIUM,

    // View preferences
    val thumbnailSize: Size = Size.LARGE,

    // Fields
    val showDurationField: Boolean = true,
    val showExtensionField: Boolean = true,
    val showPathField: Boolean = true,
    val showResolutionField: Boolean = true,
    val showSizeField: Boolean = true,
    val showThumbnailField: Boolean = true,
    val isItemPurchased: Boolean = false
)

enum class Size {
    COMPACT, MEDIUM, LARGE
}
