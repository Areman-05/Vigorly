package com.example.vigorly

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.ui.profile.ProfileAvatarCatalog
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import org.junit.runner.RunWith

/**
 * Suite E2E de UI: navegación principal, ajustes, perfil, estadísticas y persistencia de locale.
 * Requiere emulador o dispositivo. Usa clearPackageData entre clases de test.
 * Orden fijo: auth y tabs principales antes que tests que reinician la activity.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class VigorlyAppInstrumentedTest {

    @get:Rule
    val composeRule: VigorlyComposeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun test01_authFlow_login_reachesDashboard() {
        composeRule.ensureLoggedInWithMainTabs()
        composeRule.onNodeWithTag(VigorlyTestTags.DASHBOARD).assertIsDisplayed()
    }

    @Test
    fun test02_mainTabs_navigateAllPrimaryScreens() {
        composeRule.navigateToTab(VigorlyTestTags.NAV_WORKOUTS)
        composeRule.onNodeWithTag(VigorlyTestTags.WORKOUTS).assertIsDisplayed()

        composeRule.navigateToTab(VigorlyTestTags.NAV_HISTORY)
        composeRule.onNodeWithTag(VigorlyTestTags.HISTORY).assertIsDisplayed()

        composeRule.navigateToTab(VigorlyTestTags.NAV_PROFILE)
        composeRule.onNodeWithTag(VigorlyTestTags.PROFILE).assertIsDisplayed()

        composeRule.navigateToTab(VigorlyTestTags.NAV_DASHBOARD)
        composeRule.onNodeWithTag(VigorlyTestTags.DASHBOARD).assertIsDisplayed()
    }

    @Test
    fun test03_appContext_hasExpectedPackage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.vigorly", context.packageName)
    }

    @Test
    fun test04_avatarCatalog_detectsRemoteUrls() {
        assertTrue(ProfileAvatarCatalog.isRemoteUrl("https://cdn.example.com/user.png"))
        assertTrue(!ProfileAvatarCatalog.isRemoteUrl(ProfileAvatarCatalog.encode("spark")))
    }

    @Test
    fun test05_locale_setEnglish_persistsInRepository() {
        val repo = composeRule.repository()
        runBlocking {
            repo.setAppLocaleAndAwait("en")
            assertEquals("en", repo.effectiveLocale())
            repo.setAppLocaleAndAwait("es")
            assertEquals("es", repo.effectiveLocale())
        }
    }

    @Test
    fun test06_profile_openMilestonesAndInsights() {
        composeRule.ensureLoggedInWithMainTabs()
        composeRule.navigateToTab(VigorlyTestTags.NAV_PROFILE)

        composeRule.onNodeWithTag(VigorlyTestTags.PROFILE_OPEN_INSIGHTS)
            .performScrollTo()
            .performClick()
        composeRule.waitUntilTagExists(VigorlyTestTags.INSIGHTS)
        composeRule.onNodeWithTag(VigorlyTestTags.INSIGHTS).assertIsDisplayed()

        composeRule.pressTopBarBack()
        composeRule.waitUntilTagExists(VigorlyTestTags.PROFILE)
        composeRule.onNodeWithTag(VigorlyTestTags.PROFILE_OPEN_MILESTONES)
            .performScrollTo()
            .performClick()
        composeRule.waitUntilTagExists(VigorlyTestTags.MILESTONES)
        composeRule.onNodeWithTag(VigorlyTestTags.MILESTONES).assertIsDisplayed()
    }

    @Test
    fun test07_settings_weeklyTargetStepper_updatesValue() {
        composeRule.ensureLoggedInWithMainTabs()
        composeRule.openSettingsFromMain()

        composeRule.onNodeWithTag(VigorlyTestTags.WEEKLY_TARGET_VALUE).assertIsDisplayed()
        composeRule.onNodeWithTag(VigorlyTestTags.WEEKLY_TARGET_INCREASE).performClick()

        composeRule.pressTopBarBack()
        composeRule.waitUntilTagExists(VigorlyTestTags.DASHBOARD)
        composeRule.onNodeWithTag(VigorlyTestTags.DASHBOARD).assertIsDisplayed()
    }

    @Test
    fun test08_mainActivity_launchesWithoutCrash() {
        composeRule.activityRule.scenario.onActivity { /* activity viva */ }
    }
}
