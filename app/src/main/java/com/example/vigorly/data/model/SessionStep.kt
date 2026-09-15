package com.example.vigorly.data.model

/**
 * Paso unificado de sesión: calentamiento o ejercicio del plan.
 */
data class SessionStep(
    val id: String,
    val name: String,
    val detailLabel: String,
    val isWarmup: Boolean,
    val durationSeconds: Int
)
