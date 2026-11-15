package com.privatevault.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "private_vault_prefs")

@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("key_onboarding_completed")
        val KEY_18_PLUS_CONFIRMED = booleanPreferencesKey("key_18_plus_confirmed")
        val KEY_VAULT_LOCKED = booleanPreferencesKey("key_vault_locked")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    val is18PlusConfirmed: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_18_PLUS_CONFIRMED] ?: false
    }

    suspend fun set18PlusConfirmed(confirmed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_18_PLUS_CONFIRMED] = confirmed
        }
    }

    val isVaultLocked: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_VAULT_LOCKED] ?: true
    }

    suspend fun setVaultLocked(locked: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_VAULT_LOCKED] = locked
        }
    }
}
