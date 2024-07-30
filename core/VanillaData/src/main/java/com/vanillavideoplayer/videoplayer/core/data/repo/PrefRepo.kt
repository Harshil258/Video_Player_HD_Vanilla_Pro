package com.vanillavideoplayer.videoplayer.core.data.repo

import com.vanillavideoplayer.videoplayer.core.model.ApplicationPrefData
import com.vanillavideoplayer.videoplayer.core.model.PlayerPref
import kotlinx.coroutines.flow.Flow

interface PrefRepo {

    val applicationPrefData: Flow<ApplicationPrefData>

    suspend fun updateApplicationPreferences(
        transform: suspend (ApplicationPrefData) -> ApplicationPrefData
    )

    val playerPref: Flow<PlayerPref>


    suspend fun updatePlayerPreferences(transform: suspend (PlayerPref) -> PlayerPref)
}
