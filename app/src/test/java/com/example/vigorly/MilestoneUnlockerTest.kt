package com.example.vigorly

import com.example.vigorly.data.MilestoneUnlocker
import com.example.vigorly.data.repository.VigorlyRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MilestoneUnlockerTest {

    @Test
    fun elite_requiresProAndMaxLevel() {
        val profile = VigorlyRepository.defaultProfile().copy(totalWorkouts = 45, isProMember = true)
        val (milestones, _) = MilestoneUnlocker.apply(profile, VigorlyRepository.defaultMilestones())
        assertTrue(milestones.first { it.id == "elite" }.unlocked)
    }

    @Test
    fun elite_lockedWithoutPro() {
        val profile = VigorlyRepository.defaultProfile().copy(totalWorkouts = 100, isProMember = false)
        val (milestones, _) = MilestoneUnlocker.apply(profile, VigorlyRepository.defaultMilestones())
        assertFalse(milestones.first { it.id == "elite" }.unlocked)
    }
}
