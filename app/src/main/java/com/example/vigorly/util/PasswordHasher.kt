package com.example.vigorly.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * Contraseñas locales revelables (la app las muestra con el ojo en perfil).
 * Formatos soportados:
 * - reveal:  → Base64 del texto (actual)
 * - legacy:  → texto plano (cuentas antiguas)
 * - SHA-256  → solo verificación; al siguiente login se migra a reveal:
 */
object PasswordHasher {
    private const val LEGACY_PREFIX = "legacy:"
    private const val REVEAL_PREFIX = "reveal:"

    fun hash(password: String): Pair<String, String> {
        val encoded = Base64.getEncoder().encodeToString(password.toByteArray(Charsets.UTF_8))
        return "" to "$REVEAL_PREFIX$encoded"
    }

    fun verify(password: String, saltBase64: String, hashBase64: String): Boolean {
        if (hashBase64.startsWith(LEGACY_PREFIX)) {
            return hashBase64.removePrefix(LEGACY_PREFIX) == password
        }
        if (hashBase64.startsWith(REVEAL_PREFIX)) {
            return reveal(hashBase64) == password
        }
        if (saltBase64.isBlank() || hashBase64.isBlank()) return false
        return try {
            val salt = Base64.getDecoder().decode(saltBase64)
            val expected = Base64.getDecoder().decode(hashBase64)
            val actual = digest(password, salt)
            MessageDigest.isEqual(expected, actual)
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    fun legacyHash(plainPassword: String): Pair<String, String> {
        return "" to "$LEGACY_PREFIX$plainPassword"
    }

    fun isLegacy(hashBase64: String): Boolean = hashBase64.startsWith(LEGACY_PREFIX)

    fun isRevealable(hashBase64: String): Boolean =
        hashBase64.startsWith(LEGACY_PREFIX) || hashBase64.startsWith(REVEAL_PREFIX)

    /** Devuelve la contraseña en claro si el formato lo permite. */
    fun reveal(hashBase64: String): String? = when {
        hashBase64.startsWith(LEGACY_PREFIX) -> hashBase64.removePrefix(LEGACY_PREFIX)
        hashBase64.startsWith(REVEAL_PREFIX) -> {
            try {
                String(
                    Base64.getDecoder().decode(hashBase64.removePrefix(REVEAL_PREFIX)),
                    Charsets.UTF_8
                )
            } catch (_: IllegalArgumentException) {
                null
            }
        }
        else -> null
    }

    @Deprecated("Usar reveal()", ReplaceWith("reveal(hashBase64)"))
    fun revealLegacy(hashBase64: String): String? = reveal(hashBase64)

    /** True si aún es hash irreversible (SHA) y conviene migrar en el próximo login. */
    fun needsRevealUpgrade(hashBase64: String): Boolean =
        hashBase64.isNotBlank() &&
            !hashBase64.startsWith(LEGACY_PREFIX) &&
            !hashBase64.startsWith(REVEAL_PREFIX)

    private fun digest(password: String, salt: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        md.update(password.toByteArray(Charsets.UTF_8))
        return md.digest()
    }
}
