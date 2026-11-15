package com.privatevault.app.domain.model

data class SharingPermission(
    val shareId: String,
    val ownerAccountId: String,
    val recipientAccountId: String,
    val mediaId: String,
    val permissionType: String = "VIEW_ONLY",
    val createdAt: Long,
    val expiresAt: Long,
    val isRevoked: Boolean,
    val maxViewSessions: Int?,
    val sessionsUsed: Int
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiresAt

    val isViewable: Boolean
        get() = !isRevoked && !isExpired && (maxViewSessions == null || sessionsUsed < maxViewSessions)
}
