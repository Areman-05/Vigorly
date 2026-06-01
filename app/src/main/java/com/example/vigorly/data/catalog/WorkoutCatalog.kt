package com.example.vigorly.data.catalog

import com.example.vigorly.data.model.WorkoutDetail

object WorkoutCatalog {

    fun allWorkouts(): Map<String, WorkoutDetail> =
        allCatalogWorkouts().associateBy { it.id }

    fun titanProtocol(): WorkoutDetail = allWorkouts().getValue("titan_protocol")
    fun hiitSprint(): WorkoutDetail = allWorkouts().getValue("hiit_sprint")
    fun recoveryYoga(): WorkoutDetail = allWorkouts().getValue("recovery_yoga")
    fun upperBodyPower(): WorkoutDetail = allWorkouts().getValue("upper_body_power")
    fun morningSwim(): WorkoutDetail = allWorkouts().getValue("morning_swim")
    fun coreBlast(): WorkoutDetail = allWorkouts().getValue("core_blast")
    fun legDay(): WorkoutDetail = allWorkouts().getValue("leg_day")
}
