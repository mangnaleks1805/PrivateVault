package com.privatevault.app.core.playback

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import com.privatevault.app.core.security.EncryptionService
import java.io.File
import java.io.IOException

/**
 * Custom AndroidX Media3 DataSource that plays encrypted videos directly in memory.
 * Never creates an unencrypted file on disk. Decrypts once into RAM and supports
 * instantaneous zero-latency random seeking across MP4 atoms.
 */
class EncryptedMediaDataSource(
    private val encryptedFile: File,
    private val dek: ByteArray,
    private val encryptionService: EncryptionService
) : BaseDataSource(/* isNetwork = */ false) {

    private var dataSpec: DataSpec? = null
    private var decryptedBytes: ByteArray? = null
    private var readPosition: Int = 0
    private var bytesRemaining: Int = 0
    private var opened = false

    private fun ensureDecrypted(): ByteArray {
        decryptedBytes?.let { return it }
        val bytes = encryptionService.decryptFileToBytes(encryptedFile, dek)
        decryptedBytes = bytes
        return bytes
    }

    override fun open(dataSpec: DataSpec): Long {
        this.dataSpec = dataSpec
        transferInitializing(dataSpec)

        try {
            val bytes = ensureDecrypted()
            val totalLength = bytes.size

            if (dataSpec.position > totalLength) {
                throw IOException("Position ${dataSpec.position} exceeds total media length $totalLength")
            }

            readPosition = dataSpec.position.toInt()
            val requestedLength = if (dataSpec.length != C.LENGTH_UNSET.toLong()) {
                dataSpec.length.toInt()
            } else {
                totalLength - readPosition
            }

            bytesRemaining = minOf(requestedLength, totalLength - readPosition)
            opened = true
            transferStarted(dataSpec)
            return bytesRemaining.toLong()
        } catch (e: Exception) {
            throw IOException("Error opening encrypted media data source", e)
        }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        if (bytesRemaining <= 0) return C.RESULT_END_OF_INPUT

        val bytes = decryptedBytes ?: return C.RESULT_END_OF_INPUT
        val bytesToRead = minOf(length, bytesRemaining)
        System.arraycopy(bytes, readPosition, buffer, offset, bytesToRead)
        readPosition += bytesToRead
        bytesRemaining -= bytesToRead

        bytesTransferred(bytesToRead)
        return bytesToRead
    }

    override fun getUri(): Uri? = dataSpec?.uri

    override fun close() {
        if (opened) {
            opened = false
            transferEnded()
        }
    }

    class Factory(
        private val encryptedFile: File,
        private val dek: ByteArray,
        private val encryptionService: EncryptionService
    ) : DataSource.Factory {
        override fun createDataSource(): DataSource {
            return EncryptedMediaDataSource(encryptedFile, dek, encryptionService)
        }
    }
}
