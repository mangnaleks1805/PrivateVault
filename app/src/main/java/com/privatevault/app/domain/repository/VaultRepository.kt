package com.privatevault.app.domain.repository

import com.privatevault.app.domain.model.VaultFolder
import kotlinx.coroutines.flow.Flow

interface VaultRepository {
    fun getAllFolders(): Flow<List<VaultFolder>>
    suspend fun createFolder(name: String, colorIndex: Int, iconIndex: Int): String
    suspend fun deleteFolder(id: String)
}
