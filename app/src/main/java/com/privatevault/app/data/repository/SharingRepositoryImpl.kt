package com.privatevault.app.data.repository

import com.privatevault.app.core.di.IoDispatcher
import com.privatevault.app.data.local.db.dao.SharingPermissionDao
import com.privatevault.app.data.local.db.entity.SharingPermissionEntity
import com.privatevault.app.domain.model.SharingPermission
import com.privatevault.app.domain.repository.AccountRepository
import com.privatevault.app.domain.repository.SharingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharingRepositoryImpl @Inject constructor(
    private val sharingPermissionDao: SharingPermissionDao,
    private val accountRepository: AccountRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SharingRepository {

    override val activePermissions: Flow<List<SharingPermission>> =
        sharingPermissionDao.getActivePermissions().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getPermissionById(shareId: String): SharingPermission? =
        withContext(ioDispatcher) {
            sharingPermissionDao.getPermissionById(shareId)?.toDomain()
        }

    override suspend fun createInvitation(
        mediaId: String,
        recipientAccountId: String,
        durationHours: Long,
        maxSessions: Int?
    ): Result<SharingPermission> = withContext(ioDispatcher) {
        runCatching {
            var currentAccount = accountRepository.currentAccount.firstOrNull()
            if (currentAccount == null) {
                currentAccount = accountRepository.createAccount("anonymous_owner")
            }

            val shareId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val expiresAt = now + (durationHours * 3600 * 1000)

            val entity = SharingPermissionEntity(
                shareId = shareId,
                ownerAccountId = currentAccount.userId,
                recipientAccountId = recipientAccountId.trim(),
                mediaId = mediaId,
                permissionType = "VIEW_ONLY",
                createdAt = now,
                expiresAt = expiresAt,
                isRevoked = false,
                revokedAt = null,
                maxViewSessions = maxSessions,
                sessionsUsed = 0
            )

            sharingPermissionDao.insertPermission(entity)
            entity.toDomain()
        }
    }

    override suspend fun revokePermission(shareId: String): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                sharingPermissionDao.revokePermission(shareId, System.currentTimeMillis())
            }
        }

    override suspend fun recordViewingSession(shareId: String): Result<Boolean> =
        withContext(ioDispatcher) {
            runCatching {
                val entity = sharingPermissionDao.getPermissionById(shareId)
                    ?: return@runCatching false

                val domain = entity.toDomain()
                if (!domain.isViewable) {
                    return@runCatching false
                }

                val updated = entity.copy(sessionsUsed = entity.sessionsUsed + 1)
                sharingPermissionDao.updatePermission(updated)
                true
            }
        }

    private fun SharingPermissionEntity.toDomain(): SharingPermission {
        return SharingPermission(
            shareId = shareId,
            ownerAccountId = ownerAccountId,
            recipientAccountId = recipientAccountId,
            mediaId = mediaId,
            permissionType = permissionType,
            createdAt = createdAt,
            expiresAt = expiresAt,
            isRevoked = isRevoked,
            maxViewSessions = maxViewSessions,
            sessionsUsed = sessionsUsed
        )
    }
}
