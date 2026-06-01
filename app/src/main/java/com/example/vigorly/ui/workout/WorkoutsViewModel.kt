package com.example.vigorly.ui.workout

import androidx.lifecycle.ViewModel
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.util.WorkoutAssistantEngine
import com.example.vigorly.util.WorkoutSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WorkoutsViewModel : ViewModel() {
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

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedType(type: WorkoutType?) {
        _selectedType.value = type
        if (type == null && !_favoritesOnly.value) {
            clearAssistantConstraints()
        }
    }

    fun cycleSort() {
        _sort.value = when (_sort.value) {
            WorkoutSort.DURATION_ASC -> WorkoutSort.DURATION_DESC
            WorkoutSort.DURATION_DESC -> WorkoutSort.NAME_ASC
            WorkoutSort.NAME_ASC -> WorkoutSort.DURATION_ASC
        }
    }

    fun toggleFavoritesOnly() {
        _favoritesOnly.value = !_favoritesOnly.value
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
}
