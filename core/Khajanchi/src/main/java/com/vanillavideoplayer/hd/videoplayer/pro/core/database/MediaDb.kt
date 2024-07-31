package com.vanillavideoplayer.hd.videoplayer.pro.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vanillavideoplayer.hd.videoplayer.pro.core.database.dao.DirDaoInter
import com.vanillavideoplayer.hd.videoplayer.pro.core.database.dao.MediumDaoInter
import com.vanillavideoplayer.hd.videoplayer.pro.core.database.dataclass.DirEntityData
import com.vanillavideoplayer.hd.videoplayer.pro.core.database.dataclass.MediumEntityData

@Database(
    entities = [
        DirEntityData::class, MediumEntityData::class
    ],
    version = 1,
    exportSchema = true,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2),
//        AutoMigration(from = 3, to = 4)
//    ]
)
abstract class MediaDb : RoomDatabase() {

    abstract fun mediumDao(): MediumDaoInter

    abstract fun directoryDao(): DirDaoInter

    companion object {
        const val DB_NAME = "media_db_name"
    }
}
