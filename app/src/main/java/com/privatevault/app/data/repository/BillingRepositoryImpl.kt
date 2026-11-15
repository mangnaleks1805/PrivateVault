package com.privatevault.app.data.repository

import com.privatevault.app.core.di.IoDispatcher
import com.privatevault.app.domain.model.SubscriptionTier
import com.privatevault.app.domain.repository.AccountRepository
import com.privatevault.app.domain.repository.BillingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepositoryImpl @Inject constructor(
    private val accountRepository: AccountRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BillingRepository {

    override val subscriptionTier: Flow<SubscriptionTier> =
        accountRepository.currentAccount.map { account ->
            account?.subscriptionTier ?: SubscriptionTier.FREE
        }

    override suspend fun purchaseSubscription(tier: SubscriptionTier): Result<Boolean> =
        withContext(ioDispatcher) {
            runCatching {
                // In production, initiate Google Play BillingClient launchBillingFlow
                // For local secure test / architecture, activate the purchased tier
                accountRepository.upgradeSubscription(tier)
                true
            }
        }

    override suspend fun checkEntitlements(): SubscriptionTier = withContext(ioDispatcher) {
        // Query Purchases in production
        SubscriptionTier.FREE
    }

    override fun canCreateSharePermission(currentSharesCount: Int, tier: SubscriptionTier): Boolean {
        return when (tier) {
            SubscriptionTier.FREE -> currentSharesCount < 3 // Free tier allows up to 3 active shares
            SubscriptionTier.PREMIUM -> true // Unlimited shares
            SubscriptionTier.COUPLE -> true // Unlimited shares & coupled sync
        }
    }

    override fun getMaxAllowedVideoLengthSeconds(tier: SubscriptionTier): Int {
        return when (tier) {
            SubscriptionTier.FREE -> 120 // 2 minutes max per recording in free
            SubscriptionTier.PREMIUM -> 1800 // 30 minutes in premium
            SubscriptionTier.COUPLE -> 3600 // 60 minutes in couple tier
        }
    }
}
