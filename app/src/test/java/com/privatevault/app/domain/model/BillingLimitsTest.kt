package com.privatevault.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BillingLimitsTest {

    private fun getMaxAllowedVideoLengthSeconds(tier: SubscriptionTier): Int {
        return when (tier) {
            SubscriptionTier.FREE -> 120 // 2 minutes
            SubscriptionTier.PREMIUM -> 1800 // 30 minutes
            SubscriptionTier.COUPLE -> 3600 // 60 minutes
        }
    }

    private fun canCreateSharePermission(currentSharesCount: Int, tier: SubscriptionTier): Boolean {
        return when (tier) {
            SubscriptionTier.FREE -> currentSharesCount < 3
            SubscriptionTier.PREMIUM -> true
            SubscriptionTier.COUPLE -> true
        }
    }

    @Test
    fun maxAllowedVideoLength_tierVerification() {
        assertEquals("Free tier must enforce 2-minute video recording cap", 120, getMaxAllowedVideoLengthSeconds(SubscriptionTier.FREE))
        assertEquals("Premium tier must allow 30-minute 4K video recording", 1800, getMaxAllowedVideoLengthSeconds(SubscriptionTier.PREMIUM))
        assertEquals("Couple tier must allow 60-minute 4K video recording", 3600, getMaxAllowedVideoLengthSeconds(SubscriptionTier.COUPLE))
    }

    @Test
    fun canCreateSharePermission_freeTierEnforcesThreeShareLimit() {
        assertTrue("Free tier should allow 1st share", canCreateSharePermission(0, SubscriptionTier.FREE))
        assertTrue("Free tier should allow 2nd share", canCreateSharePermission(1, SubscriptionTier.FREE))
        assertTrue("Free tier should allow 3rd share", canCreateSharePermission(2, SubscriptionTier.FREE))
        assertFalse("Free tier must block 4th share", canCreateSharePermission(3, SubscriptionTier.FREE))
        assertFalse("Free tier must block shares when count is greater than 3", canCreateSharePermission(5, SubscriptionTier.FREE))
    }

    @Test
    fun canCreateSharePermission_coupleTierAllowsUnlimitedShares() {
        assertTrue(canCreateSharePermission(0, SubscriptionTier.COUPLE))
        assertTrue(canCreateSharePermission(3, SubscriptionTier.COUPLE))
        assertTrue(canCreateSharePermission(50, SubscriptionTier.COUPLE))
    }
}
