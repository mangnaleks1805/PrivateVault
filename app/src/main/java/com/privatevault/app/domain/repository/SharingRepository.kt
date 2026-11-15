package com.privatevault.app.domain.repository

import com.privatevault.app.domain.model.SharingPermission
import kotlinx.coroutines.flow.Flow

interface SharingRepository {
    val activePermissions: Flow<List<SharingPermission>>

    suspend fun getPermissionById(shareId: String): SharingPermission?

    suspend fun createInvitation(
        mediaId: String,
        recipientAccountId: String,
        durationHours: Long,
        maxSessions: Int?
    ): Result<SharingPermission>

    suspend fun revokePermission(shareId: String): Result<Unit>

    suspend fun recordViewingSession(shareId: String): Result<Boolean>
}
