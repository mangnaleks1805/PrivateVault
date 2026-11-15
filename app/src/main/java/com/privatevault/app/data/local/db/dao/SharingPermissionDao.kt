package com.privatevault.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.privatevault.app.data.local.db.entity.SharingPermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SharingPermissionDao {

    @Query("SELECT * FROM sharing_permissions WHERE is_revoked = 0 ORDER BY created_at DESC")
    fun getActivePermissions(): Flow<List<SharingPermissionEntity>>

    @Query("SELECT * FROM sharing_permissions WHERE share_id = :shareId")
    suspend fun getPermissionById(shareId: String): SharingPermissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermission(permission: SharingPermissionEntity)

    @Update
    suspend fun updatePermission(permission: SharingPermissionEntity)

    @Query("UPDATE sharing_permissions SET is_revoked = 1, revoked_at = :timestamp WHERE share_id = :shareId")
    suspend fun revokePermission(shareId: String, timestamp: Long = System.currentTimeMillis())
}
