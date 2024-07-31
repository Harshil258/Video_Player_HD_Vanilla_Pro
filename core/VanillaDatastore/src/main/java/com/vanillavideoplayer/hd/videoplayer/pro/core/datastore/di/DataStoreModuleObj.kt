package com.vanillavideoplayer.hd.videoplayer.pro.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.Dispatcher
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.VideoPlayerDispatchers
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.commonobj.ApplicationScope
import com.vanillavideoplayer.hd.videoplayer.pro.core.datastore.serializer.AppPrefSerializer
import com.vanillavideoplayer.hd.videoplayer.pro.core.datastore.serializer.PlayerPrefSerializerObj
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ApplicationPrefData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.PlayerPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

private const val APP_PREF_DATASTORE_FILE = "app_preferences.json"
private const val PLAYER_PREF_DATASTORE_FILE = "player_preferences.json"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModuleObj {

    @Provides
    @Singleton
    fun provideAppPrefDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(VideoPlayerDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope
    ): DataStore<ApplicationPrefData> {
        return DataStoreFactory.create(
            serializer = AppPrefSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
            produceFile = { context.dataStoreFile(APP_PREF_DATASTORE_FILE) }
        )
    }

    @Provides
    @Singleton
    fun providePlayerPrefDataStore(
        @ApplicationContext applicationContext: Context,
        @Dispatcher(VideoPlayerDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope
    ): DataStore<PlayerPref> {
        return DataStoreFactory.create(
            serializer = PlayerPrefSerializerObj,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
            produceFile = { applicationContext.dataStoreFile(PLAYER_PREF_DATASTORE_FILE) }
        )
    }
}
