package com.privatevault.app.domain.repository

import com.privatevault.app.domain.model.SubscriptionTier
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    val subscriptionTier: Flow<SubscriptionTier>

    suspend fun purchaseSubscription(tier: SubscriptionTier): Result<Boolean>

    suspend fun checkEntitlements(): SubscriptionTier

    fun canCreateSharePermission(currentSharesCount: Int, tier: SubscriptionTier): Boolean

    fun getMaxAllowedVideoLengthSeconds(tier: SubscriptionTier): Int
}
