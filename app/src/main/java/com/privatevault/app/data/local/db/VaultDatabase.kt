package com.privatevault.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.privatevault.app.data.local.db.dao.MediaItemDao
import com.privatevault.app.data.local.db.dao.SharingPermissionDao
import com.privatevault.app.data.local.db.dao.VaultFolderDao
import com.privatevault.app.data.local.db.entity.MediaItemEntity
import com.privatevault.app.data.local.db.entity.SharingPermissionEntity
import com.privatevault.app.data.local.db.entity.VaultFolderEntity

@Database(
    entities = [
        MediaItemEntity::class,
        VaultFolderEntity::class,
        SharingPermissionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun vaultFolderDao(): VaultFolderDao
    abstract fun sharingPermissionDao(): SharingPermissionDao

    companion object {
        const val DATABASE_NAME = "vault_database"
    }
}
