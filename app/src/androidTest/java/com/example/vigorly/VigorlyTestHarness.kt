package com.example.vigorly

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
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

private val bootstrapTags = listOf(
    VigorlyTestTags.SPLASH,
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

/**
 * Semilla la sesión en el repositorio antes de esperar la UI, para que la splash resuelva a dashboard.
 */
fun VigorlyComposeRule.ensureLoggedInWithMainTabs() {
    if (waitForTagOptional(VigorlyTestTags.DASHBOARD, timeoutMillis = 3_000L)) return

    val repo = repository()
    val loggedIn = runBlocking(Dispatchers.Default) { seedTestAccount(repo) }

    if (hasTag(VigorlyTestTags.DASHBOARD)) return

    activityRule.scenario.recreate()

    if (loggedIn) {
        waitForDashboardOrCompleteSetup(repo, timeoutMillis = 45_000L)
        return
    }

    waitUntilAnyTagExists(bootstrapTags, timeoutMillis = 35_000L)
    if (hasTag(VigorlyTestTags.DASHBOARD)) return

    if (hasTag(VigorlyTestTags.SPLASH)) {
        waitForDashboardOrCompleteSetup(repo, timeoutMillis = 45_000L)
        return
    }

    if (hasTag(VigorlyTestTags.SETUP)) {
        completeSetupAndReachDashboard(repo)
        return
    }

    if (hasTag(VigorlyTestTags.LOGIN)) {
        submitLoginForm()
        waitForDashboardOrCompleteSetup(repo, timeoutMillis = 40_000L)
        return
    }

    waitUntilTagExists(VigorlyTestTags.DASHBOARD, timeoutMillis = 45_000L)
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

private fun VigorlyComposeRule.waitForDashboardOrCompleteSetup(
    repo: VigorlyRepository,
    timeoutMillis: Long
) {
    waitUntilAnyTagExists(
        listOf(VigorlyTestTags.DASHBOARD, VigorlyTestTags.SETUP, VigorlyTestTags.LOGIN),
        timeoutMillis = timeoutMillis
    )
    when {
        hasTag(VigorlyTestTags.DASHBOARD) -> return
        hasTag(VigorlyTestTags.SETUP) -> completeSetupAndReachDashboard(repo)
        hasTag(VigorlyTestTags.LOGIN) -> {
            submitLoginForm()
            waitUntilTagExists(VigorlyTestTags.DASHBOARD, timeoutMillis = 35_000L)
        }
        else -> waitUntilTagExists(VigorlyTestTags.DASHBOARD, timeoutMillis)
    }
}

private fun VigorlyComposeRule.completeSetupAndReachDashboard(repo: VigorlyRepository) {
    runBlocking(Dispatchers.Default) { ensureOnboardingCompleted(repo) }
    activityRule.scenario.recreate()
    waitUntilTagExists(VigorlyTestTags.DASHBOARD, timeoutMillis = 45_000L)
}

private fun VigorlyComposeRule.submitLoginForm() {
    onNodeWithTag(VigorlyTestTags.LOGIN_EMAIL).performTextReplacement(VigorlyTestAccount.EMAIL)
    onNodeWithTag(VigorlyTestTags.LOGIN_PASSWORD).performTextReplacement(VigorlyTestAccount.PASSWORD)
    onNodeWithTag(VigorlyTestTags.LOGIN_SUBMIT).performClick()
}

fun VigorlyComposeRule.waitForTagOptional(tag: String, timeoutMillis: Long): Boolean =
    runCatching {
        waitUntilTagExists(tag, timeoutMillis)
        true
    }.getOrDefault(false)

private fun VigorlyComposeRule.hasTag(tag: String): Boolean =
    runCatching {
        onNodeWithTag(tag).assertExists()
        true
    }.getOrDefault(false)

private suspend fun ensureOnboardingCompleted(repository: VigorlyRepository) {
    if (repository.onboardingCompleted.value) return
    repository.saveSetupPreferencesAndAwait(
        fitnessGoal = "wellness",
        activityLevel = "moderate",
        weeklySessions = 4,
        notifications = true,
        workoutLocation = "home",
        preferredTime = "flexible"
    )
}

private suspend fun seedTestAccount(repository: VigorlyRepository): Boolean {
    repository.preloadAppData()
    val loggedIn = when (repository.login(VigorlyTestAccount.EMAIL, VigorlyTestAccount.PASSWORD)) {
        is AuthResult.Success -> true
        is AuthResult.Error -> false
    } || when (
        repository.register(
            email = VigorlyTestAccount.EMAIL,
            password = VigorlyTestAccount.PASSWORD,
            username = VigorlyTestAccount.USERNAME,
            birthDate = VigorlyTestAccount.BIRTH_DATE
        )
    ) {
        is AuthResult.Success -> true
        is AuthResult.Error ->
            repository.login(VigorlyTestAccount.EMAIL, VigorlyTestAccount.PASSWORD) is AuthResult.Success
    }

    if (loggedIn) ensureOnboardingCompleted(repository)
    return loggedIn
}
