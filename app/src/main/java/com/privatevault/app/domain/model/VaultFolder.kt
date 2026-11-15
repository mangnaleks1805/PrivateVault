package com.privatevault.app.domain.model

data class VaultFolder(
    val id: String,
    val name: String,
    val colorIndex: Int,
    val iconIndex: Int,
    val createdAt: Long,
    val itemCount: Int
)
