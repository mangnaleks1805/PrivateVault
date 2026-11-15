package com.privatevault.app.domain.model

enum class MediaType {
    PHOTO,
    VIDEO
}

data class MediaItem(
    val id: String,
    val folderId: String?,
    val mediaType: MediaType,
    val encryptedFilePath: String,
    val thumbnailPath: String?,
    val fileSizeBytes: Long,
    val durationMs: Long?,
    val createdAt: Long,
    val modifiedAt: Long
)
