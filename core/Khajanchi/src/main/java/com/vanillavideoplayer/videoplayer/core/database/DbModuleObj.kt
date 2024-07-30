package com.vanillavideoplayer.videoplayer.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModuleObj {

    @Singleton
    @Provides
    fun provideMediaDb(
        @ApplicationContext context: Context
    ): MediaDb = Room.databaseBuilder(
        context,
        MediaDb::class.java,
        MediaDb.DB_NAME
    ).fallbackToDestructiveMigration().build()
}
