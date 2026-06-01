package com.example.vigorly.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.util.WorkoutAssistantEngine
import com.example.vigorly.util.WorkoutFilter
import com.example.vigorly.util.WorkoutSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class WorkoutsViewModel(
    repository: VigorlyRepository
) : ViewModel() {

    private val allWorkouts: List<WorkoutDetail> = repository.listWorkouts()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow<WorkoutType?>(null)
    val selectedType: StateFlow<WorkoutType?> = _selectedType.asStateFlow()

    private val _sort = MutableStateFlow(WorkoutSort.DURATION_ASC)
    val sort: StateFlow<WorkoutSort> = _sort.asStateFlow()

    private val _favoritesOnly = MutableStateFlow(false)
    val favoritesOnly: StateFlow<Boolean> = _favoritesOnly.asStateFlow()

    private val _assistantFilters = MutableStateFlow(WorkoutAssistantEngine.Result())
    val assistantFilters: StateFlow<WorkoutAssistantEngine.Result> = _assistantFilters.asStateFlow()

    val filteredWorkouts: StateFlow<List<WorkoutDetail>> = combine(
        combine(
            _searchQuery,
            _selectedType,
            _sort,
            _favoritesOnly,
            repository.favorites
        ) { query, type, sort, favoritesOnly, favoriteIds ->
            FilterState(query, type, sort, favoritesOnly, favoriteIds)
        },
        _assistantFilters
    ) { filter, assistant ->
        buildWorkoutList(
            all = allWorkouts,
            searchQuery = filter.query,
            selectedFilter = filter.type,
            sort = filter.sort,
            favoritesOnly = filter.favoritesOnly,
            favoriteIds = filter.favoriteIds,
            assistant = assistant
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = allWorkouts
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectAll() {
        _selectedType.value = null
        _favoritesOnly.value = false
    }

    fun selectType(type: WorkoutType) {
        _selectedType.value = type
        _favoritesOnly.value = false
    }

    fun onFavoritesChipClick() {
        if (_favoritesOnly.value) {
            _favoritesOnly.value = false
        } else {
            _favoritesOnly.value = true
            _selectedType.value = null
        }
    }

    fun cycleSort() {
        _sort.value = when (_sort.value) {
            WorkoutSort.DURATION_ASC -> WorkoutSort.DURATION_DESC
            WorkoutSort.DURATION_DESC -> WorkoutSort.NAME_ASC
            WorkoutSort.NAME_ASC -> WorkoutSort.DURATION_ASC
        }
    }

    fun applyAssistant(result: WorkoutAssistantEngine.Result) {
        _searchQuery.value = result.searchQuery
        _selectedType.value = result.type
        result.sort?.let { _sort.value = it }
        _favoritesOnly.value = result.favoritesOnly
        _assistantFilters.value = result.copy(searchQuery = "")
    }

    fun clearAssistantConstraints() {
        _assistantFilters.value = WorkoutAssistantEngine.Result()
    }

    private data class FilterState(
        val query: String,
        val type: WorkoutType?,
        val sort: WorkoutSort,
        val favoritesOnly: Boolean,
        val favoriteIds: Set<String>
    )

    private fun buildWorkoutList(
        all: List<WorkoutDetail>,
        searchQuery: String,
        selectedFilter: WorkoutType?,
        sort: WorkoutSort,
        favoritesOnly: Boolean,
        favoriteIds: Set<String>,
        assistant: WorkoutAssistantEngine.Result
    ): List<WorkoutDetail> {
        var workouts = WorkoutFilter.filter(all, searchQuery, selectedFilter, sort)
        if (favoritesOnly) {
            workouts = WorkoutFilter.filterFavorites(workouts, favoriteIds)
        }
        return workouts.filter {
            WorkoutAssistantEngine.matchesConstraints(
                durationMinutes = it.durationMinutes,
                intensity = it.intensity,
                filters = assistant
            )
        }
    }
}
