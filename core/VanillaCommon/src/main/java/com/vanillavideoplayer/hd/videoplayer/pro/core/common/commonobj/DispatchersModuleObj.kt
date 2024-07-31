package com.vanillavideoplayer.hd.videoplayer.pro.core.common.commonobj

import com.vanillavideoplayer.hd.videoplayer.pro.core.common.Dispatcher
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.VideoPlayerDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModuleObj {

    @Provides
    @Dispatcher(VideoPlayerDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(VideoPlayerDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default


}
