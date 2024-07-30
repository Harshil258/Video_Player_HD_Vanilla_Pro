package com.vanillavideoplayer.videoplayer.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.vanillavideoplayer.videoplayer.core.ui.R

val NunitoLight = FontFamily(
    Font(R.font.n_light, FontWeight.Bold)
)
val NunitoRegular = FontFamily(
    Font(R.font.n_regular, FontWeight.Bold)
)
val NunitoBold = FontFamily(
    Font(R.font.n_bold, FontWeight.Bold)
)

var typographytemo = Typography()

val Typography = Typography(

    displayLarge = typographytemo.displayLarge.copy(fontFamily = NunitoBold),
    displayMedium = typographytemo.displayMedium.copy(fontFamily = NunitoBold),
    displaySmall = typographytemo.displaySmall.copy(fontFamily = NunitoBold),
    headlineLarge = typographytemo.headlineLarge.copy(fontFamily = NunitoBold),
    headlineMedium = typographytemo.headlineMedium.copy(fontFamily = NunitoBold),
    headlineSmall = typographytemo.headlineSmall.copy(fontFamily = NunitoRegular),
    titleLarge = typographytemo.titleLarge.copy(fontFamily = NunitoBold),
    titleMedium = typographytemo.titleMedium.copy(fontFamily = NunitoBold),
    titleSmall = typographytemo.titleSmall.copy(fontFamily = NunitoBold),
    bodyLarge = typographytemo.bodyLarge.copy(fontFamily = NunitoRegular),
    bodyMedium = typographytemo.bodyMedium.copy(fontFamily = NunitoRegular),
    bodySmall = typographytemo.bodySmall.copy(fontFamily = NunitoRegular),
    labelLarge = typographytemo.labelLarge.copy(fontFamily = NunitoBold),
    labelMedium = typographytemo.labelMedium.copy(fontFamily = NunitoRegular),
    labelSmall = typographytemo.labelSmall.copy(fontFamily = NunitoRegular),

    )

