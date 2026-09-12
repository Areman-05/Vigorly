package com.example.vigorly.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.util.WorkoutBrowseFilters
import com.example.vigorly.util.WorkoutFilter
import com.example.vigorly.util.WorkoutSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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

    private val _selectedPlaylistId = MutableStateFlow<String?>(null)
    val selectedPlaylistId: StateFlow<String?> = _selectedPlaylistId.asStateFlow()

    private val _browseFilters = MutableStateFlow(WorkoutBrowseFilters())
    val browseFilters: StateFlow<WorkoutBrowseFilters> = _browseFilters.asStateFlow()

    val favoriteWorkouts: StateFlow<List<WorkoutDetail>> = repository.favorites
        .map { ids -> allWorkouts.filter { it.id in ids } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

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
        _browseFilters
    ) { filter, browse ->
        buildWorkoutList(
            all = allWorkouts,
            searchQuery = filter.query,
            selectedFilter = filter.type,
            sort = filter.sort,
            favoritesOnly = filter.favoritesOnly,
            favoriteIds = filter.favoriteIds,
            browse = browse
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = allWorkouts
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectType(type: WorkoutType) {
        _selectedType.value = type
        _favoritesOnly.value = false
        _selectedPlaylistId.value = null
    }

    fun clearSelectedType() {
        _selectedType.value = null
    }

    fun showAllFavorites() {
        _favoritesOnly.value = true
        _selectedType.value = null
        _selectedPlaylistId.value = null
    }

    fun clearFavoritesMode() {
        _favoritesOnly.value = false
        _selectedPlaylistId.value = null
        _searchQuery.value = ""
    }

    fun openPlaylist(playlistId: String) {
        _selectedPlaylistId.value = playlistId
        _favoritesOnly.value = true
        _selectedType.value = null
    }

    fun clearSelectedPlaylist() {
        _selectedPlaylistId.value = null
    }

    fun applyBrowseFilters(filters: WorkoutBrowseFilters) {
        _browseFilters.value = filters
        when {
            filters.types.size == 1 -> _selectedType.value = filters.types.first()
            filters.types.isEmpty() -> Unit
            else -> _selectedType.value = null
        }
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedType.value = null
        _favoritesOnly.value = false
        _selectedPlaylistId.value = null
        _browseFilters.value = WorkoutBrowseFilters()
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
        browse: WorkoutBrowseFilters
    ): List<WorkoutDetail> {
        var workouts = WorkoutFilter.filter(all, searchQuery, selectedFilter, sort)
        if (favoritesOnly) {
            workouts = WorkoutFilter.filterFavorites(workouts, favoriteIds)
        }
        if (!browse.isEmpty) {
            val rest = browse.copy(types = if (selectedFilter != null) emptySet() else browse.types)
            workouts = workouts.filter { rest.matches(it) }
        }
        return workouts
    }
}
