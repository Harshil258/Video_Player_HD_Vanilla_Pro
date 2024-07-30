package com.vanillavideoplayer.videoplayer.core.common

import javax.inject.Qualifier


enum class VideoPlayerDispatchers {
    Default, IO
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val niaDispatcher: VideoPlayerDispatchers)


