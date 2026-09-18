package com.example.vigorly

import com.example.vigorly.auth.GoogleUserInfo
import com.example.vigorly.data.model.AuthError
import com.example.vigorly.data.model.AuthResult
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.navigation.AppDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class VigorlyRepositoryFlowsTest {

    private lateinit var repository: VigorlyRepository

    @Before
    fun setUp() {
        repository = VigorlyRepository(RuntimeEnvironment.getApplication())
    }

    @Test
    fun resolveStartDestination_followsLoginState() = runBlocking {
        repository.logout()
        withTimeout(4_000) {
            while (repository.resolveStartDestination() != AppDestination.Login) {
                delay(50)
            }
        }
        val suffix = UUID.randomUUID().toString().take(8)
        repository.register("dest$suffix@vigorly.test", "Segura1!", "Dest$suffix", "15/03/1995")
        withTimeout(4_000) {
            while (repository.resolveStartDestination() != AppDestination.Main) {
                delay(50)
            }
        }
        assertEquals(AppDestination.Main, repository.resolveStartDestination())
    }

    @Test
    fun register_login_andRejectDuplicates() = runBlocking {
        val suffix = UUID.randomUUID().toString().take(8)
        val email = "user$suffix@vigorly.test"
        val username = "User$suffix"
        val registered = repository.register(email, "Segura1!", username, "15/03/1995")
        assertTrue(registered is AuthResult.Success)
        assertTrue((registered as AuthResult.Success).needsSetup)
        assertTrue(repository.isLoggedIn.value)

        val duplicateEmail = repository.register(email, "Segura1!", "Otro$suffix", "15/03/1995")
        assertEquals(AuthError.EMAIL_ALREADY_EXISTS, (duplicateEmail as AuthResult.Error).messageKey)

        val duplicateUser = repository.register(
            "otra$suffix@vigorly.test",
            "Segura1!",
            username,
            "15/03/1995"
        )
        assertEquals(AuthError.USERNAME_ALREADY_EXISTS, (duplicateUser as AuthResult.Error).messageKey)

        val badLogin = repository.login(email, "Wrong1!")
        assertEquals(AuthError.INVALID_CREDENTIALS, (badLogin as AuthResult.Error).messageKey)

        val okLogin = repository.login(email, "Segura1!")
        assertTrue(okLogin is AuthResult.Success)
        assertFalse((okLogin as AuthResult.Success).needsSetup)
    }

    @Test
    fun setupPreferences_markOnboardingComplete() = runBlocking {
        val suffix = UUID.randomUUID().toString().take(8)
        repository.register("setup$suffix@vigorly.test", "Segura1!", "Set$suffix", "15/03/1995")
        repository.saveSetupPreferencesAndAwait(
            fitnessGoal = "hiit,strength",
            activityLevel = "active",
            weeklySessions = 5,
            notifications = true,
            workoutLocation = "gym",
            preferredTime = "morning"
        )
        withTimeout(3_000) {
            repository.fitnessGoal.first { it == "hiit,strength" }
            repository.activityLevel.first { it == "active" }
            repository.workoutLocation.first { it == "gym" }
            repository.weeklyGoal.first { it.targetSessions == 5 }
        }
        assertTrue(repository.onboardingCompleted.value)
        assertEquals(0, repository.weeklyGoal.value.completedSessions)
    }

    @Test
    fun favoritesAndPlaylists_updateInMemory() {
        val workoutId = repository.listWorkouts().first().id
        assertFalse(repository.isFavorite(workoutId))
        repository.toggleFavorite(workoutId)
        assertTrue(repository.isFavorite(workoutId))
        repository.toggleFavorite(workoutId)
        assertFalse(repository.isFavorite(workoutId))

        val before = repository.playlists.value.size
        repository.createPlaylist("Piernas", listOf(workoutId))
        repository.createPlaylist("Piernas", listOf(workoutId))
        val created = repository.playlists.value
        assertEquals(before + 2, created.size)
        val names = created.takeLast(2).map { it.name }
        assertTrue(names.contains("Piernas"))
        assertTrue(names.any { it.startsWith("Piernas") && it != "Piernas" })
    }

    @Test
    fun session_startPauseCancel() {
        val workout = repository.listWorkouts().first()
        val session = repository.startWorkoutSession(workout.id)
        assertNotNull(session)
        assertEquals(workout.id, session!!.workoutId)
        assertFalse(session.isPaused)
        assertTrue(session.totalExercises > 0)

        repository.toggleSessionPause()
        assertTrue(repository.activeSession.value?.isPaused == true)
        assertFalse(repository.tickSession())

        repository.cancelWorkoutSession()
        assertNull(repository.activeSession.value)
    }

    @Test
    fun recommendations_respectCatalog() {
        val recs = repository.getRecommendedWorkouts(5)
        assertTrue(recs.isNotEmpty())
        assertTrue(recs.size <= 5)
        assertEquals(recs.size, recs.distinctBy { it.id }.size)
        recs.forEach { assertNotNull(repository.getWorkout(it.id)) }
    }

    @Test
    fun googleLogin_makesUniqueUsernames() = runBlocking {
        val suffix = UUID.randomUUID().toString().take(6)
        val first = repository.loginWithGoogle(
            GoogleUserInfo(
                id = "g-$suffix-1",
                email = "g1$suffix@vigorly.test",
                displayName = "Alex",
                profilePictureUri = null
            )
        )
        val second = repository.loginWithGoogle(
            GoogleUserInfo(
                id = "g-$suffix-2",
                email = "g2$suffix@vigorly.test",
                displayName = "Alex",
                profilePictureUri = null
            )
        )
        assertTrue(first is AuthResult.Success)
        assertTrue(second is AuthResult.Success)
        val names = repository.accounts.value
            .filter { it.email.contains(suffix) }
            .map { it.username }
        assertTrue(names.contains("Alex"))
        assertTrue(names.any { it == "Alex2" || it.startsWith("Alex") && it != "Alex" })
    }

    @Test
    fun historyItem_missingReturnsNull() {
        assertNull(repository.getHistoryItem("missing-id"))
    }
}
