package com.vanillavideoplayer.hd.videoplayer.pro.core.domain

import com.vanillavideoplayer.hd.videoplayer.pro.core.common.Dispatcher
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.VideoPlayerDispatchers
import com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo.MediaRepo
import com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo.PrefRepo
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.FolderData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.SortByEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.SortOrderEnum
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FetchSortedFoldersUC @Inject constructor(
    private val mediaRepo: MediaRepo,
    private val prefRepo: PrefRepo,
    @Dispatcher(VideoPlayerDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<List<FolderData>> {
        return combine(
            mediaRepo.getFoldersFlow(),
            prefRepo.applicationPrefData
        ) { folders, preferences ->

            val nonExcludedDirectories = folders.filterNot {
                it.path in preferences.excludeFolders
            }

            when (preferences.sortOrderEnum) {
                SortOrderEnum.SORT_ASCENDING -> {
                    when (preferences.sortByEnum) {
                        SortByEnum.TITLE_SORT -> nonExcludedDirectories.sortedBy { it.name.lowercase() }
                        SortByEnum.LENGTH_SORT -> nonExcludedDirectories.sortedBy { it.mediaCount }
                        SortByEnum.PATH_SORT -> nonExcludedDirectories.sortedBy { it.path.lowercase() }
                        SortByEnum.SIZE_SORT -> nonExcludedDirectories.sortedBy { it.mediaSize }
                        SortByEnum.DATE_SORT -> nonExcludedDirectories.sortedBy { it.dateModified }
                    }
                }

                SortOrderEnum.SORT_DESCENDING -> {
                    when (preferences.sortByEnum) {
                        SortByEnum.TITLE_SORT -> nonExcludedDirectories.sortedByDescending { it.name.lowercase() }
                        SortByEnum.LENGTH_SORT -> nonExcludedDirectories.sortedByDescending { it.mediaCount }
                        SortByEnum.PATH_SORT -> nonExcludedDirectories.sortedByDescending { it.path.lowercase() }
                        SortByEnum.SIZE_SORT -> nonExcludedDirectories.sortedByDescending { it.mediaSize }
                        SortByEnum.DATE_SORT -> nonExcludedDirectories.sortedByDescending { it.dateModified }
                    }
                }
            }
        }.flowOn(defaultDispatcher)
    }
}
