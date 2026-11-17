package com.privatevault.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import com.privatevault.app.core.camera.PrivacyFilter
import com.privatevault.app.core.camera.PrivacyFilterProcessor
import com.privatevault.app.core.di.IoDispatcher
import com.privatevault.app.core.security.EncryptionService
import com.privatevault.app.core.security.KeystoreManager
import com.privatevault.app.data.local.db.dao.MediaItemDao
import com.privatevault.app.data.local.db.entity.MediaItemEntity
import com.privatevault.app.domain.model.MediaItem
import com.privatevault.app.domain.model.MediaType
import com.privatevault.app.domain.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    private val encryptionService: EncryptionService,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MediaRepository {

    override fun getAllActiveMedia(): Flow<List<MediaItem>> {
        return mediaItemDao.getAllActiveMedia().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getMediaByFolder(folderId: String): Flow<List<MediaItem>> {
        return mediaItemDao.getMediaByFolder(folderId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMediaById(id: String): MediaItem? = withContext(ioDispatcher) {
        mediaItemDao.getMediaById(id)?.toDomain()
    }

    override suspend fun deleteMedia(id: String): Unit = withContext(ioDispatcher) {
        val entity = mediaItemDao.getMediaById(id)
        if (entity != null) {
            mediaItemDao.softDeleteMedia(id)

            val encryptedMediaFile = File(context.filesDir, entity.encryptedFilePath)
            encryptionService.securelyDeleteTempFile(encryptedMediaFile)

            entity.thumbnailPath?.let { thumbPath ->
                val encryptedThumbFile = File(context.filesDir, thumbPath)
                encryptionService.securelyDeleteTempFile(encryptedThumbFile)
            }

            mediaItemDao.permanentlyDeleteMedia(id)
        }
    }

    override fun getTotalVaultSizeBytes(): Flow<Long?> {
        return mediaItemDao.getTotalVaultSizeBytes()
    }

    override suspend fun saveCapturedPhoto(
        tempFile: File,
        folderId: String?,
        filter: PrivacyFilter
    ): MediaItem = withContext(ioDispatcher) {
        // Apply offline privacy/discretion filter before hardware encryption
        if (filter != PrivacyFilter.ORIGINAL) {
            runCatching {
                val rawBitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                if (rawBitmap != null) {
                    val filteredBitmap = PrivacyFilterProcessor.applyFilter(rawBitmap, filter)
                    FileOutputStream(tempFile).use { out ->
                        filteredBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                    }
                    rawBitmap.recycle()
                    if (filteredBitmap != rawBitmap) {
                        filteredBitmap.recycle()
                    }
                }
            }
        }

        val id = UUID.randomUUID().toString()
        val dek = encryptionService.generateDEK()
        val wrappedDek = encryptionService.wrapDEK(dek)

        val encryptedRelativePath = "vault/$id.enc"
        val encryptedFile = File(context.filesDir, encryptedRelativePath)

        encryptionService.encryptFile(tempFile, encryptedFile, dek)

        val thumbRelativePath = "thumbs/${id}_thumb.enc"
        val thumbFile = File(context.filesDir, thumbRelativePath)
        runCatching {
            // Ultra-fast thumbnail extraction via downsampling (avoids loading massive full camera resolution into RAM)
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(tempFile.absolutePath, boundsOptions)
            var sampleSize = 1
            val targetSize = 320
            while (boundsOptions.outWidth / (sampleSize * 2) >= targetSize &&
                boundsOptions.outHeight / (sampleSize * 2) >= targetSize
            ) {
                sampleSize *= 2
            }
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            val downsampled = BitmapFactory.decodeFile(tempFile.absolutePath, decodeOptions)
            if (downsampled != null) {
                val scaled = Bitmap.createScaledBitmap(downsampled, 320, (320f / downsampled.width * downsampled.height).toInt().coerceAtLeast(1), true)
                encryptionService.encryptBitmap(scaled, thumbFile, dek)
                downsampled.recycle()
                if (scaled != downsampled) {
                    scaled.recycle()
                }
            }
        }

        encryptionService.securelyDeleteTempFile(tempFile)

        val entity = MediaItemEntity(
            id = id,
            folderId = folderId,
            mediaType = "PHOTO",
            encryptedFilePath = encryptedRelativePath,
            thumbnailPath = if (thumbFile.exists()) thumbRelativePath else null,
            encryptedDekBlob = wrappedDek,
            dekKeyAlias = KeystoreManager.MASTER_KEY_ALIAS,
            fileSizeBytes = encryptedFile.length(),
            durationMs = null,
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )
        mediaItemDao.insertMedia(entity)

        entity.toDomain()
    }

    override suspend fun saveCapturedVideo(tempFile: File, durationMs: Long, folderId: String?): MediaItem = withContext(ioDispatcher) {
        val id = UUID.randomUUID().toString()
        val dek = encryptionService.generateDEK()
        val wrappedDek = encryptionService.wrapDEK(dek)

        val encryptedRelativePath = "vault/$id.enc"
        val encryptedFile = File(context.filesDir, encryptedRelativePath)

        val thumbRelativePath = "thumbs/${id}_thumb.enc"
        val thumbFile = File(context.filesDir, thumbRelativePath)
        runCatching {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(tempFile.absolutePath)
            val frame = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()
            if (frame != null) {
                val scaled = Bitmap.createScaledBitmap(frame, 320, (320f / frame.width * frame.height).toInt().coerceAtLeast(1), true)
                encryptionService.encryptBitmap(scaled, thumbFile, dek)
                frame.recycle()
                scaled.recycle()
            }
        }

        encryptionService.encryptFile(tempFile, encryptedFile, dek)
        encryptionService.securelyDeleteTempFile(tempFile)

        val entity = MediaItemEntity(
            id = id,
            folderId = folderId,
            mediaType = "VIDEO",
            encryptedFilePath = encryptedRelativePath,
            thumbnailPath = if (thumbFile.exists()) thumbRelativePath else null,
            encryptedDekBlob = wrappedDek,
            dekKeyAlias = KeystoreManager.MASTER_KEY_ALIAS,
            fileSizeBytes = encryptedFile.length(),
            durationMs = durationMs,
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )
        mediaItemDao.insertMedia(entity)

        entity.toDomain()
    }

    override suspend fun loadThumbnail(mediaItem: MediaItem): Bitmap? = withContext(ioDispatcher) {
        val thumbPath = mediaItem.thumbnailPath ?: return@withContext null
        val thumbFile = File(context.filesDir, thumbPath)
        if (!thumbFile.exists()) return@withContext null

        val entity = mediaItemDao.getMediaById(mediaItem.id) ?: return@withContext null
        val dek = encryptionService.unwrapDEK(entity.encryptedDekBlob)
        encryptionService.decryptBitmap(thumbFile, dek)
    }

    override suspend fun loadFullPhotoBitmap(mediaItem: MediaItem): Bitmap? = withContext(ioDispatcher) {
        val encryptedFile = File(context.filesDir, mediaItem.encryptedFilePath)
        if (!encryptedFile.exists()) return@withContext null

        val entity = mediaItemDao.getMediaById(mediaItem.id) ?: return@withContext null
        val dek = encryptionService.unwrapDEK(entity.encryptedDekBlob)
        runCatching {
            val decryptedBytes = encryptionService.decryptFileToBytes(encryptedFile, dek)
            BitmapFactory.decodeByteArray(decryptedBytes, 0, decryptedBytes.size)
        }.getOrNull()
    }

    override suspend fun getMediaDEK(mediaItem: MediaItem): ByteArray? = withContext(ioDispatcher) {
        val entity = mediaItemDao.getMediaById(mediaItem.id) ?: return@withContext null
        encryptionService.unwrapDEK(entity.encryptedDekBlob)
    }

    override fun getEncryptedFile(mediaItem: MediaItem): File {
        return File(context.filesDir, mediaItem.encryptedFilePath)
    }

    private fun MediaItemEntity.toDomain(): MediaItem {
        return MediaItem(
            id = id,
            folderId = folderId,
            mediaType = if (mediaType == "VIDEO") MediaType.VIDEO else MediaType.PHOTO,
            encryptedFilePath = encryptedFilePath,
            thumbnailPath = thumbnailPath,
            fileSizeBytes = fileSizeBytes,
            durationMs = durationMs,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }
}
