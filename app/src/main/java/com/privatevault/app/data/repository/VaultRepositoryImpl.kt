package com.privatevault.app.data.repository

import com.privatevault.app.data.local.db.dao.VaultFolderDao
import com.privatevault.app.data.local.db.entity.VaultFolderEntity
import com.privatevault.app.domain.model.VaultFolder
import com.privatevault.app.domain.repository.VaultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultRepositoryImpl @Inject constructor(
    private val vaultFolderDao: VaultFolderDao
) : VaultRepository {

    override fun getAllFolders(): Flow<List<VaultFolder>> {
        return vaultFolderDao.getAllFolders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createFolder(name: String, colorIndex: Int, iconIndex: Int): String {
        val id = UUID.randomUUID().toString()
        val entity = VaultFolderEntity(
            id = id,
            name = name,
            colorIndex = colorIndex,
            iconIndex = iconIndex,
            createdAt = System.currentTimeMillis(),
            itemCount = 0
        )
        vaultFolderDao.insertFolder(entity)
        return id
    }

    override suspend fun deleteFolder(id: String) {
        vaultFolderDao.deleteFolder(id)
    }

    private fun VaultFolderEntity.toDomain(): VaultFolder {
        return VaultFolder(
            id = id,
            name = name,
            colorIndex = colorIndex,
            iconIndex = iconIndex,
            createdAt = createdAt,
            itemCount = itemCount
        )
    }
}
