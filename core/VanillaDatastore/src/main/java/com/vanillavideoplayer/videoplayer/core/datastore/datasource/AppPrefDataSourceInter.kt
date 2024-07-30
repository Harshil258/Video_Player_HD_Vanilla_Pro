package com.vanillavideoplayer.videoplayer.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.vanillavideoplayer.videoplayer.core.model.ApplicationPrefData
import timber.log.Timber
import javax.inject.Inject

class AppPrefDataSourceInter @Inject constructor(
    private val appPreferencesData: DataStore<ApplicationPrefData>
) : PrefDataSourceInter<ApplicationPrefData> {

    override val preferences = appPreferencesData.data

    override suspend fun updateFun(
        transform: suspend (ApplicationPrefData) -> ApplicationPrefData
    ) {
        try {
            appPreferencesData.updateData(transform)
        } catch (ioException: Exception) {
            Timber.tag("VanillaPlayerPreferences").e("Failed to update app preferences: $ioException")
        }
    }
}
