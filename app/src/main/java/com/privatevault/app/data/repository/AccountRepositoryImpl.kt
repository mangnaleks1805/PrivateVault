package com.privatevault.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.privatevault.app.core.di.IoDispatcher
import com.privatevault.app.data.local.datastore.dataStore
import com.privatevault.app.data.local.db.dao.MediaItemDao
import com.privatevault.app.data.local.db.dao.SharingPermissionDao
import com.privatevault.app.data.local.db.dao.VaultFolderDao
import com.privatevault.app.domain.model.SubscriptionTier
import com.privatevault.app.domain.model.UserAccount
import com.privatevault.app.domain.repository.AccountRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaItemDao: MediaItemDao,
    private val vaultFolderDao: VaultFolderDao,
    private val sharingPermissionDao: SharingPermissionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AccountRepository {

    companion object {
        val KEY_USER_ID = stringPreferencesKey("account_user_id")
        val KEY_AUTH_ID = stringPreferencesKey("account_auth_id")
        val KEY_TIER = stringPreferencesKey("account_tier")
        val KEY_DEVICE_FP = stringPreferencesKey("account_device_fingerprint")
    }

    override val currentAccount: Flow<UserAccount?> = context.dataStore.data.map { prefs ->
        val userId = prefs[KEY_USER_ID] ?: return@map null
        val authId = prefs[KEY_AUTH_ID] ?: ""
        val tierStr = prefs[KEY_TIER] ?: SubscriptionTier.FREE.name
        val deviceFp = prefs[KEY_DEVICE_FP] ?: ""

        UserAccount(
            userId = userId,
            authId = authId,
            isAdultEligible = true,
            deviceFingerprint = deviceFp,
            createdAt = System.currentTimeMillis(),
            subscriptionTier = runCatching { SubscriptionTier.valueOf(tierStr) }.getOrDefault(SubscriptionTier.FREE)
        )
    }

    override suspend fun createAccount(authId: String): UserAccount = withContext(ioDispatcher) {
        val userId = UUID.randomUUID().toString()
        val deviceFp = UUID.randomUUID().toString()
        val tier = SubscriptionTier.FREE

        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
            prefs[KEY_AUTH_ID] = authId
            prefs[KEY_TIER] = tier.name
            prefs[KEY_DEVICE_FP] = deviceFp
        }

        UserAccount(
            userId = userId,
            authId = authId,
            isAdultEligible = true,
            deviceFingerprint = deviceFp,
            createdAt = System.currentTimeMillis(),
            subscriptionTier = tier
        )
    }

    override suspend fun upgradeSubscription(tier: SubscriptionTier): Unit = withContext(ioDispatcher) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TIER] = tier.name
        }
    }

    override suspend fun deleteAccountPermanently(deleteLocalVault: Boolean): Unit = withContext(ioDispatcher) {
        // 1. Clear account metadata
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_AUTH_ID)
            prefs.remove(KEY_TIER)
            prefs.remove(KEY_DEVICE_FP)
        }

        // 2. If requested, permanently erase local vault files
        if (deleteLocalVault) {
            val vaultDir = File(context.filesDir, "vault")
            vaultDir.listFiles()?.forEach { it.delete() }
            val thumbsDir = File(context.filesDir, "thumbs")
            thumbsDir.listFiles()?.forEach { it.delete() }
        }
    }
}
