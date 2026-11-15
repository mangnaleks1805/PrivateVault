package com.privatevault.app.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sharing_permissions")
data class SharingPermissionEntity(
    @PrimaryKey
    @ColumnInfo(name = "share_id")
    val shareId: String,

    @ColumnInfo(name = "owner_account_id")
    val ownerAccountId: String,

    @ColumnInfo(name = "recipient_account_id")
    val recipientAccountId: String,

    @ColumnInfo(name = "media_id")
    val mediaId: String,

    @ColumnInfo(name = "permission_type")
    val permissionType: String = "VIEW_ONLY",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long,

    @ColumnInfo(name = "is_revoked")
    val isRevoked: Boolean = false,

    @ColumnInfo(name = "revoked_at")
    val revokedAt: Long? = null,

    @ColumnInfo(name = "max_view_sessions")
    val maxViewSessions: Int? = null,

    @ColumnInfo(name = "sessions_used")
    val sessionsUsed: Int = 0
)
