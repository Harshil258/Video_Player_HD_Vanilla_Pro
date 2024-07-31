package com.vanillavideoplayer.hd.videoplayer.pro.core.media

import com.vanillavideoplayer.hd.videoplayer.pro.core.media.sync.DesiMediaSynchronizerInter
import com.vanillavideoplayer.hd.videoplayer.pro.core.media.sync.MediaSynchronizerInter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface MediaModuleInter {

    @Binds
    @Singleton
    fun jointMediaSynchronizer(
        mediaSynchronizer: DesiMediaSynchronizerInter
    ): MediaSynchronizerInter
}
