package com.privatevault.app.domain.repository

import android.graphics.Bitmap
import com.privatevault.app.core.camera.PrivacyFilter
import com.privatevault.app.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MediaRepository {
    fun getAllActiveMedia(): Flow<List<MediaItem>>
    fun getMediaByFolder(folderId: String): Flow<List<MediaItem>>
    suspend fun getMediaById(id: String): MediaItem?
    suspend fun deleteMedia(id: String)
    fun getTotalVaultSizeBytes(): Flow<Long?>

    suspend fun saveCapturedPhoto(
        tempFile: File,
        folderId: String? = null,
        filter: PrivacyFilter = PrivacyFilter.NONE
    ): MediaItem
    suspend fun saveCapturedVideo(tempFile: File, durationMs: Long, folderId: String? = null): MediaItem

    suspend fun loadThumbnail(mediaItem: MediaItem): Bitmap?
    suspend fun loadFullPhotoBitmap(mediaItem: MediaItem): Bitmap?
    suspend fun getMediaDEK(mediaItem: MediaItem): ByteArray?
    fun getEncryptedFile(mediaItem: MediaItem): File
}
