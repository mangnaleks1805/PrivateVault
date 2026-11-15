package com.privatevault.app.core.di

import com.privatevault.app.data.repository.AccountRepositoryImpl
import com.privatevault.app.data.repository.BillingRepositoryImpl
import com.privatevault.app.data.repository.MediaRepositoryImpl
import com.privatevault.app.data.repository.PreferenceRepositoryImpl
import com.privatevault.app.data.repository.SharingRepositoryImpl
import com.privatevault.app.data.repository.VaultRepositoryImpl
import com.privatevault.app.domain.repository.AccountRepository
import com.privatevault.app.domain.repository.BillingRepository
import com.privatevault.app.domain.repository.MediaRepository
import com.privatevault.app.domain.repository.PreferenceRepository
import com.privatevault.app.domain.repository.SharingRepository
import com.privatevault.app.domain.repository.VaultRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        impl: MediaRepositoryImpl
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindVaultRepository(
        impl: VaultRepositoryImpl
    ): VaultRepository

    @Binds
    @Singleton
    abstract fun bindPreferenceRepository(
        impl: PreferenceRepositoryImpl
    ): PreferenceRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        impl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindSharingRepository(
        impl: SharingRepositoryImpl
    ): SharingRepository

    @Binds
    @Singleton
    abstract fun bindBillingRepository(
        impl: BillingRepositoryImpl
    ): BillingRepository
}
