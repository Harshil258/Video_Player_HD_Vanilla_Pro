package com.vanillavideoplayer.videoplayer.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.vanillavideoplayer.videoplayer.core.model.PlayerPref
import timber.log.Timber
import javax.inject.Inject

class PlayerPrefDataSourceInter @Inject constructor(
    private val preferencesDataStore: DataStore<PlayerPref>
) : PrefDataSourceInter<PlayerPref> {

    override val preferences = preferencesDataStore.data

    override suspend fun updateFun(transform: suspend (PlayerPref) -> PlayerPref) {
        try {
            preferencesDataStore.updateData(transform)
        } catch (ioException: Exception) {
            Timber.tag("VanillaPlayerPreferences").e("Failed to update app preferences: $ioException")
        }
    }
}
