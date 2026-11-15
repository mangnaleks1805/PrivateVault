package com.privatevault.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    val isOnboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)

    val is18PlusConfirmed: Flow<Boolean>
    suspend fun set18PlusConfirmed(confirmed: Boolean)

    val isVaultLocked: Flow<Boolean>
    suspend fun setVaultLocked(locked: Boolean)
}
