package com.example.vigorly

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.model.AuthResult
import com.example.vigorly.data.repository.VigorlyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object VigorlyTestAccount {
    const val EMAIL = "e2e@vigorly.test"
    const val PASSWORD = "TestPass1!"
    const val USERNAME = "E2EUser"
    const val BIRTH_DATE = "01/01/1990"
}

typealias VigorlyComposeRule =
    AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

private val navTagToScreenTag = mapOf(
    VigorlyTestTags.NAV_DASHBOARD to VigorlyTestTags.DASHBOARD,
    VigorlyTestTags.NAV_WORKOUTS to VigorlyTestTags.WORKOUTS,
    VigorlyTestTags.NAV_HISTORY to VigorlyTestTags.HISTORY,
    VigorlyTestTags.NAV_PROFILE to VigorlyTestTags.PROFILE
)

private val postSplashTags = listOf(
    VigorlyTestTags.DASHBOARD,
    VigorlyTestTags.LOGIN,
    VigorlyTestTags.REGISTER,
    VigorlyTestTags.SETUP
)

fun VigorlyComposeRule.repository(): VigorlyRepository {
    val app = activity.application as VigorlyApplication
    return app.repository
}

/** No usar waitForIdle(): animaciones infinitas bloquean el idling de Compose. */
fun VigorlyComposeRule.waitForIdleCompose() = Unit

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

fun VigorlyComposeRule.waitUntilAnyTagExists(
    tags: List<String>,
    timeoutMillis: Long = 25_000L
): String? {
    var found: String? = null
    waitUntil(timeoutMillis) {
        tags.firstOrNull { tag ->
            runCatching {
                onNodeWithTag(tag).assertExists()
                true
            }.getOrDefault(false)
        }?.also { found = it } != null
    }
    return found
}

fun VigorlyComposeRule.ensureLoggedInWithMainTabs() {
    val repo = repository()
    runBlocking(Dispatchers.Default) {
        seedTestAccount(repo)
    }

    if (runCatching { onNodeWithTag(VigorlyTestTags.DASHBOARD).assertExists(); true }
        .getOrDefault(false)
    ) {
        return
    }

    waitUntilAnyTagExists(postSplashTags, timeoutMillis = 30_000L)

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
        waitUntilTagExists(VigorlyTestTags.DASHBOARD, timeoutMillis = 30_000L)
        return
    }

    waitUntilTagExists(VigorlyTestTags.DASHBOARD, timeoutMillis = 30_000L)
}

fun VigorlyComposeRule.navigateToTab(navTag: String) {
    val screenTag = navTagToScreenTag[navTag] ?: navTag
    waitUntilTagExists(navTag)
    onNodeWithTag(navTag).performClick()
    waitUntilTagExists(screenTag)
}

fun VigorlyComposeRule.openSettingsFromMain() {
    navigateToTab(VigorlyTestTags.NAV_DASHBOARD)
    waitUntilTagExists(VigorlyTestTags.TOPBAR_SETTINGS)
    onNodeWithTag(VigorlyTestTags.TOPBAR_SETTINGS).performClick()
    waitUntilTagExists(VigorlyTestTags.SETTINGS)
}

fun VigorlyComposeRule.pressTopBarBack() {
    waitUntilTagExists(VigorlyTestTags.TOPBAR_BACK)
    onNodeWithTag(VigorlyTestTags.TOPBAR_BACK).performClick()
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
            repository.saveSetupPreferencesAndAwait(
                fitnessGoal = "wellness",
                activityLevel = "moderate",
                weeklySessions = 4,
                notifications = true,
                workoutLocation = "home",
                preferredTime = "flexible"
            )
        }
        is AuthResult.Error -> {
            repository.login(VigorlyTestAccount.EMAIL, VigorlyTestAccount.PASSWORD)
        }
    }
}
