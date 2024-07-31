package com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo

import com.vanillavideoplayer.hd.videoplayer.pro.core.datastore.datasource.AppPrefDataSourceInter
import com.vanillavideoplayer.hd.videoplayer.pro.core.datastore.datasource.PlayerPrefDataSourceInter
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ApplicationPrefData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.PlayerPref
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DesiPrefRepo @Inject constructor(
    private val appPreferencesDataSource: AppPrefDataSourceInter,
    private val playerPreferencesDataSource: PlayerPrefDataSourceInter
) : PrefRepo {
    override val applicationPrefData: Flow<ApplicationPrefData>
        get() = appPreferencesDataSource.preferences

    override suspend fun updateApplicationPreferences(
        transform: suspend (ApplicationPrefData) -> ApplicationPrefData
    ) {
        appPreferencesDataSource.updateFun(transform)
    }

    override val playerPref: Flow<PlayerPref>
        get() = playerPreferencesDataSource.preferences


    override suspend fun updatePlayerPreferences(
        transform: suspend (PlayerPref) -> PlayerPref
    ) {
        playerPreferencesDataSource.updateFun(transform)
    }
}
