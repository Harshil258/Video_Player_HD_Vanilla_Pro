package com.vanillavideoplayer.videoplayer.feature.videopicker.composables

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.vanillavideoplayer.videoplayer.core.model.ApplicationPrefData
import com.vanillavideoplayer.videoplayer.core.model.FolderData
import com.vanillavideoplayer.videoplayer.core.model.VideoData
import com.vanillavideoplayer.videoplayer.core.ui.PlaylistKeeper
import com.vanillavideoplayer.videoplayer.core.ui.R
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaCancelButton
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaDoneButton
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaPlayerDialog
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.videoplayer.feature.player.PlayerViewModel
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.FoldersStateSealedInter
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.VideosStateSealedInter
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.media.MediaPickerViewModel
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.media.ROUND_PROG_INDI_TEST_STRING
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.mediaFolder.MediaPickerDirVM
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//val androidViewCache = remember { mutableMapOf<Int, NativeGBig>() }

@Preview
@Composable
fun RemoveDialogPrev() {
    RemoveConformDialog(
        subText = "The following files will be deleted permanently",
        onConfirm = {},
        onCancel = {},
        fileNames = listOf("Harry potter 1", "Harry potter 2", "Harry potter 3", "Harry potter 4")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsBSheet(
    title: String,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss, sheetState = sheetState
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(20.dp))
        content()
    }
}

@Composable
fun ContentLazyList(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    state: LazyListState,
    content: LazyListScope.() -> Unit,
) {

    LazyColumn(
        contentPadding = PaddingValues(vertical = 10.dp),
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        content = content,
        state = state
    )
}

@Composable
fun NoVidFound() {
    Text(
        text = stringResource(id = R.string.no_videos_found),
        style = MaterialTheme.typography.titleLarge
    )
}


@Composable
fun DeleteConfirmationDialog(
    subText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    fileNames: List<String>,
    modifier: Modifier = Modifier
) {
    VanillaPlayerDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = stringResource(R.string.delete),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = { DoneButton(onClick = onConfirm) },
        dismissButton = { CancelButton(onClick = onCancel) },
        modifier = modifier,
        content = {
            Text(
                text = subText, style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn {
                items(fileNames) {
                    Text(
                        text = it,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        })
}

@Composable
fun DoneButton(
    onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true
) {
    TextButton(
        enabled = enabled, onClick = onClick, modifier = modifier
    ) {
        Text(text = stringResource(R.string.done))
    }
}

@Composable
fun CancelButton(
    onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true
) {
    TextButton(
        enabled = enabled, onClick = onClick, modifier = modifier
    ) {
        Text(text = stringResource(R.string.cancel))
    }
}


@Composable
fun CenterInParentCircularProgress() {
    CircularProgressIndicator(
        modifier = Modifier.testTag(ROUND_PROG_INDI_TEST_STRING)
    )
}

private fun toggleSelection(video: VideoData, viewModel: ViewModel) {

    if (video.isSelected) {
        if (viewModel is MediaPickerViewModel) {
            viewModel.addToSelectedTracks(video)
        } else if (viewModel is MediaPickerDirVM) {
            viewModel.addToSelectedTracks(video)
        }
    } else {
        if (viewModel is MediaPickerViewModel) {
            viewModel.removeFromSelectedTracks(video)
        } else if (viewModel is MediaPickerDirVM) {
            viewModel.removeFromSelectedTracks(video)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, DelicateCoroutinesApi::class)
@Composable
fun VidListFromState(
    videosStateSealedInter: VideosStateSealedInter,
    preferences: ApplicationPrefData,
    state: LazyListState,
    onVideoClick: (Uri) -> Unit,
    onDeleteVideoClick: (String) -> Unit,
    playerViewModel: PlayerViewModel?,
    disableMultiSelect: Boolean,
    toggleMultiSelect: () -> Unit,
    viewModel: ViewModel,
) {
    val searchQuery by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current
    var showMediaActionsFor: VideoData? by rememberSaveable { mutableStateOf(null) }
    var deleteAction: VideoData? by rememberSaveable { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var multiSelect by rememberSaveable { mutableStateOf(false) }

    if (disableMultiSelect) {
        multiSelect = false
    }

    var boolInfoVisible by remember { mutableStateOf("") }

    var showFloatingButton by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleItemIndex }.collect { firstVisibleItemIndex ->
            showFloatingButton = firstVisibleItemIndex > 0
        }
    }

    when (videosStateSealedInter) {
        is VideosStateSealedInter.LoadingDataObj -> CenterInParentCircularProgress()

        is VideosStateSealedInter.SuccessDataClass -> if (videosStateSealedInter.data.isEmpty()) {
            NoVidFound()
        } else {

            LazyColumn(
                state = state, modifier = Modifier.fillMaxSize()
            ) {
                if (disableMultiSelect) {
                    if (viewModel is MediaPickerViewModel) {
                        viewModel.videoTracks = videosStateSealedInter.data.map { it.copy() }
                    } else if (viewModel is MediaPickerDirVM) {
                        viewModel.videoTracks = videosStateSealedInter.data.map { it.copy() }
                    }
                }

//                item {
//                    OutlinedTextField(value = searchQuery, onValueChange = { newQuery ->
//                        searchQuery = newQuery
//                    }, placeholder = { Text("Search videos") }, modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(start = 10.dp, end = 10.dp)
//                    )
//                }
//                val filteredVideos = videosStateSealedInter.data.filter {
//                    it.nameWithExtension.contains(searchQuery, ignoreCase = true)
//                }
                val filteredVideos = when (viewModel) {
                    is MediaPickerViewModel -> {
                        viewModel.videoTracks.filter {
                            it.nameWithExtension.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    is MediaPickerDirVM -> {
                        viewModel.videoTracks.filter {
                            it.nameWithExtension.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    else -> {
                        emptyList()
                    }
                }

                items(filteredVideos, key = { it.path }) { video ->
                    VideoItem(
                        vidData = video,
                        isSelected = video.isSelected,
                        pref = preferences,
                        md = Modifier.combinedClickable(onClick = {
                            if (multiSelect) {
                                video.isSelected = !video.isSelected
                                toggleSelection(video, viewModel)
                            } else {
                                val list: ArrayList<Uri> = ArrayList()
                                videosStateSealedInter.data.forEach {
                                    list.add(Uri.parse(it.uriString))
                                }
                                GlobalScope.launch(Dispatchers.IO) {
                                    PlaylistKeeper.setPlaylistUris(list)
                                }
                                onVideoClick(Uri.parse(video.uriString))
                            }


                        }, onLongClick = {
                            multiSelect = true

                            toggleMultiSelect()
                            video.isSelected = !video.isSelected
                            toggleSelection(video, viewModel)
                        }),
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

                    AnimatedVisibility(visible = (boolInfoVisible == video.path)) {
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        "dd/MM/yyyy hh:mm:ss aa",
                                        Locale.getDefault()
                                    )
                                    val formattedDate =
                                        dateFormat.format(Date(video.dateModified * 1000))
                                    Text(
                                        text = "${stringResource(R.string.date_added)} : ",
                                        maxLines = 2,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = video.nameWithExtension.removePrefix(video.displayName),
                                        style = MaterialTheme.typography.bodySmall,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }

                }

            }

            Column(
                verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()
            ) {
                if (showFloatingButton) {
                    FloatingActionButton(
                        onClick = {
                            GlobalScope.launch(Dispatchers.Main) {
//                                                videolistState.animateScrollToItem(0)
                                state.scrollToItem(0)
                            }
                            showFloatingButton = false
                        }, modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = VanillaIcons.ArrowUpward,
                            contentDescription = "Scroll to Upward"
                        )
                    }
                }
            }
        }
    }
    showMediaActionsFor?.let {
        OptionsBSheet(title = it.displayName, onDismiss = { showMediaActionsFor = null }) {
            BSheetItem(text = stringResource(R.string.info), icon = VanillaIcons.Info, onClick = {
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
                text = stringResource(R.string.delete),
                icon = VanillaIcons.Delete,
                onClick = {
                    deleteAction = it
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) showMediaActionsFor = null
                    }
                })
            BSheetItem(text = stringResource(R.string.share), icon = VanillaIcons.Share, onClick = {
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

    deleteAction?.let {
        DeleteConfirmationDialog(
            subText = stringResource(id = R.string.delete_file),
            onCancel = { deleteAction = null },
            onConfirm = {
                onDeleteVideoClick(it.uriString)
                deleteAction = null
//            clearSelectedTracks()
//            disableMultiSelect = true

            },
            fileNames = listOf(it.nameWithExtension)
        )
    }


}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DirsListFromState(
    foldersStateSealedInter: FoldersStateSealedInter,
    preferences: ApplicationPrefData,
    state: LazyListState,
    onFolderClick: (String) -> Unit,
    onDeleteFolderClick: (String) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    var showFolderActionsFor: FolderData? by rememberSaveable { mutableStateOf(null) }
    var deleteAction: FolderData? by rememberSaveable { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    when (foldersStateSealedInter) {
        FoldersStateSealedInter.LoadingDataOb -> CenterInParentCircularProgress()
        is FoldersStateSealedInter.Success -> if (foldersStateSealedInter.data.isEmpty()) {
            NoVidFound()
        } else {
            ContentLazyList(state = state) {
                items(foldersStateSealedInter.data, key = { it.path }) {
                    DirItem(
                        folder = it,
                        preferences = preferences,
                        modifier = Modifier.combinedClickable(onClick = {
                            onFolderClick(it.path)
                        }, onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showFolderActionsFor = it
                        })
                    )
                }
            }
        }
    }

    showFolderActionsFor?.let {
        OptionsBSheet(title = it.name, onDismiss = { showFolderActionsFor = null }) {
            BSheetItem(
                text = stringResource(R.string.delete),
                icon = VanillaIcons.Delete,
                onClick = {
                    deleteAction = it
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) showFolderActionsFor = null
                    }
                })
        }
    }

    deleteAction?.let {
        RemoveConformDialog(
            subText = stringResource(R.string.delete_folder),
            onCancel = { deleteAction = null },
            onConfirm = {
                onDeleteFolderClick(it.path)
                deleteAction = null
            },
            fileNames = listOf(it.name)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, DelicateCoroutinesApi::class)
@Composable
fun RecentVidsListFromState(
    videosStateSealedInter: VideosStateSealedInter,
    preferences: ApplicationPrefData,
    state: LazyListState,
    onVideoClick: (Uri) -> Unit,
    onDeleteVideoClick: (String) -> Unit,
    playerViewModel: PlayerViewModel?,
    viewModel: MediaPickerViewModel,
    toggleMultiSelect: () -> Unit,
    disableMultiSelect: Boolean,
) {
    val haptic = LocalHapticFeedback.current
    var showMediaActionsFor: VideoData? by rememberSaveable { mutableStateOf(null) }
    var deleteAction: VideoData? by rememberSaveable { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var boolInfoVisible by remember { mutableStateOf("") }

    var multiSelect by rememberSaveable { mutableStateOf(false) }

    if (disableMultiSelect) {
        multiSelect = false
    }



    when (videosStateSealedInter) {
        is VideosStateSealedInter.LoadingDataObj -> CenterInParentCircularProgress()
        is VideosStateSealedInter.SuccessDataClass -> if (videosStateSealedInter.data.isEmpty()) {
            NoVidFound()
        } else {
            ContentLazyList(state = state) {
                items(videosStateSealedInter.data, key = { it.path }) { video ->
                    VideoItem(
                        vidData = video,
                        isSelected = video.isSelected,
                        pref = preferences,
                        md = Modifier.combinedClickable(onClick = {

                            if (multiSelect) {
                                video.isSelected = !video.isSelected
                                toggleSelection(video, viewModel)
                            } else {
                                val list: ArrayList<Uri> = ArrayList()
                                videosStateSealedInter.data.forEach {
                                    list.add(Uri.parse(it.uriString))
                                }
                                GlobalScope.launch(Dispatchers.IO) {
                                    PlaylistKeeper.setPlaylistUris(list)
                                }
                                onVideoClick(Uri.parse(video.uriString))
                            }


                        }, onLongClick = {
                            multiSelect = true
                            toggleMultiSelect()
                        }),
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

                    AnimatedVisibility(visible = (boolInfoVisible == video.path)) {
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        "dd/MM/yyyy hh:mm:ss aa",
                                        Locale.getDefault()
                                    )
                                    val formattedDate =
                                        dateFormat.format(Date(video.dateModified * 1000))
                                    Text(
                                        text = "${stringResource(R.string.date_added)} : ",
                                        maxLines = 2,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = video.nameWithExtension.removePrefix(video.displayName),
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
        VideosStateSealedInter.LoadingDataObj -> {
        }
    }

    showMediaActionsFor?.let {
        OptionsBSheet(title = it.displayName, onDismiss = { showMediaActionsFor = null }) {
            BSheetItem(text = stringResource(R.string.info), icon = VanillaIcons.Info, onClick = {
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
                text = stringResource(R.string.deleteFromHistory),
                icon = VanillaIcons.Delete,
                onClick = {
                    deleteAction = it
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) showMediaActionsFor = null
                    }
                })
            BSheetItem(text = stringResource(R.string.share), icon = VanillaIcons.Share, onClick = {
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

    deleteAction?.let {
        RemoveConformDialog(
            subText = stringResource(id = R.string.deleteFromHistoryText),
            onCancel = { deleteAction = null },
            onConfirm = {
                onDeleteVideoClick(it.path)
                deleteAction = null
            },
            fileNames = listOf(it.nameWithExtension)
        )
    }
}

@Composable
fun BSheetItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = { Icon(imageVector = icon, contentDescription = null) },
        headlineContent = { Text(text = text) },
        modifier = modifier.clickable(onClick = onClick)
    )
}


@Composable
fun RemoveConformDialog(
    subText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    fileNames: List<String>,
    modifier: Modifier = Modifier,
) {
    VanillaPlayerDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = stringResource(R.string.delete), modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = { VanillaDoneButton(onClick = onConfirm) },
        dismissButton = { VanillaCancelButton(onClick = onCancel) },
        modifier = modifier,
        content = {
            Text(
                text = subText, style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn {
                items(fileNames) {
                    Text(
                        text = it,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        })
}


