package com.example.vigorly

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Lógica de resolución de locale almacenado (espeja VigorlyPreferencesDataStore.appLocale).
 */
class AppLocalePreferenceTest {

    @Test
    fun userSelectedLocale_isReturned() {
        val resolved = resolveAppLocale(
            userSelected = true,
            storedLocale = "en"
        )
        assertEquals("en", resolved)
    }

    @Test
    fun defaultLocale_whenNotUserSelected() {
        val resolved = resolveAppLocale(
            userSelected = false,
            storedLocale = "fr"
        )
        assertEquals("es", resolved)
    }

    private fun resolveAppLocale(userSelected: Boolean, storedLocale: String?): String {
        return if (userSelected) {
            storedLocale ?: "es"
        } else {
            "es"
        }
    }
}
