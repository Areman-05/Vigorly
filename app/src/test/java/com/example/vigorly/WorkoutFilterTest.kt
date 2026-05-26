package com.example.vigorly

import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.util.WorkoutFilter
import com.example.vigorly.util.WorkoutSort
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutFilterTest {

  @Test
  fun filter_byQuery_matchesName() {
    val all = WorkoutCatalog.allWorkouts().values.toList()
    val result = WorkoutFilter.filter(all, "titan", null, WorkoutSort.NAME_ASC)
    assertEquals(1, result.size)
    assertEquals("Titan Protocol", result.first().name)
  }

  @Test
  fun filter_byType_returnsOnlyHiit() {
    val all = WorkoutCatalog.allWorkouts().values.toList()
    val result = WorkoutFilter.filter(all, "", WorkoutType.HIIT, WorkoutSort.DURATION_ASC)
    assertTrue(result.all { it.type == WorkoutType.HIIT })
  }
}
