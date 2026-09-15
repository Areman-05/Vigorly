package com.example.vigorly.data.activity

enum class ActivityMetric {
    STEPS,
    MOVE,
    EXERCISE,
    STAND;

    companion object {
        fun fromRoute(raw: String?): ActivityMetric =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: STEPS
    }
}
