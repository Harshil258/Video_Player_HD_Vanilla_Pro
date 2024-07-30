package com.vanillavideoplayer.videoplayer.core.media

import com.vanillavideoplayer.videoplayer.core.media.sync.DesiMediaSynchronizerInter
import com.vanillavideoplayer.videoplayer.core.media.sync.MediaSynchronizerInter
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
