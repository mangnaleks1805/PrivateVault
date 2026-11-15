package com.privatevault.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.privatevault.app.data.local.db.entity.MediaItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaItemDao {

    @Query("SELECT * FROM media_items WHERE is_deleted = 0 ORDER BY created_at DESC")
    fun getAllActiveMedia(): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE folder_id = :folderId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getMediaByFolder(folderId: String): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE id = :id AND is_deleted = 0")
    suspend fun getMediaById(id: String): MediaItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaItemEntity)

    @Update
    suspend fun updateMedia(media: MediaItemEntity)

    @Query("UPDATE media_items SET is_deleted = 1, deleted_at = :timestamp WHERE id = :id")
    suspend fun softDeleteMedia(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun permanentlyDeleteMedia(id: String)

    @Query("SELECT SUM(file_size_bytes) FROM media_items WHERE is_deleted = 0")
    fun getTotalVaultSizeBytes(): Flow<Long?>
}
