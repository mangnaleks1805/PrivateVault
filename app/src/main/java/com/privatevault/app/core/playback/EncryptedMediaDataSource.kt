package com.privatevault.app.core.playback

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import com.privatevault.app.core.security.EncryptionService
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Custom AndroidX Media3 DataSource that plays encrypted videos directly in memory.
 * Never creates an unencrypted file on disk.
 */
class EncryptedMediaDataSource(
    private val encryptedFile: File,
    private val dek: ByteArray,
    private val encryptionService: EncryptionService
) : BaseDataSource(/* isNetwork = */ false) {

    private var dataSpec: DataSpec? = null
    private var inputStream: InputStream? = null
    private var bytesRemaining: Long = 0
    private var opened = false

    override fun open(dataSpec: DataSpec): Long {
        this.dataSpec = dataSpec
        transferInitializing(dataSpec)

        try {
            // Decrypt stream in-memory directly
            val decryptedStream = encryptionService.decryptFileToStream(encryptedFile, dek)

            // Read into byte array in memory (protected under FLAG_SECURE)
            val bos = ByteArrayOutputStream()
            val buffer = ByteArray(32 * 1024)
            var read: Int
            while (decryptedStream.read(buffer).also { read = it } != -1) {
                bos.write(buffer, 0, read)
            }
            decryptedStream.close()

            val decryptedBytes = bos.toByteArray()
            val totalLength = decryptedBytes.size.toLong()

            val position = dataSpec.position
            val length = if (dataSpec.length != C.LENGTH_UNSET.toLong()) {
                dataSpec.length
            } else {
                totalLength - position
            }

            bytesRemaining = length
            val bais = ByteArrayInputStream(decryptedBytes)
            bais.skip(position)
            this.inputStream = bais

            opened = true
            transferStarted(dataSpec)
            return bytesRemaining
        } catch (e: Exception) {
            throw IOException("Error opening encrypted media data source", e)
        }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        if (bytesRemaining == 0L) return C.RESULT_END_OF_INPUT

        val bytesToRead = if (bytesRemaining == C.LENGTH_UNSET.toLong()) {
            length
        } else {
            Math.min(bytesRemaining, length.toLong()).toInt()
        }

        val bytesRead = inputStream?.read(buffer, offset, bytesToRead) ?: -1
        if (bytesRead == -1) {
            return C.RESULT_END_OF_INPUT
        }

        if (bytesRemaining != C.LENGTH_UNSET.toLong()) {
            bytesRemaining -= bytesRead.toLong()
        }

        bytesTransferred(bytesRead)
        return bytesRead
    }

    override fun getUri(): Uri? = dataSpec?.uri

    override fun close() {
        try {
            inputStream?.close()
        } finally {
            inputStream = null
            if (opened) {
                opened = false
                transferEnded()
            }
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
