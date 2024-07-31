package com.vanillavideoplayer.hd.videoplayer.pro.core.domain

import com.vanillavideoplayer.hd.videoplayer.pro.core.common.Dispatcher
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.VideoPlayerDispatchers
import com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo.MediaRepo
import com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo.PrefRepo
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.SortByEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.SortOrderEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.VideoData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FetchSortedVidUC @Inject constructor(
    private val mediaRepo: MediaRepo,
    private val prefRepo: PrefRepo,
    @Dispatcher(VideoPlayerDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    operator fun invoke(folderPath: String? = null): Flow<List<VideoData>> {
        val videosFlow = if (folderPath != null) {
            mediaRepo.getVideosFlowFromFolderPath(folderPath)
        } else {
            mediaRepo.getVideosFlow()
        }

        return combine(
            videosFlow,
            prefRepo.applicationPrefData
        ) { videoItems, preferences ->


            val nonExcludedVideos = videoItems.filterNot {
                it.parentPath in preferences.excludeFolders
            }

            when (preferences.sortOrderEnum) {
                SortOrderEnum.SORT_ASCENDING -> {
                    when (preferences.sortByEnum) {
                        SortByEnum.TITLE_SORT -> nonExcludedVideos.sortedBy { it.displayName.lowercase() }
                        SortByEnum.LENGTH_SORT -> nonExcludedVideos.sortedBy { it.duration }
                        SortByEnum.PATH_SORT -> nonExcludedVideos.sortedBy { it.path.lowercase() }
                        SortByEnum.SIZE_SORT -> nonExcludedVideos.sortedBy { it.size }
                        SortByEnum.DATE_SORT -> nonExcludedVideos.sortedBy { it.dateModified }
                    }
                }

                SortOrderEnum.SORT_DESCENDING -> {
                    when (preferences.sortByEnum) {
                        SortByEnum.TITLE_SORT -> nonExcludedVideos.sortedByDescending { it.displayName.lowercase() }
                        SortByEnum.LENGTH_SORT -> nonExcludedVideos.sortedByDescending { it.duration }
                        SortByEnum.PATH_SORT -> nonExcludedVideos.sortedByDescending { it.path.lowercase() }
                        SortByEnum.SIZE_SORT -> nonExcludedVideos.sortedByDescending { it.size }
                        SortByEnum.DATE_SORT -> nonExcludedVideos.sortedByDescending { it.dateModified }
                    }
                }
            }
        }.flowOn(defaultDispatcher)
    }
}
