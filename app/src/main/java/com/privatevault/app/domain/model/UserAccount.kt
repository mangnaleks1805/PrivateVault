package com.privatevault.app.domain.model

enum class SubscriptionTier {
    FREE,
    PREMIUM,
    COUPLE
}

data class UserAccount(
    val userId: String,
    val authId: String,
    val isAdultEligible: Boolean,
    val deviceFingerprint: String,
    val createdAt: Long,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val subscriptionExpiresAt: Long? = null
)
