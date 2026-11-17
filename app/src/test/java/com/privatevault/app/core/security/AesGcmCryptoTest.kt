package com.privatevault.app.core.security

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.SecureRandom
import java.util.Arrays
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class AesGcmCryptoTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val secureRandom = SecureRandom()

    private fun generateDEK(): ByteArray {
        val dek = ByteArray(32)
        secureRandom.nextBytes(dek)
        return dek
    }

    private fun generateIV(): ByteArray {
        val iv = ByteArray(12)
        secureRandom.nextBytes(iv)
        return iv
    }

    @Test
    fun aesGcmEncryptDecrypt_roundtripRestoresExactPlaintext() {
        val dek = generateDEK()
        val originalData = "Antigravity PrivateVault Encrypted Payload String \u2014 Confidential Memory".toByteArray(Charsets.UTF_8)

        val inputFile = tempFolder.newFile("sample_input.dat")
        inputFile.writeBytes(originalData)

        val encryptedFile = tempFolder.newFile("sample_encrypted.vault")

        // Encrypt with AES-256-GCM
        val iv = generateIV()
        val secretKey = SecretKeySpec(dek, "AES")
        val cipherEncrypt = Cipher.getInstance("AES/GCM/NoPadding")
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

        FileOutputStream(encryptedFile).use { fos ->
            fos.write(iv)
            CipherOutputStream(fos, cipherEncrypt).use { cos ->
                cos.write(originalData)
            }
        }

        // Decrypt
        FileInputStream(encryptedFile).use { fis ->
            val readIv = ByteArray(12)
            val ivBytes = fis.read(readIv)
            assertEquals(12, ivBytes)
            assertArrayEquals("Extracted IV must match original IV", iv, readIv)

            val cipherDecrypt = Cipher.getInstance("AES/GCM/NoPadding")
            cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, readIv))

            val decryptedBytes = CipherInputStream(fis, cipherDecrypt).use { cis ->
                cis.readBytes()
            }

            assertArrayEquals("Decrypted data must match original plaintext exactly", originalData, decryptedBytes)
        }
    }

    @Test
    fun aesGcmTamperDetection_modifyingOneByteCausesDecryptionFailure() {
        val dek = generateDEK()
        val originalData = "Ultra Sensitive Couple Video Frame Bytes".toByteArray(Charsets.UTF_8)

        val encryptedFile = tempFolder.newFile("tampered_media.vault")

        val iv = generateIV()
        val secretKey = SecretKeySpec(dek, "AES")
        val cipherEncrypt = Cipher.getInstance("AES/GCM/NoPadding")
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

        FileOutputStream(encryptedFile).use { fos ->
            fos.write(iv)
            CipherOutputStream(fos, cipherEncrypt).use { cos ->
                cos.write(originalData)
            }
        }

        // Tamper with the ciphertext by flipping a single bit
        val rawBytes = encryptedFile.readBytes()
        assertTrue("File must contain IV + ciphertext + tag", rawBytes.size > 20)
        rawBytes[rawBytes.size - 5] = (rawBytes[rawBytes.size - 5].toInt() xor 0x01).toByte()
        encryptedFile.writeBytes(rawBytes)

        // Attempting to decrypt tampered data must throw AEADBadTagException / IOException
        try {
            FileInputStream(encryptedFile).use { fis ->
                val readIv = ByteArray(12)
                fis.read(readIv)
                val cipherDecrypt = Cipher.getInstance("AES/GCM/NoPadding")
                cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, readIv))
                CipherInputStream(fis, cipherDecrypt).use { cis ->
                    cis.readBytes()
                }
            }
            fail("Decryption of tampered ciphertext must throw an authentication exception!")
        } catch (expected: Exception) {
            // Expected cryptographic failure (AEAD authentication tag mismatch)
            assertTrue("Exception must be related to authentication or tag check",
                expected is AEADBadTagException || expected.cause is AEADBadTagException || expected is java.io.IOException)
        }
    }

    @Test
    fun ivUniqueness_twoEncryptionsOfSameDataProduceCompletelyDifferentCiphertext() {
        val dek = generateDEK()
        val data = "Identical photo data frame".toByteArray()

        val secretKey = SecretKeySpec(dek, "AES")

        val iv1 = generateIV()
        val cipher1 = Cipher.getInstance("AES/GCM/NoPadding")
        cipher1.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv1))
        val ciphertext1 = cipher1.doFinal(data)

        val iv2 = generateIV()
        val cipher2 = Cipher.getInstance("AES/GCM/NoPadding")
        cipher2.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv2))
        val ciphertext2 = cipher2.doFinal(data)

        assertFalse("IVs must be distinct", Arrays.equals(iv1, iv2))
        assertFalse("Ciphertexts must be completely distinct even for identical plaintext", Arrays.equals(ciphertext1, ciphertext2))
    }

    @Test
    fun secureZeroWiping_overwritesContent() {
        val file = tempFolder.newFile("temp_secret.raw")
        file.writeBytes("Confidential Camera Buffer Content".toByteArray())
        val length = file.length()
        assertTrue(length > 0)

        // Zero wipe simulation
        FileOutputStream(file).use { fos ->
            val zeros = ByteArray(length.toInt())
            fos.write(zeros)
            fos.flush()
        }

        val wipedBytes = file.readBytes()
        assertTrue("All bytes must be zeroed", wipedBytes.all { it == 0.toByte() })
    }
}
