package com.vanillavideoplayer.videoplayer.core.ui.designsystem

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

object NavAnim {

    val slideIn = slideInHorizontally(initialOffsetX = { it }) + fadeIn(tween(300))
    val slideOut = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(300))
}
