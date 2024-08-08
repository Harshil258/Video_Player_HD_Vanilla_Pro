package com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.mediaFolder

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harshil258.adplacer.adViews.BannerView
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.prettyName
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ApplicationPrefData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.VideoData
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerCenterAlignedTopBar
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.PlayerViewModel
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.composables.DeleteConfirmationDialog
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.composables.VidListFromState
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.VideosStateSealedInter
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderScreenUi(
    folderPath: String,
    videosStateSealedInter: VideosStateSealedInter,
    preferences: ApplicationPrefData,
    state: LazyListState,
    onNavigateUp: () -> Unit,
    onVideoClick: (Uri) -> Unit,
    onDeleteVideoClick: (List<String>?) -> Unit,
    playerViewModel: PlayerViewModel?,
    viewModel: FolderPickerViewModel,
    selectedTracks: List<VideoData>?,
    clearSelectedTracks: () -> Unit?
) {

    var disableMultiSelect by rememberSaveable { mutableStateOf(true) }
    var itemCount by rememberSaveable { mutableIntStateOf(0) }
    var deleteAction by rememberSaveable { mutableStateOf("") }

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
    }

    itemCount = when (videosStateSealedInter) {
        is VideosStateSealedInter.SuccessDataClass -> videosStateSealedInter.data.size
        else -> 0
    }

    Column {
        VanillaPlayerCenterAlignedTopBar(
            s = if (disableMultiSelect) {
                File(folderPath).prettyName
            } else {
                stringResource(
                    id = R.string.selected_tracks_count,
                    selectedTracks!!.size,
                    itemCount
                )
            },
            icon = {
                IconButton(
                    onClick = {
                        if (disableMultiSelect) {
                            onNavigateUp()
                        } else {
                            disableMultiSelect = true
                            clearSelectedTracks()
                        }
                    }
                ) {
                    Icon(
                        imageVector = VanillaIcons.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_up)
                    )
                }
            },
            actions = {
                if (!disableMultiSelect) {
                    IconButton(onClick = {
                        deleteAction = "normalvideo"
                    }) {
                        Icon(
                            imageVector = VanillaIcons.Delete,
                            contentDescription = stringResource(id = R.string.delete)
                        )
                    }
                }
            }
        )


        AndroidView(factory = { context ->
            
            val myAndroidView = BannerView(context)
            myAndroidView
        })
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            VidListFromState(
                videosStateSealedInter = videosStateSealedInter,
                preferences = preferences,
                state = state,
                onVideoClick = onVideoClick,
                onDeleteVideoClick = {

                    onDeleteVideoClick(listOf(it))
                    if (!disableMultiSelect && selectedTracks!!.isNotEmpty()) {
                        onDeleteVideoClick(selectedTracks.map { videoData ->  videoData.path })
                    }

                },
                playerViewModel = playerViewModel,
                disableMultiSelect = disableMultiSelect,
                toggleMultiSelect = {
                    disableMultiSelect = false
                },
                viewModel = viewModel,
            )
        }

    }
}


@Composable
fun FolderScreenRoute(
    viewModel: FolderPickerViewModel = hiltViewModel(),
    mediaPickerViewModel: FolderPickerViewModel = hiltViewModel(),
    onVideoClick: (uri: Uri) -> Unit,
    onNavigateUp: () -> Unit,
    playerViewModel: PlayerViewModel?,
) {
    val vidState by viewModel.videos.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)
    val prefs by viewModel.pref.collectAsStateWithLifecycle()

    val removeIntentSenderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {}
    )

    FolderScreenUi(
        folderPath = viewModel.dirPath,
        videosStateSealedInter = vidState,
        preferences = prefs,
        state = rememberLazyListState(),
        onVideoClick = onVideoClick,
        onNavigateUp = onNavigateUp,
        onDeleteVideoClick = {

            mediaPickerViewModel.removeVideos(it!!, removeIntentSenderLauncher)
        },
        playerViewModel = playerViewModel,
        viewModel = mediaPickerViewModel,
        selectedTracks = mediaPickerViewModel.selectedTracks,
        clearSelectedTracks = { mediaPickerViewModel.clearSelectedTracks() }
    )
}
