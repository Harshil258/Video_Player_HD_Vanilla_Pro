package com.vanillavideoplayer.videoplayer.core.common.commonobj

import com.vanillavideoplayer.videoplayer.core.common.Dispatcher
import com.vanillavideoplayer.videoplayer.core.common.VideoPlayerDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope


@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopesModule {
    @Provides
    @Singleton
    @ApplicationScope
    fun providesCoroutineScope(
        @Dispatcher(VideoPlayerDispatchers.Default) dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}


