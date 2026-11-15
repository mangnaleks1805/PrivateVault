package com.privatevault.app.data.repository

import com.privatevault.app.data.local.datastore.OnboardingPreferences
import com.privatevault.app.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepositoryImpl @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences
) : PreferenceRepository {

    override val isOnboardingCompleted: Flow<Boolean> = onboardingPreferences.isOnboardingCompleted

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        onboardingPreferences.setOnboardingCompleted(completed)
    }

    override val is18PlusConfirmed: Flow<Boolean> = onboardingPreferences.is18PlusConfirmed

    override suspend fun set18PlusConfirmed(confirmed: Boolean) {
        onboardingPreferences.set18PlusConfirmed(confirmed)
    }

    override val isVaultLocked: Flow<Boolean> = onboardingPreferences.isVaultLocked

    override suspend fun setVaultLocked(locked: Boolean) {
        onboardingPreferences.setVaultLocked(locked)
    }
}
