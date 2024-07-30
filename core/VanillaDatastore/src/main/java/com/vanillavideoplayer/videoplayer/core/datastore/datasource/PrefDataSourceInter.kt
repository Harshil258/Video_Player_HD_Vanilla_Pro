package com.vanillavideoplayer.videoplayer.core.datastore.datasource

import kotlinx.coroutines.flow.Flow

interface PrefDataSourceInter<T> {

    suspend fun updateFun(transform: suspend (T) -> T)

    val preferences: Flow<T>

}
