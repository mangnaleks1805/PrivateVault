package com.privatevault.app.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SharingPermissionTest {

    @Test
    fun isViewable_activeValidPermission_returnsTrue() {
        val permission = SharingPermission(
            shareId = "share_001",
            ownerAccountId = "owner_a",
            recipientAccountId = "recipient_b",
            mediaId = "media_123",
            createdAt = System.currentTimeMillis() - 10_000,
            expiresAt = System.currentTimeMillis() + 3_600_000, // 1 hour in future
            isRevoked = false,
            maxViewSessions = 1,
            sessionsUsed = 0
        )

        assertFalse("Should not be expired", permission.isExpired)
        assertTrue("Should be viewable", permission.isViewable)
    }

    @Test
    fun isViewable_revokedPermission_returnsFalse() {
        val permission = SharingPermission(
            shareId = "share_002",
            ownerAccountId = "owner_a",
            recipientAccountId = "recipient_b",
            mediaId = "media_123",
            createdAt = System.currentTimeMillis() - 10_000,
            expiresAt = System.currentTimeMillis() + 3_600_000,
            isRevoked = true, // Revoked by owner
            maxViewSessions = 1,
            sessionsUsed = 0
        )

        assertFalse("Revoked permission must not be viewable", permission.isViewable)
    }

    @Test
    fun isViewable_expiredTimestamp_returnsFalse() {
        val permission = SharingPermission(
            shareId = "share_003",
            ownerAccountId = "owner_a",
            recipientAccountId = "recipient_b",
            mediaId = "media_123",
            createdAt = System.currentTimeMillis() - 7_200_000,
            expiresAt = System.currentTimeMillis() - 1_000, // Expired 1 second ago
            isRevoked = false,
            maxViewSessions = 5,
            sessionsUsed = 1
        )

        assertTrue("Must be marked expired", permission.isExpired)
        assertFalse("Expired permission must not be viewable", permission.isViewable)
    }

    @Test
    fun isViewable_sessionLimitReached_returnsFalse() {
        val permission = SharingPermission(
            shareId = "share_004",
            ownerAccountId = "owner_a",
            recipientAccountId = "recipient_b",
            mediaId = "media_123",
            createdAt = System.currentTimeMillis() - 10_000,
            expiresAt = System.currentTimeMillis() + 3_600_000,
            isRevoked = false,
            maxViewSessions = 1, // 1-view only
            sessionsUsed = 1    // Already viewed once
        )

        assertFalse("Permission with exhausted sessions must not be viewable", permission.isViewable)
    }

    @Test
    fun isViewable_unlimitedSessions_remainsViewableUntilExpiry() {
        val permission = SharingPermission(
            shareId = "share_005",
            ownerAccountId = "owner_a",
            recipientAccountId = "recipient_b",
            mediaId = "media_123",
            createdAt = System.currentTimeMillis() - 10_000,
            expiresAt = System.currentTimeMillis() + 86_400_000,
            isRevoked = false,
            maxViewSessions = null, // Unlimited
            sessionsUsed = 42
        )

        assertTrue("Unlimited sessions must be viewable if not expired and not revoked", permission.isViewable)
    }
}
