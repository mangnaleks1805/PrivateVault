package com.privatevault.app.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

/**
 * Manages Android Keystore operations.
 *
 * The master key NEVER leaves the hardware-backed Keystore (TEE/SE).
 * It is used only to wrap/unwrap per-file Data Encryption Keys (DEKs).
 *
 * Phase 1: Key generation + basic wrap/unwrap stubs.
 * Phase 3: Full AES-256-GCM streaming encryption integrated.
 */
class KeystoreManager @Inject constructor() {

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        const val MASTER_KEY_ALIAS = "privatevault_master_key_v1"
        private const val GCM_TAG_LENGTH = 128
        const val IV_SIZE_BYTES = 12 // 96-bit IV for GCM
        const val KEY_SIZE_BITS = 256
    }

    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
        load(null)
    }

    /**
     * Generates the master key in Android Keystore if it doesn't exist.
     * User authentication is NOT required at Phase 1 (biometric gate added in Phase 4).
     * Phase 4 will call regenerateMasterKeyWithAuth() to enforce biometric binding.
     */
    fun initializeMasterKey() {
        if (keyStore.containsAlias(MASTER_KEY_ALIAS)) return

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val spec = KeyGenParameterSpec.Builder(
            MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(KEY_SIZE_BITS)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            // Phase 4: setUserAuthenticationRequired(true) will be enabled
            .setUserAuthenticationRequired(false)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        keyGenerator.generateKey()
    }

    fun masterKeyExists(): Boolean = keyStore.containsAlias(MASTER_KEY_ALIAS)

    fun getMasterKey(): SecretKey {
        return (keyStore.getEntry(MASTER_KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    /**
     * Encrypts a DEK with the master key using AES-256-GCM.
     * Returns [iv + ciphertext + tag] as a single ByteArray.
     */
    fun wrapKey(dek: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getMasterKey())
        val iv = cipher.iv // Keystore generates a secure random IV
        val encrypted = cipher.doFinal(dek)
        // Prepend IV so we can unwrap later: [12 bytes IV][ciphertext+tag]
        return iv + encrypted
    }

    /**
     * Decrypts a wrapped DEK. Input must be [iv + ciphertext + tag].
     */
    fun unwrapKey(wrappedDek: ByteArray): ByteArray {
        val iv = wrappedDek.sliceArray(0 until IV_SIZE_BYTES)
        val ciphertext = wrappedDek.sliceArray(IV_SIZE_BYTES until wrappedDek.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getMasterKey(), spec)
        return cipher.doFinal(ciphertext)
    }
}
