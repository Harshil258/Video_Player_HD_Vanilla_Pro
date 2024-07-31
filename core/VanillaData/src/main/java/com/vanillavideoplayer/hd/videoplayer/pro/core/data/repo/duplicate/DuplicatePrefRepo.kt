package com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo.duplicate

import com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo.PrefRepo
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ApplicationPrefData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.PlayerPref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DuplicatePrefRepo : PrefRepo {

    private val applicationPrefDataStateFlow = MutableStateFlow(ApplicationPrefData())
    override suspend fun updateApplicationPreferences(
        transform: suspend (ApplicationPrefData) -> ApplicationPrefData
    ) {
        applicationPrefDataStateFlow.update { transform.invoke(it) }
    }

    private val playerPrefStateFlow = MutableStateFlow(PlayerPref())

    override val applicationPrefData: Flow<ApplicationPrefData>
        get() = applicationPrefDataStateFlow

    override suspend fun updatePlayerPreferences(
        transform: suspend (PlayerPref) -> PlayerPref
    ) {
        playerPrefStateFlow.update { transform.invoke(it) }
    }

    override val playerPref: Flow<PlayerPref>
        get() = playerPrefStateFlow


}
