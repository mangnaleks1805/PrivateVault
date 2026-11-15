package com.privatevault.app.domain.repository

import com.privatevault.app.domain.model.SubscriptionTier
import com.privatevault.app.domain.model.UserAccount
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val currentAccount: Flow<UserAccount?>
    suspend fun createAccount(authId: String): UserAccount
    suspend fun upgradeSubscription(tier: SubscriptionTier)
    suspend fun deleteAccountPermanently(deleteLocalVault: Boolean)
}
