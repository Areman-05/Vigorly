package com.example.vigorly

import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.util.DurationBucket
import com.example.vigorly.util.WorkoutBrowseFilters
import com.example.vigorly.util.WorkoutFilter
import com.example.vigorly.util.WorkoutSort
import com.example.vigorly.util.WorkoutZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutFilterTest {

    private val all get() = WorkoutCatalog.allWorkouts().values.toList()

    @Test
    fun filter_byQuery_matchesName() {
        val result = WorkoutFilter.filter(all, "titan", null, WorkoutSort.NAME_ASC)
        assertEquals(1, result.size)
        assertEquals("Protocolo Titán", result.first().name)
    }

    @Test
    fun filter_byQuery_isAccentInsensitive() {
        val result = WorkoutFilter.filter(all, "titan", null, WorkoutSort.NAME_ASC)
        assertTrue(result.any { it.id == "titan_protocol" })
        val withAccent = WorkoutFilter.filter(all, "titán", null, WorkoutSort.NAME_ASC)
        assertEquals(result.map { it.id }, withAccent.map { it.id })
    }

    @Test
    fun filter_byType_returnsOnlyHiit() {
        val result = WorkoutFilter.filter(all, "", WorkoutType.HIIT, WorkoutSort.DURATION_ASC)
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.type == WorkoutType.HIIT })
    }

    @Test
    fun filter_sort_durationAscAndDesc() {
        val asc = WorkoutFilter.filter(all, "", null, WorkoutSort.DURATION_ASC)
        val desc = WorkoutFilter.filter(all, "", null, WorkoutSort.DURATION_DESC)
        assertEquals(asc.map { it.durationMinutes }, desc.map { it.durationMinutes }.reversed())
        assertTrue(asc.zipWithNext().all { (a, b) -> a.durationMinutes <= b.durationMinutes })
    }

    @Test
    fun filter_sort_nameAsc() {
        val result = WorkoutFilter.filter(all, "", null, WorkoutSort.NAME_ASC)
        assertEquals(result.map { it.name }.sorted(), result.map { it.name })
    }

    @Test
    fun filterFavorites_keepsOnlyFavoriteIds() {
        val favorites = setOf("titan_protocol", "hiit_sprint")
        val result = WorkoutFilter.filterFavorites(all, favorites)
        assertEquals(2, result.size)
        assertTrue(result.all { it.id in favorites })
    }

    @Test
    fun filterFavorites_emptyIds_returnsEmpty() {
        assertTrue(WorkoutFilter.filterFavorites(all, emptySet()).isEmpty())
    }

    @Test
    fun browseFilters_typeAndDuration() {
        val filters = WorkoutBrowseFilters(
            types = setOf(WorkoutType.HIIT),
            durations = setOf(DurationBucket.SHORT)
        )
        val matched = all.filter { filters.matches(it) }
        assertTrue(matched.isNotEmpty())
        assertTrue(matched.all { it.type == WorkoutType.HIIT && it.durationMinutes <= 20 })
    }

    @Test
    fun browseFilters_intensityKey_normalizes() {
        assertEquals("high", WorkoutBrowseFilters.intensityKey("Alta"))
        assertEquals("moderate", WorkoutBrowseFilters.intensityKey("media"))
        assertEquals("low", WorkoutBrowseFilters.intensityKey("baja"))
    }

    @Test
    fun browseFilters_zoneLowerBody() {
        val filters = WorkoutBrowseFilters(zones = setOf(WorkoutZone.LOWER_BODY, WorkoutZone.LEGS))
        val matched = all.filter { filters.matches(it) }
        assertTrue(matched.isNotEmpty())
        assertFalse(filters.isEmpty)
        assertTrue(filters.selectedCount >= 2)
    }

    @Test
    fun browseFilters_empty_matchesEverything() {
        val filters = WorkoutBrowseFilters()
        assertTrue(filters.isEmpty)
        assertTrue(all.all { filters.matches(it) })
    }

    @Test
    fun browseFilters_intensityFiltersCatalog() {
        val filters = WorkoutBrowseFilters(intensities = setOf("high"))
        val matched = all.filter { filters.matches(it) }
        assertTrue(matched.isNotEmpty())
        assertTrue(
            matched.all {
                WorkoutBrowseFilters.intensityKey(it.intensity) == "high"
            }
        )
    }
}
