package com.vanillavideoplayer.videoplayer.core.domain

import android.content.Context
import android.net.Uri
import com.vanillavideoplayer.videoplayer.core.common.Dispatcher
import com.vanillavideoplayer.videoplayer.core.common.VideoPlayerDispatchers
import com.vanillavideoplayer.videoplayer.core.common.extra.getPathFromUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FetchSortedPlaylistUC @Inject constructor(
    private val fetchSortedVidUC: FetchSortedVidUC,
    @ApplicationContext private val context: Context,
    @Dispatcher(VideoPlayerDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(uri: Uri): List<Uri> = withContext(defaultDispatcher) {
        val path = context.getPathFromUri(uri) ?: return@withContext emptyList()
        val parent = File(path).parent

        fetchSortedVidUC.invoke(parent).first().map { Uri.parse(it.uriString) }
    }
}
