package com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.search

import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harshil258.adplacer.adViews.BannerView
import com.harshil258.adplacer.utils.Logger
import com.harshil258.adplacer.utils.STATUS
import com.harshil258.adplacer.utils.SharedPrefConfig.Companion.sharedPrefConfig
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ApplicationPrefData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.VideoData
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.PlaylistKeeper
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.PlayerViewModel
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.composables.BSheetItem
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.composables.CenterInParentCircularProgress
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.composables.NoVidFound
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.composables.OptionsBSheet
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.composables.VideoItem
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.VideosStateSealedInter
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.media.FilePickerViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val SEARCH_ROUTE_CONST = "search_route_const"

@OptIn(ExperimentalFoundationApi::class, DelicateCoroutinesApi::class)
@ExperimentalMaterial3Api
@Composable
internal fun SearchScreenUi(
    onNavigateUp: () -> Unit = {},
    videosStateSealedInter: VideosStateSealedInter = VideosStateSealedInter.LoadingDataObj,
    preferences: ApplicationPrefData?, viewModel: FilePickerViewModel?, onPlayVideo: (uri: Uri) -> Unit = {}, playerViewModel: PlayerViewModel?
) {
    val videoListState = rememberLazyListState()
    val bannerSizeState = remember { MutableStateFlow(0) }
    var searchQuery by remember { mutableStateOf("") }
    var boolInfoVisible by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current
    var showMediaActionsFor: VideoData? by rememberSaveable { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            shape = RoundedCornerShape(percent = 50),
            tonalElevation = 5.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        onNavigateUp.invoke()
                        Logger.e("TAGdsththth", "MediaSearchUi: ")
                    }, modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(imageVector = VanillaIcons.ArrowBack, contentDescription = "Back")
                }
                val focusRequester = remember { FocusRequester() }

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(text = "Search") },
                    modifier = Modifier
                        .fillMaxWidth().focusRequester(focusRequester), singleLine = true, colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }


        if(sharedPrefConfig.appDetails.adStatus == STATUS.ON.name) {
            AndroidView(factory = { context ->
                val myAndroidView = BannerView(context)
                GlobalScope.launch(Dispatchers.Main) {
                    android.os.Handler(Looper.getMainLooper()).post {
                        val screenPixelDensity = context.resources.displayMetrics.density
                        val dpValue = myAndroidView.height / screenPixelDensity
                        bannerSizeState.value = dpValue.toInt()
                        Logger.e("tjstjstj", "MediaSearchUi: dpValue  $dpValue")

                    }
                }
                myAndroidView
            })
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp)
                    .align(Alignment.CenterHorizontally),
//                horizontalArrangement = Arrangement.SpaceBetween,
                horizontalArrangement = Arrangement.Center // This ensures children are centered horizontally
            ) {
                when (videosStateSealedInter) {
                    is VideosStateSealedInter.LoadingDataObj -> CenterInParentCircularProgress()

                    is VideosStateSealedInter.SuccessDataClass -> if (videosStateSealedInter.data.isEmpty()) {
                        NoVidFound()
                    } else {
                        LazyColumn(
                            state = videoListState, modifier = Modifier.fillMaxSize()
                        ) {
                            viewModel?.videoTracks = videosStateSealedInter.data.map { it.copy() }

                            val filteredVideos = viewModel?.videoTracks?.filter {
                                it.nameWithExtension.contains(searchQuery, ignoreCase = true)
                            } ?: emptyList()

                            items(filteredVideos, key = { it.path }) { video ->
                                VideoItem(vidData = video,
                                    isSelected = video.isSelected,
                                    pref = preferences!!,
                                    modifier = Modifier.combinedClickable(onClick = {
                                        val list: ArrayList<Uri> = ArrayList()
                                        videosStateSealedInter.data.forEach {
                                            list.add(Uri.parse(it.uriString))
                                        }
                                        GlobalScope.launch(Dispatchers.IO) {
                                            PlaylistKeeper.setPlaylistUris(list)
                                        }
                                        onPlayVideo(Uri.parse(video.uriString))

                                    }, onLongClick = {}),
                                    playerViewModel = playerViewModel,
                                    infoButtonClick = {
                                        if (video.path != boolInfoVisible) {
                                            boolInfoVisible = ""
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            showMediaActionsFor = video
                                        } else {
                                            boolInfoVisible = ""
                                        }
                                    })

                                androidx.compose.animation.AnimatedVisibility(visible = (boolInfoVisible == video.path)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 20.dp, end = 20.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 4.dp),
                                            ) {
                                                Text(
                                                    text = "${stringResource(R.string.title)} : ",
                                                    maxLines = 2,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = video.displayName,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 4.dp),
                                            ) {
                                                Text(
                                                    text = "${stringResource(R.string.path)} : ",
                                                    maxLines = 2,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = video.path,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 4.dp),
                                            ) {
                                                Text(
                                                    text = "${stringResource(R.string.duration)} : ",
                                                    maxLines = 2,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = video.formattedDuration,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 4.dp),
                                            ) {
                                                val dateFormat = SimpleDateFormat(
                                                    "dd/MM/yyyy hh:mm:ss aa", Locale.getDefault()
                                                )
                                                val formattedDate =
                                                    dateFormat.format(Date(video.dateModified * 1000))
                                                Text(
                                                    text = "${stringResource(R.string.date_added)} : ",
                                                    maxLines = 2,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = formattedDate,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 4.dp),
                                            ) {
                                                Text(
                                                    text = "${stringResource(R.string.size)} : ",
                                                    maxLines = 2,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = video.formattedFileSize,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 4.dp),
                                            ) {
                                                Text(
                                                    text = "${stringResource(R.string.resolution)} : ",
                                                    maxLines = 2,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = "${video.width} * ${video.height}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 4.dp),
                                            ) {
                                                Text(
                                                    text = "${stringResource(R.string.format)} : ",
                                                    maxLines = 2,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = video.nameWithExtension.removePrefix(
                                                        video.displayName
                                                    ),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                        }
                                    }
                                }


                            }

                        }
                    }
                }
                showMediaActionsFor?.let {
                    OptionsBSheet(
                        title = it.displayName,
                        onDismiss = { showMediaActionsFor = null }) {
                        BSheetItem(
                            text = stringResource(R.string.info),
                            icon = VanillaIcons.Info,
                            onClick = {
                                boolInfoVisible = if (boolInfoVisible != it.path) {
                                    it.path
                                } else {
                                    ""
                                }
                                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                    if (!bottomSheetState.isVisible) showMediaActionsFor = null
                                }
                            })
                        BSheetItem(
                            text = stringResource(R.string.share),
                            icon = VanillaIcons.Share,
                            onClick = {
                                val mediaStoreUri = Uri.parse(it.uriString)
                                val intent = Intent.createChooser(
                                    Intent().apply {
                                        type = "video/*"
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_STREAM, mediaStoreUri)
                                    }, null
                                )
                                context.startActivity(intent)
                                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                    if (!bottomSheetState.isVisible) showMediaActionsFor = null
                                }
                            })
                    }
                }
            }


        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenRoute(
    onNavigateUp: () -> Unit,
    playerViewModel: PlayerViewModel,
    onPlayVideo: (uri: Uri) -> Unit = {},
    viewModel: FilePickerViewModel,
) {
    val videosState by viewModel.videosStateSealedInter.collectAsStateWithLifecycle()
    val preferences by viewModel.prefs.collectAsStateWithLifecycle()

    SearchScreenUi(
        onNavigateUp = onNavigateUp, videosStateSealedInter = videosState, preferences = preferences, viewModel = viewModel, onPlayVideo = onPlayVideo, playerViewModel = playerViewModel
    )
}