package com.vanillavideoplayer.videoplayer.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.vanillavideoplayer.videoplayer.core.database.dataclass.MediumEntityData
import kotlinx.coroutines.flow.Flow

@Dao
interface MediumDaoInter {

    @Upsert
    suspend fun add(medium: MediumEntityData)

    @Upsert
    suspend fun addAll(media: List<MediumEntityData>)

    @Query("SELECT * FROM media WHERE path = :path")
    suspend fun fetch(path: String): MediumEntityData?

    @Query("SELECT * FROM media")
    fun fetchAll(): Flow<List<MediumEntityData>>

    @Query("SELECT * FROM media WHERE parent_path = :directoryPath")
    fun fetchAllFromDir(directoryPath: String): Flow<List<MediumEntityData>>

    @Transaction
    suspend fun remove(paths: List<String>) {
        val batchSize = 250
        val chunkedPaths = paths.chunked(batchSize)
        for (chunk in chunkedPaths) {
            removeChunk(chunk)
        }
    }

    @Query("DELETE FROM media WHERE path IN (:paths)")
    suspend fun removeChunk(paths: List<String>)

    @Query("UPDATE OR REPLACE media SET playback_position = :position, audio_track_index = :audioTrackIndex,lastPlayed = :lastPlayed, subtitle_track_index = :subtitleTrackIndex, playback_speed = :playbackSpeed, external_subs = :externalSubs WHERE path = :path")
    suspend fun changeMediumState(
        path: String,
        position: Long,
        audioTrackIndex: Int?,
        subtitleTrackIndex: Int?,
        playbackSpeed: Float?,
        externalSubs: String,
        lastPlayed: Long,
    )

    @Query("UPDATE OR REPLACE media SET lastPlayed = :lastPlayed WHERE path = :path")
    suspend fun changeLastPlayedState(
        path: String,
        lastPlayed: Long,
    )
}
