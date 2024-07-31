package com.vanillavideoplayer.hd.videoplayer.pro.core.common

import javax.inject.Qualifier


enum class VideoPlayerDispatchers {
    Default, IO
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val niaDispatcher: VideoPlayerDispatchers)


