package com.privatevault.app.core.di

import android.content.Context
import androidx.room.Room
import com.privatevault.app.data.local.db.VaultDatabase
import com.privatevault.app.data.local.db.dao.MediaItemDao
import com.privatevault.app.data.local.db.dao.SharingPermissionDao
import com.privatevault.app.data.local.db.dao.VaultFolderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideVaultDatabase(
        @ApplicationContext context: Context
    ): VaultDatabase {
        return Room.databaseBuilder(
            context,
            VaultDatabase::class.java,
            VaultDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMediaItemDao(db: VaultDatabase): MediaItemDao = db.mediaItemDao()

    @Provides
    fun provideVaultFolderDao(db: VaultDatabase): VaultFolderDao = db.vaultFolderDao()

    @Provides
    fun provideSharingPermissionDao(db: VaultDatabase): SharingPermissionDao = db.sharingPermissionDao()
}
