package com.privatevault.app.core.security

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Robust, production-grade AES-256-GCM encryption & decryption engine.
 *
 * Cryptographic Contract:
 * 1. Every media file is encrypted with a unique, randomly generated 256-bit AES DEK (Data Encryption Key).
 * 2. Every encryption operation uses a unique 96-bit (12-byte) initialization vector (IV).
 * 3. The raw DEK is never saved in plaintext; it is wrapped via hardware Android Keystore.
 * 4. GCM authenticated encryption (128-bit authentication tag) prevents tampering.
 * 5. File format on disk: [12-byte IV] + [AES-256-GCM Ciphertext + 16-byte Tag].
 * 6. Plaintext temp files are securely wiped and unlinked immediately after encryption.
 */
@Singleton
class EncryptionService @Inject constructor(
    private val keystoreManager: KeystoreManager,
    private val context: Context
) {

    companion object {
        const val ALGORITHM = "AES/GCM/NoPadding"
        const val KEY_ALGORITHM = "AES"
        const val DEK_SIZE_BYTES = 32  // 256 bits
        const val IV_SIZE_BYTES = 12   // 96 bits for GCM
        const val TAG_LENGTH_BITS = 128
        const val BUFFER_SIZE = 64 * 1024 // 64 KB buffer
    }

    private val secureRandom = SecureRandom()

    /** Generates a fresh cryptographically secure 256-bit DEK. */
    fun generateDEK(): ByteArray {
        val dek = ByteArray(DEK_SIZE_BYTES)
        secureRandom.nextBytes(dek)
        return dek
    }

    /** Generates a fresh random 96-bit IV for GCM. */
    fun generateIV(): ByteArray {
        val iv = ByteArray(IV_SIZE_BYTES)
        secureRandom.nextBytes(iv)
        return iv
    }

    /** Wraps a per-file DEK using the hardware Keystore master key. */
    fun wrapDEK(dek: ByteArray): ByteArray {
        return keystoreManager.wrapKey(dek)
    }

    /** Unwraps a wrapped DEK using the hardware Keystore master key. */
    fun unwrapDEK(wrappedDek: ByteArray): ByteArray {
        return keystoreManager.unwrapKey(wrappedDek)
    }

    /**
     * Encrypts a source file (e.g. from camera capture) into a target encrypted file.
     * Writes [12-byte IV] followed by the AES-GCM ciphertext + tag.
     */
    fun encryptFile(inputFile: File, outputFile: File, dek: ByteArray) {
        val iv = generateIV()
        val secretKey = SecretKeySpec(dek, KEY_ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        outputFile.parentFile?.mkdirs()

        FileOutputStream(outputFile).use { fos ->
            // 1. Write the 12-byte IV at the beginning of the file
            fos.write(iv)

            // 2. Stream ciphertext through CipherOutputStream
            CipherOutputStream(fos, cipher).use { cos ->
                FileInputStream(inputFile).use { fis ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var read: Int
                    while (fis.read(buffer).also { read = it } != -1) {
                        cos.write(buffer, 0, read)
                    }
                }
            }
        }
    }

    /**
     * Decrypts an encrypted file and returns an InputStream of plaintext bytes.
     * Reads the prepended 12-byte IV and initializes a CipherInputStream.
     */
    fun decryptFileToStream(encryptedFile: File, dek: ByteArray): InputStream {
        val fis = FileInputStream(encryptedFile)
        val iv = ByteArray(IV_SIZE_BYTES)
        val bytesRead = fis.read(iv)
        require(bytesRead == IV_SIZE_BYTES) { "Corrupt encrypted file: missing IV header" }

        val secretKey = SecretKeySpec(dek, KEY_ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        return CipherInputStream(fis, cipher)
    }

    /**
     * Encrypts an in-memory Bitmap directly to an encrypted thumbnail file.
     * No plaintext thumbnail is ever written to disk.
     */
    fun encryptBitmap(bitmap: Bitmap, outputFile: File, dek: ByteArray) {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bos)
        val plaintextBytes = bos.toByteArray()

        val iv = generateIV()
        val secretKey = SecretKeySpec(dek, KEY_ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        val encryptedBytes = cipher.doFinal(plaintextBytes)

        outputFile.parentFile?.mkdirs()
        FileOutputStream(outputFile).use { fos ->
            fos.write(iv)
            fos.write(encryptedBytes)
        }
    }

    /**
     * Decrypts an encrypted thumbnail file directly into an in-memory Bitmap.
     * Never writes plaintext to cache or disk.
     */
    fun decryptBitmap(encryptedFile: File, dek: ByteArray): Bitmap? {
        if (!encryptedFile.exists()) return null
        return runCatching {
            decryptFileToStream(encryptedFile, dek).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }.getOrNull()
    }

    /**
     * Securely wipes a temporary file by overwriting with zeros before deleting.
     */
    fun securelyDeleteTempFile(file: File) {
        if (!file.exists()) return
        runCatching {
            val length = file.length()
            if (length > 0) {
                FileOutputStream(file).use { fos ->
                    val zeros = ByteArray(BUFFER_SIZE.coerceAtMost(length.toInt()))
                    var remaining = length
                    while (remaining > 0) {
                        val toWrite = zeros.size.toLong().coerceAtMost(remaining).toInt()
                        fos.write(zeros, 0, toWrite)
                        remaining -= toWrite
                    }
                    fos.flush()
                }
            }
            file.delete()
        }
    }
}
