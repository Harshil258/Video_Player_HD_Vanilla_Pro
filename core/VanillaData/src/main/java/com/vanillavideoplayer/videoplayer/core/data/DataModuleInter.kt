package com.vanillavideoplayer.videoplayer.core.data

import com.vanillavideoplayer.videoplayer.core.data.repo.DesiMediaRepo
import com.vanillavideoplayer.videoplayer.core.data.repo.DesiPrefRepo
import com.vanillavideoplayer.videoplayer.core.data.repo.MediaRepo
import com.vanillavideoplayer.videoplayer.core.data.repo.PrefRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModuleInter {

    @Binds
    fun bindsMediaRepo(
        videoRepository: DesiMediaRepo
    ): MediaRepo

    @Binds
    fun bindsPrefRepo(
        preferencesRepository: DesiPrefRepo
    ): PrefRepo
}
