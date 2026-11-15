package com.privatevault.app.core.di

import android.content.Context
import com.privatevault.app.core.security.KeystoreManager
import com.privatevault.app.core.security.EncryptionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideKeystoreManager(): KeystoreManager {
        return KeystoreManager()
    }

    @Provides
    @Singleton
    fun provideEncryptionService(
        keystoreManager: KeystoreManager,
        @ApplicationContext context: Context
    ): EncryptionService {
        return EncryptionService(keystoreManager, context)
    }
}
