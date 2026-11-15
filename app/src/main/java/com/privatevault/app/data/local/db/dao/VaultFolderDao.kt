package com.privatevault.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.privatevault.app.data.local.db.entity.VaultFolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultFolderDao {

    @Query("SELECT * FROM vault_folders ORDER BY name ASC")
    fun getAllFolders(): Flow<List<VaultFolderEntity>>

    @Query("SELECT * FROM vault_folders WHERE id = :id")
    suspend fun getFolderById(id: String): VaultFolderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: VaultFolderEntity)

    @Update
    suspend fun updateFolder(folder: VaultFolderEntity)

    @Query("DELETE FROM vault_folders WHERE id = :id")
    suspend fun deleteFolder(id: String)
}
