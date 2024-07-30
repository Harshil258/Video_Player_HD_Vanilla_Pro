package com.vanillavideoplayer.videoplayer.core.database

import com.vanillavideoplayer.videoplayer.core.database.dao.DirDaoInter
import com.vanillavideoplayer.videoplayer.core.database.dao.MediumDaoInter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModuleObj {

    @Provides
    fun mediumDaoAapo(db: MediaDb): MediumDaoInter = db.mediumDao()

    @Provides
    fun directoryDaoAapo(db: MediaDb): DirDaoInter = db.directoryDao()
}
