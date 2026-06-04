package com.example.vigorly

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.model.AuthResult
import com.example.vigorly.data.repository.VigorlyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

object VigorlyTestAccount {
    const val EMAIL = "e2e@vigorly.test"
    const val PASSWORD = "TestPass1!"
    const val USERNAME = "E2EUser"
    const val BIRTH_DATE = "01/01/1990"
}

typealias VigorlyComposeRule =
    AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

fun VigorlyComposeRule.repository(): VigorlyRepository {
    val app = activity.application as VigorlyApplication
    return app.repository
}

fun VigorlyComposeRule.waitForIdleCompose() {
    waitForIdle()
    mainClock.advanceTimeByFrame()
    waitForIdle()
}

fun VigorlyComposeRule.waitUntilTagExists(
    tag: String,
    timeoutMillis: Long = 20_000L
) {
    waitUntil(timeoutMillis) {
        runCatching {
            onNodeWithTag(tag).assertExists()
            true
        }.getOrDefault(false)
    }
}

fun VigorlyComposeRule.ensureLoggedInWithMainTabs() {
    waitForIdleCompose()
    val repo = repository()
    runBlocking {
        seedTestAccount(repo)
        delay(1_500)
    }

    if (runCatching { onNodeWithTag(VigorlyTestTags.DASHBOARD).assertExists(); true }
        .getOrDefault(false)
    ) {
        return
    }

    if (runCatching { onNodeWithTag(VigorlyTestTags.LOGIN).assertExists(); true }
        .getOrDefault(false)
    ) {
        onNodeWithTag(VigorlyTestTags.LOGIN_EMAIL).performTextInput(VigorlyTestAccount.EMAIL)
        onNodeWithTag(VigorlyTestTags.LOGIN_PASSWORD).performTextInput(VigorlyTestAccount.PASSWORD)
        onNodeWithTag(VigorlyTestTags.LOGIN_SUBMIT).performClick()
        waitForIdleCompose()
        runBlocking { delay(2_000) }
    }

    waitUntilTagExists(VigorlyTestTags.DASHBOARD, timeoutMillis = 30_000L)
}

fun VigorlyComposeRule.navigateToTab(tag: String) {
    waitUntilTagExists(tag)
    onNodeWithTag(tag).performClick()
    waitForIdleCompose()
}

fun VigorlyComposeRule.openSettingsFromMain() {
    navigateToTab(VigorlyTestTags.NAV_DASHBOARD)
    waitUntilTagExists(VigorlyTestTags.TOPBAR_SETTINGS)
    onNodeWithTag(VigorlyTestTags.TOPBAR_SETTINGS).performClick()
    waitUntilTagExists(VigorlyTestTags.SETTINGS)
}

private suspend fun seedTestAccount(repository: VigorlyRepository) {
    when (repository.login(VigorlyTestAccount.EMAIL, VigorlyTestAccount.PASSWORD)) {
        is AuthResult.Success -> return
        is AuthResult.Error -> Unit
    }

    when (
        repository.register(
            email = VigorlyTestAccount.EMAIL,
            password = VigorlyTestAccount.PASSWORD,
            username = VigorlyTestAccount.USERNAME,
            birthDate = VigorlyTestAccount.BIRTH_DATE
        )
    ) {
        is AuthResult.Success -> {
            repository.saveSetupPreferences(
                fitnessGoal = "wellness",
                activityLevel = "moderate",
                weeklySessions = 4,
                notifications = true,
                workoutLocation = "home",
                preferredTime = "flexible"
            )
            delay(1_000)
        }
        is AuthResult.Error -> {
            repository.login(VigorlyTestAccount.EMAIL, VigorlyTestAccount.PASSWORD)
        }
    }
}
