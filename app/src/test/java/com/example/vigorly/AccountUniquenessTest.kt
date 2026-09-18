package com.example.vigorly

import com.example.vigorly.data.model.AccountUniqueness
import com.example.vigorly.data.model.UserAccount
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountUniquenessTest {
    private val accounts = listOf(
        UserAccount(
            id = "1",
            email = "ana@vigorly.app",
            username = "Ana",
            birthDate = "01/01/1990"
        ),
        UserAccount(
            id = "2",
            email = "pablo@vigorly.app",
            username = "Pablo",
            birthDate = "02/02/1992"
        )
    )

    @Test
    fun email_isTaken_ignoresCaseAndSpaces() {
        assertTrue(AccountUniqueness.isEmailTaken(accounts, "  ANA@vigorly.app "))
        assertFalse(AccountUniqueness.isEmailTaken(accounts, "nueva@vigorly.app"))
        assertFalse(AccountUniqueness.isEmailTaken(accounts, "ana@vigorly.app", exceptId = "1"))
    }

    @Test
    fun username_isTaken_ignoresCaseAndSpaces() {
        assertTrue(AccountUniqueness.isUsernameTaken(accounts, " pablo "))
        assertFalse(AccountUniqueness.isUsernameTaken(accounts, "Lucia"))
        assertFalse(AccountUniqueness.isUsernameTaken(accounts, "Pablo", exceptId = "2"))
    }
}
