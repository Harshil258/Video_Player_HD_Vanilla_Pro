package com.vanillavideoplayer.hd.videoplayer.pro.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.vanillavideoplayer.hd.videoplayer.pro.core.database.dataclass.DirEntityData
import kotlinx.coroutines.flow.Flow

@Dao
interface DirDaoInter {

    @Upsert
    suspend fun add(directory: DirEntityData)

    @Upsert
    suspend fun addAll(directories: List<DirEntityData>)

    @Query("SELECT * FROM directories")
    fun fetchAll(): Flow<List<DirEntityData>>

    @Transaction
    suspend fun remove(paths: List<String>) {
        val batchSize = 1000 // Set the batch size according to your needs
        val chunkedPaths = paths.chunked(batchSize)
        for (chunk in chunkedPaths) {
            removeChunk(chunk)
        }
    }

    @Query("DELETE FROM directories WHERE path IN (:paths)")
    suspend fun removeChunk(paths: List<String>)
}
