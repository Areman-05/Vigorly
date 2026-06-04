package com.example.vigorly

import com.example.vigorly.data.model.WeeklyGoal
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regresión: al cambiar solo el objetivo semanal no debe resetear sesiones completadas.
 */
class WeeklyGoalPersistenceTest {

    @Test
    fun changingTarget_preservesCompletedSessions() {
        val current = WeeklyGoal(targetSessions = 5, completedSessions = 4)
        val updated = current.copy(
            targetSessions = 6,
            completedSessions = current.completedSessions.coerceAtMost(6)
        )
        assertEquals(4, updated.completedSessions)
        assertEquals(6, updated.targetSessions)
    }

    @Test
    fun loweringTarget_coercesCompletedDown() {
        val current = WeeklyGoal(targetSessions = 5, completedSessions = 4)
        val updated = current.copy(
            targetSessions = 3,
            completedSessions = current.completedSessions.coerceAtMost(3)
        )
        assertEquals(3, updated.completedSessions)
    }
}
