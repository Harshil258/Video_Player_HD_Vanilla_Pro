package com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo

import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ApplicationPrefData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.PlayerPref
import kotlinx.coroutines.flow.Flow

interface PrefRepo {

    val applicationPrefData: Flow<ApplicationPrefData>

    suspend fun updateApplicationPreferences(
        transform: suspend (ApplicationPrefData) -> ApplicationPrefData
    )

    val playerPref: Flow<PlayerPref>


    suspend fun updatePlayerPreferences(transform: suspend (PlayerPref) -> PlayerPref)
}
