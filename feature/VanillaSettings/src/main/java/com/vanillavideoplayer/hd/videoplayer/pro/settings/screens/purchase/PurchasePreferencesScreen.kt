package com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.purchase

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshil258.adplacer.utils.Logger
import com.harshil258.adplacer.utils.SharedPrefConfig.Companion.sharedPrefConfig
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.openLinkInBrowser

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun UpgradeToPremiumScreen(
    onNavigateUp: () -> Unit = {},
    onPurchase: () -> Unit = {},
    formattedPrice: MutableState<String> = mutableStateOf("")
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Upgrade", color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = { onNavigateUp.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
        )
    }, bottomBar = {
        Column {

            val context = LocalContext.current
            val tutorialUrl = sharedPrefConfig.appDetails.premiumVideo
            Logger.e("dertetdhjteh", "UpgradeToPremiumScreen: ${tutorialUrl}")
            if (tutorialUrl.isNotEmpty()) {
                Text(
                    text = "Unlock premium for FREE!",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 25.dp)
                        .padding(bottom = 25.dp)
                        .clickable {
                            openLinkInBrowser(context, tutorialUrl)
                        },
                    lineHeight = 15.sp,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            ElevatedButton(
                onClick = {
                    onPurchase.invoke()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 20.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(
                        0xFFDAA520
                    )
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Get Premium",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontFamily = FontFamily.Serif
                )
            }

            Text(
                text = "Take your video watching to the next level with our premium features! Upgrade to unlock a suite of powerful tools that will enhance your viewing experience.",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .padding(bottom = 20.dp),
            )
        }
    }) {
        Column(
            Modifier
                .background(color = Color.Black)
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp)
        ) {

            Image(
                painter = painterResource(id = com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R.drawable.vanilla_video_player_banner_12),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .background(Color.White)
                    .border(border = BorderStroke(1.dp, Color.White), RoundedCornerShape(20.dp))
            )

            Text(
                text = "Unlock Powerful Video Playback (or level up your video experience)",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(vertical = 16.dp),
                color = Color.White
            )

            Text(
                text = "Best Plans :-",
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 16.dp),
                color = Color.White
            )

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(top = 10.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
                border = BorderStroke(
                    1.dp, Color(
                        0xFFDAA520
                    )
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(
                    Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .width(85.dp)
                            .height(25.dp)
                            .align(Alignment.TopEnd)
                            .background(
                                color = Color(
                                    0xFFDAA520
                                ),
                                shape = RoundedCornerShape(topEnd = 10.dp, bottomStart = 10.dp)
                            ),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = "Trending",
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(20.dp)
                            .padding(top = 20.dp)
                            .padding(bottom = 5.dp)
                    ) {
                        Text(
                            text = "LifeTime : Ad Free",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            fontWeight = FontWeight.Light,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${formattedPrice.value.ifEmpty { "$0.49"}}/- Only",
                            fontWeight = FontWeight.Light,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }

        }
    }
}
