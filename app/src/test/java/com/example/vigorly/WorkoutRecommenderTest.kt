package com.example.vigorly

import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.util.WorkoutRecommender
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutRecommenderTest {
    private val workouts = WorkoutCatalog.allWorkouts()

    @Test
    fun recommend_prefersFavorite() {
        val favoriteId = workouts.first().id
        val result = WorkoutRecommender.recommend(
            workouts = workouts,
            history = emptyList(),
            favorites = setOf(favoriteId)
        )
        assertEquals(favoriteId, result?.id)
    }

    @Test
    fun recommend_avoidsRecentTitles() {
        val first = workouts.first()
        val history = listOf(
            WorkoutHistoryItem("h1", first.name, "Today", 30, 200, "fitness_center")
        )
        val result = WorkoutRecommender.recommend(workouts, history, emptySet())
        assertNotNull(result)
        if (workouts.size > 1) {
            assertTrue(result?.name != first.name || workouts.all { it.name == first.name })
        }
    }
}
