package com.privatevault.app.core.security

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ephemeral ECDH (Elliptic Curve Diffie-Hellman) on NIST P-256 (secp256r1).
 *
 * Used for Phase 6 Private Sharing:
 * Generates forward-secret ephemeral session keys between Sender and Recipient.
 * Both derive an identical 256-bit symmetric session key without transmitting it.
 */
@Singleton
class ECDHKeyManager @Inject constructor() {

    companion object {
        const val EC_CURVE = "secp256r1"
        const val ALGORITHM = "EC"
    }

    /**
     * Generates a new ephemeral EC keypair for a single sharing session.
     */
    fun generateEphemeralKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM)
        keyPairGenerator.initialize(ECGenParameterSpec(EC_CURVE))
        return keyPairGenerator.generateKeyPair()
    }

    /**
     * Decodes an X.509 encoded public key received from peer.
     */
    fun decodePeerPublicKey(encodedBytes: ByteArray): PublicKey {
        val keyFactory = KeyFactory.getInstance(ALGORITHM)
        return keyFactory.generatePublic(X509EncodedKeySpec(encodedBytes))
    }

    /**
     * Computes the 256-bit shared symmetric session secret from own private key and peer's public key.
     */
    fun deriveSharedSessionKey(ownKeyPair: KeyPair, peerPublicKey: PublicKey): ByteArray {
        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(ownKeyPair.private)
        keyAgreement.doPhase(peerPublicKey, true)
        val rawSharedSecret = keyAgreement.generateSecret()

        // Derive 256-bit AES key via SHA-256 KDF
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(rawSharedSecret)
    }
}
