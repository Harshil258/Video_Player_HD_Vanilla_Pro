package com.vanillavideoplayer.videoplayer.feature.videopicker.screens.media

import android.net.Uri
import android.os.Looper
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harshil258.adplacer.adViews.BannerView
import com.harshil258.adplacer.utils.STATUS
import com.harshil258.adplacer.utils.SharedPrefConfig.Companion.sharedPrefConfig
import com.vanillavideoplayer.videoplayer.core.model.ApplicationPrefData
import com.vanillavideoplayer.videoplayer.core.model.VideoData
import com.vanillavideoplayer.videoplayer.core.model.VideoTheme
import com.vanillavideoplayer.videoplayer.core.ui.R
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaCancelButton
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaDoneButton
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaPlayerCenterAlignedTopBar
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaPlayerDialog
import com.vanillavideoplayer.videoplayer.core.ui.composviews.verticalFadingEdge
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.videoplayer.feature.player.PlayerViewModel
import com.vanillavideoplayer.videoplayer.feature.videopicker.composables.DeleteConfirmationDialog
import com.vanillavideoplayer.videoplayer.feature.videopicker.composables.DirsListFromState
import com.vanillavideoplayer.videoplayer.feature.videopicker.composables.QuickSettingsDialogUi
import com.vanillavideoplayer.videoplayer.feature.videopicker.composables.RecentVidsListFromState
import com.vanillavideoplayer.videoplayer.feature.videopicker.composables.TextIcToggleBtn
import com.vanillavideoplayer.videoplayer.feature.videopicker.composables.VidListFromState
import com.vanillavideoplayer.videoplayer.feature.videopicker.navigation.launchVideoPlayerActivity
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.FoldersStateSealedInter
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.VideosStateSealedInter
import com.vanillavideoplayer.videoplayer.settings.GlobalPrefs
import com.vanillavideoplayer.videoplayer.settings.screens.appearance.AppearancePreferencesViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, DelicateCoroutinesApi::class)
@ExperimentalMaterial3Api
@Composable
internal fun MediaPickerUi(
    videosStateSealedInter: VideosStateSealedInter,
    foldersStateSealedInter: FoldersStateSealedInter,
    historyVideoState: VideosStateSealedInter,
    preferences: ApplicationPrefData,
    viewModel: FilePickerViewModel?,
    onPlayVideo: (uri: Uri) -> Unit = {},
    onFolderClick: (folderPath: String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    updatePreferences: (ApplicationPrefData) -> Unit = {},
    onDeleteVideoClick: (List<String>?) -> Unit,
    onDeleteFolderClick: (String) -> Unit,
    playerViewModel: PlayerViewModel?,
    viewModelAppearance: AppearancePreferencesViewModel = hiltViewModel(),
    androidViewBannerCache: MutableMap<Int, BannerView>,
    selectedTracks: List<VideoData>?,
    clearSelectedTracks: () -> Unit?
) {

    var showSortMenu by rememberSaveable { mutableStateOf(false) }
    val videolistState = rememberLazyListState()
    val folderlistState = rememberLazyListState()
    val historylistState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val bannerSizeState = remember { MutableStateFlow(0) }
    val bannerSize by bannerSizeState.collectAsState()
    var disableMultiSelect by rememberSaveable { mutableStateOf(true) }
    var itemCount by rememberSaveable { mutableIntStateOf(0) }
    var deleteAction by rememberSaveable { mutableStateOf("") }
    var lastposition by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.fillMaxSize()
    )

//    viewModelAppearance::toggleChangeThumbnailSize
//    var bannerSize12 by remember {  }
    Column {

        val pagerState = rememberPagerState(pageCount = { 3 })

        VanillaPlayerCenterAlignedTopBar(s = if (disableMultiSelect) {
            stringResource(id = R.string.app_name)
        } else {
            stringResource(
                id = R.string.selected_tracks_count,
                selectedTracks!!.size,
                itemCount
            )
        }, icon = {
            IconButton(onClick = {
                GlobalPrefs().increaseCount(context)
                if (disableMultiSelect) {
                    viewModelAppearance.toggleChangeThumbnailSize()
                } else {
                    disableMultiSelect = true
                    clearSelectedTracks()
                }
            }) {

                if (disableMultiSelect) {
                    if (preferences.showVideoTheme == VideoTheme.THUMBNAIL_BIG) {
                        Icon(
                            imageVector = VanillaIcons.BigThumbnail,
                            contentDescription = stringResource(id = R.string.ChangeThumbnail)
                        )
                    } else {
                        Icon(
                            imageVector = VanillaIcons.SmallThumbnail,
                            contentDescription = stringResource(id = R.string.ChangeThumbnail)
                        )
                    }
                } else {
                    Icon(
                        imageVector = VanillaIcons.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_up)
                    )
                }

            }
        }, actions = {
            if (!disableMultiSelect && lastposition == 2) {
                IconButton(onClick = {
                    viewModel?.clearSelectedTracks()
                    when (historyVideoState) {
                        is VideosStateSealedInter.SuccessDataClass -> {
                            GlobalScope.launch(Dispatchers.IO) {
                                historyVideoState.data.forEach {
                                    it.isSelected = true
                                    viewModel?.addToSelectedTracks(it)
                                }
                            }
                        }

                        else -> {}
                    }

                }) {
                    Icon(
                        imageVector = VanillaIcons.SelectAll,
                        contentDescription = stringResource(id = R.string.selectall)
                    )
                }
            }
            if (!disableMultiSelect && selectedTracks!!.isNotEmpty()) {

                IconButton(onClick = {
                    deleteAction = if (lastposition == 2) {
                        "historyvideo"
                    } else {
                        "normalvideo"
                    }

                }) {
                    Icon(
                        imageVector = VanillaIcons.Delete,
                        contentDescription = stringResource(id = R.string.delete)
                    )
                }

            } else if (disableMultiSelect) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = VanillaIcons.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
            }

        })

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier.weight(1f),
            reverseLayout = false,
            beyondBoundsPageCount = 0,
            key = null,
        ) { page ->
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalFadingEdge(scrollState, if (page == 0) 15.dp else 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 5.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    val state: Any = when (page) {
                        0 -> {
                            videosStateSealedInter
                        }

                        1 -> {
                            foldersStateSealedInter
                        }

                        else -> {
                            historyVideoState
                        }
                    }

                    when (state) {
                        is VideosStateSealedInter.SuccessDataClass, is FoldersStateSealedInter.Success -> {
                            itemCount = when (state) {
                                is VideosStateSealedInter.SuccessDataClass -> state.data.size
                                is FoldersStateSealedInter.Success -> state.data.size
                                else -> 0
                            }

                            val message = when (itemCount) {
                                0 -> when (page) {
                                    0 -> {
                                        stringResource(id = R.string.no_videos)
                                    }

                                    1 -> {
                                        stringResource(id = R.string.no_folders)
                                    }

                                    else -> {
                                        stringResource(id = R.string.no_history_videos)
                                    }
                                }

                                1 -> "1 ${
                                    when (page) {
                                        0 -> {
                                            stringResource(id = R.string.video)
                                        }

                                        1 -> {
                                            stringResource(id = R.string.folder)
                                        }

                                        else -> {
                                            stringResource(id = R.string.history_video)
                                        }
                                    }
                                }"
                                else -> "$itemCount ${
                                    when (page) {
                                        0 -> {
                                            stringResource(id = R.string.videos)
                                        }

                                        1 -> {
                                            stringResource(id = R.string.folders)
                                        }

                                        else -> {
                                            stringResource(id = R.string.history_videos)
                                        }
                                    }
                                }"
                            }
                            Text(
                                text = message,
                                maxLines = 2,
                                style = MaterialTheme.typography.bodyLarge,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }

                        is VideosStateSealedInter.LoadingDataObj, is FoldersStateSealedInter.LoadingDataOb -> {
                            Text(
                                text = stringResource(id = R.string.loading),
                                maxLines = 2,
                                style = MaterialTheme.typography.bodyLarge,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }

                        else -> {
                            // Handle other states here, if needed.
                        }
                    }
                    if (page != 2) {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(
                                imageVector = VanillaIcons.Sort,
                                contentDescription = null,
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f), contentAlignment = Alignment.Center
                ) {
                    if (playerViewModel != null) {
                        when (page) {
                            0 -> {
                                lastposition = 0
                                var showFloatingButton by remember { mutableStateOf(false) }

                                LaunchedEffect(scrollState) {
                                    snapshotFlow { videolistState.firstVisibleItemIndex }.collect { firstVisibleItemIndex ->
                                        showFloatingButton = firstVisibleItemIndex > 0
                                    }
                                }

                                VidListFromState(
                                    videosStateSealedInter = videosStateSealedInter,
                                    onVideoClick = onPlayVideo, preferences = preferences,
                                    toggleMultiSelect = {
                                        disableMultiSelect = false
                                    },
                                    onDeleteVideoClick = {
                                        onDeleteVideoClick(listOf(it))
                                        if (!disableMultiSelect && !selectedTracks.isNullOrEmpty()) {
                                            onDeleteVideoClick(selectedTracks.map { videoData -> videoData.path })
                                        } else {
                                            GlobalScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    "no selected videos!!!",
                                                    withDismissAction = false,
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    },
                                    disableMultiSelect = disableMultiSelect,
                                    state = videolistState,
                                    playerViewModel = playerViewModel,
                                    viewModel = viewModel!!,
                                )

                            }

                            1 -> {
                                if (page != lastposition) {
                                    disableMultiSelect = true
                                    clearSelectedTracks()
                                }
                                lastposition = page
                                DirsListFromState(
                                    foldersStateSealedInter = foldersStateSealedInter,
                                    onFolderClick = onFolderClick,
                                    state = folderlistState,
                                    onDeleteFolderClick = onDeleteFolderClick
                                )
                            }

                            2 -> {
                                if (page != lastposition) {
                                    disableMultiSelect = true
                                    clearSelectedTracks()
                                }
                                lastposition = page
                                RecentVidsListFromState(
                                    videosStateSealedInter = historyVideoState,
                                    onVideoClick = onPlayVideo,
                                    preferences = preferences,
                                    toggleMultiSelect = {
                                        disableMultiSelect = false
                                    },
                                    state = historylistState,
                                    onDeleteVideoClick = {
                                        GlobalScope.launch(Dispatchers.Main) {
                                            val videosState = playerViewModel.getVideoState(it)
                                            playerViewModel.saveLastPlayedState(
                                                videosState!!.path, 0
                                            )
                                        }
                                    },
                                    playerViewModel = playerViewModel,
                                    viewModel = viewModel!!,
                                    disableMultiSelect = disableMultiSelect,
                                )
                            }
                        }
                    }
                }
            }
        }

        val items = listOf("Videos", "Folders", "Search", "History")
        val logos = listOf(
            VanillaIcons.Video,
            VanillaIcons.Folder,
            VanillaIcons.Search,
            VanillaIcons.History
        )

        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = { Icon(logos[index], contentDescription = item) },
                    label = { Text(item) },
                    selected = if (pagerState.currentPage == 2) {
                        index == 3
                    } else {
                        pagerState.currentPage == index
                    },
                    onClick = {
                        if (index == 2) {
                            onSearchClick.invoke()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    })
            }
        }

        val cachedView = androidViewBannerCache[0]
        val appDetailModel = sharedPrefConfig.appDetails
        if(appDetailModel.adStatus == STATUS.ON.name){
            androidx.compose.animation.AnimatedVisibility(visible = true) {
                AndroidView(factory = { context ->
                    if (cachedView != null) {
                        (cachedView.parent as? ViewGroup)?.removeView(cachedView)
                        GlobalScope.launch(Dispatchers.Main) {
                            android.os.Handler(Looper.getMainLooper()).post {
                                val screenPixelDensity = context.resources.displayMetrics.density
                                val dpValue = cachedView.height / screenPixelDensity
                                bannerSizeState.value = dpValue.toInt()
                            }
                        }
                        cachedView
                    } else {
                        val myAndroidView = BannerView(context)
                        androidViewBannerCache[0] = myAndroidView
                        GlobalScope.launch(Dispatchers.Main) {
                            android.os.Handler(Looper.getMainLooper()).post {
                                val screenPixelDensity = context.resources.displayMetrics.density
                                val dpValue = myAndroidView.height / screenPixelDensity
                                bannerSizeState.value = dpValue.toInt()
                            }
                        }

                        myAndroidView
                    }
                })
            }
        }

    }
    val bannerSizePadding = remember(bannerSize) {
        PaddingValues(bottom = 90.dp + bannerSize.dp, end = 12.dp)
    }

    var showUrlDialog by rememberSaveable { mutableStateOf(false) }
    val selectVideoFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            it?.let(context::launchVideoPlayerActivity)
        })
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bannerSizePadding),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(
            onClick = { selectVideoFileLauncher.launch("video/*") },
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Icon(
                imageVector = VanillaIcons.FileOpen, contentDescription = "Upload File"
            )
        }
        FloatingActionButton(
            onClick = { showUrlDialog = true }, modifier = Modifier.padding(end = 12.dp, top = 8.dp)
        ) {
            Icon(
                imageVector = VanillaIcons.Link, contentDescription = "Open URL"
            )
        }
    }

    if (showSortMenu) {
        QuickSettingsDialogUi(applicationPrefData = preferences, onDismiss = {
            showSortMenu = false
            coroutineScope.launch {
                videolistState.scrollToItem(index = 0)
                folderlistState.scrollToItem(index = 0)
            }
        }, updatePreferences = updatePreferences, onCancel = {
            showSortMenu = false
        })
    }

    if (showUrlDialog) {
        NetworkUrlDialog(
            onDismiss = { showUrlDialog = false },
            onDone = { context.launchVideoPlayerActivity(Uri.parse(it)) })
    }

//    if (showMenu) {
//        QuickSettingsDialog(
//            applicationPreferences = preferences,
//            onDismiss = { showMenu = false },
//            updatePreferences = updatePreferences
//        )
//    }


    if (deleteAction == "normalvideo") {
        DeleteConfirmationDialog(
            subText = stringResource(id = R.string.delete_file),
            onCancel = { deleteAction = "" },
            onConfirm = {
                onDeleteVideoClick(selectedTracks!!.map { it.uriString })
                deleteAction = ""
                clearSelectedTracks()
                disableMultiSelect = true

            },
            fileNames = selectedTracks!!.map { it.nameWithExtension })
    } else if (deleteAction == "historyvideo") {
        DeleteConfirmationDialog(
            subText = stringResource(id = R.string.deleteFromHistoryText),
            onCancel = { deleteAction = "" },
            onConfirm = {
                selectedTracks!!.forEach {
                    playerViewModel?.saveLastPlayedState(
                        it.path, 0
                    )
                }
                deleteAction = ""
                clearSelectedTracks()
                disableMultiSelect = true
            },
            fileNames = selectedTracks!!.map { it.nameWithExtension })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPickerRoute(
    onSettingsClick: () -> Unit,
    onPlayVideo: (uri: Uri) -> Unit,
    onFolderClick: (folderPath: String) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: FilePickerViewModel,
    playerViewModel: PlayerViewModel,
    androidViewBannerCache: MutableMap<Int, BannerView>
) {
    val videosState by viewModel.videosStateSealedInter.collectAsStateWithLifecycle()
    val foldersState by viewModel.dirsStateSealedInter.collectAsStateWithLifecycle()
    val preferences by viewModel.prefs.collectAsStateWithLifecycle()
    val historyVideoState by viewModel.recentVideoState.collectAsStateWithLifecycle()

    val deleteIntentSenderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {})

    MediaPickerUi(videosStateSealedInter = videosState,
        foldersStateSealedInter = foldersState,
        historyVideoState = historyVideoState,
        preferences = preferences,
        onPlayVideo = onPlayVideo,
        onFolderClick = onFolderClick,
        onSettingsClick = onSettingsClick,
        onSearchClick = onSearchClick,
        updatePreferences = viewModel::initMenu,
        viewModel = viewModel,
        selectedTracks = viewModel.selectedTracks,
        onDeleteVideoClick = {
            viewModel.removeVideos(it!!, deleteIntentSenderLauncher)
        },
        onDeleteFolderClick = { viewModel.removeDirs(listOf(it), deleteIntentSenderLauncher) },
        playerViewModel = playerViewModel,
        androidViewBannerCache = androidViewBannerCache,
        clearSelectedTracks = { viewModel.clearSelectedTracks() })
}

const val ROUND_PROG_INDI_TEST_STRING = "circularProgressIndicator"

@Preview
@Composable
fun MPButtonPreview() {
    Surface {
        TextIcToggleBtn(btnText = "Title", icVect = VanillaIcons.Title, onClick = {})
    }
}

@Composable
fun NetworkUrlDialog(
    onDismiss: () -> Unit,
    onDone: (String) -> Unit,
) {
    var url by rememberSaveable { mutableStateOf("") }
    VanillaPlayerDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.network_stream)) },
        content = {
            Text(text = stringResource(R.string.enter_a_network_url))
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(R.string.example_url)) })
        },
        confirmButton = {
            VanillaDoneButton(onClick = {
                if (url.isBlank()) onDismiss() else onDone(url)
            })
        },
        dismissButton = { VanillaCancelButton(onClick = onDismiss) })
}
