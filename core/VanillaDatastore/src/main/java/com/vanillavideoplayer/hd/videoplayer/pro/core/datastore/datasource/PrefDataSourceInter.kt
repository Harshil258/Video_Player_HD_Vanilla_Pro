package com.vanillavideoplayer.hd.videoplayer.pro.core.datastore.datasource

import kotlinx.coroutines.flow.Flow

interface PrefDataSourceInter<T> {

    suspend fun updateFun(transform: suspend (T) -> T)

    val preferences: Flow<T>

}
