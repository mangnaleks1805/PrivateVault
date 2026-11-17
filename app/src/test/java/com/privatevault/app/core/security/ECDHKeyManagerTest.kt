package com.privatevault.app.core.security

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.util.Arrays

class ECDHKeyManagerTest {

    private lateinit var ecdhKeyManager: ECDHKeyManager

    @Before
    fun setUp() {
        ecdhKeyManager = ECDHKeyManager()
    }

    @Test
    fun generateEphemeralKeyPair_producesValidP256Keys() {
        val keyPair = ecdhKeyManager.generateEphemeralKeyPair()

        assertNotNull("Public key should not be null", keyPair.public)
        assertNotNull("Private key should not be null", keyPair.private)
        assertEquals("Algorithm must be EC", "EC", keyPair.public.algorithm)
    }

    @Test
    fun decodePeerPublicKey_roundtripPreservesKey() {
        val originalKeyPair = ecdhKeyManager.generateEphemeralKeyPair()
        val encodedBytes = originalKeyPair.public.encoded

        val decodedPublicKey = ecdhKeyManager.decodePeerPublicKey(encodedBytes)

        assertEquals("Decoded public key must match original algorithm", originalKeyPair.public.algorithm, decodedPublicKey.algorithm)
        assertArrayEquals("Decoded public key encoded bytes must match", encodedBytes, decodedPublicKey.encoded)
    }

    @Test
    fun deriveSharedSessionKey_symmetricAgreementBetweenBothPeers() {
        // Peer A (Sender)
        val keyPairA = ecdhKeyManager.generateEphemeralKeyPair()

        // Peer B (Recipient)
        val keyPairB = ecdhKeyManager.generateEphemeralKeyPair()

        // Mutual derivation over public keys
        val sharedSecretA = ecdhKeyManager.deriveSharedSessionKey(keyPairA, keyPairB.public)
        val sharedSecretB = ecdhKeyManager.deriveSharedSessionKey(keyPairB, keyPairA.public)

        // Key Agreement guarantee: Both secrets must be identical 256-bit (32 byte) keys
        assertEquals("Derived key must be 256 bits (32 bytes)", 32, sharedSecretA.size)
        assertEquals("Derived key must be 256 bits (32 bytes)", 32, sharedSecretB.size)
        assertArrayEquals("Both peers must compute the exact same session secret", sharedSecretA, sharedSecretB)
    }

    @Test
    fun differentKeyPairs_produceDifferentSecrets() {
        val keyPairA = ecdhKeyManager.generateEphemeralKeyPair()
        val keyPairB = ecdhKeyManager.generateEphemeralKeyPair()
        val keyPairC = ecdhKeyManager.generateEphemeralKeyPair()

        val secretAB = ecdhKeyManager.deriveSharedSessionKey(keyPairA, keyPairB.public)
        val secretAC = ecdhKeyManager.deriveSharedSessionKey(keyPairA, keyPairC.public)

        assertFalse("Different peers must yield different forward-secret keys", Arrays.equals(secretAB, secretAC))
    }
}
