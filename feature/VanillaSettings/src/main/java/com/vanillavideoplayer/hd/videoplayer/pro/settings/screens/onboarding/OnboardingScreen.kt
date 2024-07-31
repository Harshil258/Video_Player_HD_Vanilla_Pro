package com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.NativeAdCompose
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.theme.VideoPlayerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@PreviewLightDark
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val images = listOf(
        R.drawable.image1, R.drawable.image2, R.drawable.image3
    )
    val headers = listOf(
        stringResource(id = R.string.header1), stringResource(id = R.string.header2), stringResource(id = R.string.header3)
    )
    val descriptions = listOf(
        stringResource(id = R.string.description1), stringResource(id = R.string.description2), stringResource(id = R.string.description3)
    )

    val pagerState = rememberPagerState(
        initialPage = 0, initialPageOffsetFraction = 0f
    ) { 3 }

    val scope = rememberCoroutineScope()
    var adLoaded by remember { mutableStateOf(false) }

    VideoPlayerTheme {
        Scaffold(topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = context.getString(R.string.onboarding_header), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }, actions = {
                if (pagerState.currentPage < images.size - 1) {
                    Text(text = context.getString(R.string.skip), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal), modifier = Modifier
                        .padding(end = 10.dp)
                        .clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(3)
                            }
                        })
                }
            })
        }) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                HorizontalPager(modifier = Modifier.weight(1f), state = pagerState, userScrollEnabled = true, pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(pagerState, Orientation.Horizontal), pageContent = { page ->
                    Image(
                        painter = painterResource(id = images[page]), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                    )
                })

                Row(
                    Modifier
                        .height(35.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val alpha = if (pagerState.currentPage == iteration) 1f else 0.2f
                        val size = if (pagerState.currentPage == iteration) 13.dp else 10.dp
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(
                                        alpha = alpha
                                    ), CircleShape
                                )
                                .size(size)
                        )
                    }
                }

                Text(
                    text = headers[pagerState.currentPage], style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp
                    ), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), maxLines = 1
                )

                Text(
                    text = descriptions[pagerState.currentPage],
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    minLines = 4,
                    maxLines = 4,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
                )



                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        delay(50) // 50ms delay
                        adLoaded = true
                    }
                }

                if (adLoaded) {
                    NativeAdCompose(fromWhichScreen = "onBoarding")
                }
                Button(
                    onClick = {
                        if (pagerState.currentPage < images.size - 1) {
                            scope.launch {
                                pagerState.scrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onGetStarted()
                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 15.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage < images.size - 1) stringResource(id = R.string.next) else stringResource(id = R.string.get_started), style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSecondary
                        ), fontSize = 18.sp
                    )
                }
            }
        }
    }
}
