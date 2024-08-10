package com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.composables

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ApplicationPrefData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.VideoData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.VideoTheme
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.preview.LightDarkPrev
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.theme.VideoPlayerTheme
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.theme.color
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.PlayerViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun VidProgressUi(totalDuration: Int, currentDuration: Int? = 5000) {
    val progCount = remember { mutableFloatStateOf(0.0f) }

    // Calculate the progress as a percentage
    if (currentDuration != null) {
        progCount.floatValue = (currentDuration.toFloat() / totalDuration.toFloat()) * 100
    }
    val progressAnimation by animateFloatAsState(
        targetValue = progCount.floatValue / 100f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing), label = ""
    )
    LinearProgressIndicator(
        progress = { progressAnimation },
        modifier = Modifier.fillMaxWidth(),
    )
}


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalLayoutApi::class, DelicateCoroutinesApi::class)
@Composable
fun VideoItem(
    vidData: VideoData,
    pref: ApplicationPrefData,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel?,
    infoButtonClick: () -> Unit
) {
    val selectedItemColor = if (!isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
    }

    var downSidePadding by remember { mutableStateOf(8.dp) }

    AnimatedVisibility(visible = pref.showVideoTheme == VideoTheme.THUMBNAIL_MEDIUM) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = if (isSelected) selectedItemColor else Color.Transparent
            ),

            leadingContent = {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
//                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                        .width(min(150.dp, LocalConfiguration.current.screenWidthDp.dp * 0.35f))
                        .aspectRatio(16f / 10f)
                ) {
                    Icon(
                        imageVector = VanillaIcons.Video,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceColorAtElevation(100.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(0.5f)
                    )


                    GlideImage(
                        imageModel = { vidData.uriString }, imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop, alignment = Alignment.Center
                        ), modifier = Modifier.fillMaxSize()
                    )

                    DetailsChipUi(
                        text = vidData.formattedDuration,
                        modifier = Modifier
                            .padding(5.dp)
                            .align(Alignment.BottomStart),
                        backgroundColor = Color.Black.copy(alpha = 0.6f),
                        contentColor = Color.White,
                        shape = MaterialTheme.shapes.extraSmall
                    )

                }
            },

            headlineContent = {
                Text(
                    text = vidData.displayName,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis
                )
            },

            supportingContent = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically // Align content vertically in the row
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f) // Adjust weight to distribute space
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if (pref.showSizeField) {
                                DetailsChipUi(text = vidData.formattedFileSize)
                            }
                            if (pref.showResolutionField && vidData.height > 0) {
                                DetailsChipUi(text = "${vidData.height}p")
                            }
                        }

                        var currentDuration by remember { mutableIntStateOf(0) }
                        GlobalScope.launch(Dispatchers.Main) {
                            currentDuration =
                                playerViewModel?.getVideoState(vidData.path)?.position?.toInt() ?: 0
                        }

                        if (currentDuration != 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 5.dp)
                            ) {
                                VidProgressUi(
                                    vidData.duration.toInt(), currentDuration,
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            infoButtonClick.invoke()
                        }, modifier = Modifier.padding(start = 0.dp, end = 0.dp)
                    ) {
                        Icon(
                            imageVector = VanillaIcons.InfoVideo,
                            contentDescription = null,
                        )
                    }
                }
            },

            modifier = modifier.background(color = Color.Transparent).padding(bottom = 3.dp, top = 2.dp)
        )

        Divider(thickness = 1.dp, modifier = Modifier.padding(start = 15.dp, end = 15.dp))
    }

    AnimatedVisibility(visible = pref.showVideoTheme == VideoTheme.THUMBNAIL_BIG) {
        Column {
            Box(
                modifier = modifier
                    .clip(MaterialTheme.shapes.small)
                    .aspectRatio(16f / 10f)
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
            ) {
                val gradient = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        "#9C000000".color,
                        "#9C000000".color,
                        "#B5000000".color,
                    ),
                )
                Icon(
                    imageVector = VanillaIcons.Video,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceColorAtElevation(100.dp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(0.5f)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    GlideImage(
                        imageModel = { vidData.uriString }, imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop, alignment = Alignment.Center
                        ), modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .align(Alignment.BottomCenter)
                            .background(gradient)
                    )

                    var currentDuration by remember { mutableIntStateOf(0) }
                    GlobalScope.launch(Dispatchers.Main) {
                        currentDuration =
                            playerViewModel?.getVideoState(vidData.path)?.position?.toInt() ?: 0
                    }
                    if (currentDuration != 0) {
                        downSidePadding = 8.dp
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            VidProgressUi(
                                vidData.duration.toInt(), currentDuration,
                            )
                        }
                    } else {
                        downSidePadding = 0.dp
                    }

                }



                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                ) {
                    Text(
                        text = vidData.displayName,
                        maxLines = 1,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Start),
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 8.dp, start = 8.dp, end = 10.dp
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 8.dp,
                                top = 2.dp,
                                end = 8.dp,
                                bottom = downSidePadding
                            ), horizontalArrangement = Arrangement.Start
                    ) {
                        DetailsChipUi(
                            text = vidData.formattedDuration,
                            modifier = Modifier.padding(5.dp),
                            backgroundColor = Color.Black.copy(alpha = 0.6f),
                            contentColor = Color.White,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        DetailsChipUi(
                            text = vidData.formattedFileSize,
                            modifier = Modifier.padding(5.dp),
                            backgroundColor = Color.Black.copy(alpha = 0.6f),
                            contentColor = Color.White,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                    }

                }

                DetailsChipUi(
                    text = "${vidData.height}p",
                    modifier = Modifier
                        .padding(5.dp)
                        .then(Modifier.align(Alignment.TopEnd)),
                    backgroundColor = Color.Black.copy(alpha = 0.6f),
                    contentColor = Color.White,
                    shape = MaterialTheme.shapes.extraSmall,
                )

                IconButton(modifier = Modifier
                    .align(Alignment.TopStart)
                    .alpha(0.7f), onClick = {
//                    boolInfoVisible = !boolInfoVisible
                    infoButtonClick.invoke()
                }) {
                    Icon(
                        imageVector = VanillaIcons.Info,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = modifier
                            .clip(MaterialTheme.shapes.small)
                            .fillMaxSize()
                            .background(selectedItemColor)
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                    ) {
                        IconButton(
                            onClick = { }, modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                imageVector = VanillaIcons.CheckBox,
                                contentDescription = "Selected",
                            )
                        }
                    }
                }


            }


        }

    }


}

@LightDarkPrev
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun VidItemPrev() {
    VideoPlayerTheme {
        Surface {
            VideoItem(
                vidData = VideoData.sample,
                isSelected = false,
                pref = ApplicationPrefData(),
                playerViewModel = null,
                infoButtonClick = {})
        }
    }
}
