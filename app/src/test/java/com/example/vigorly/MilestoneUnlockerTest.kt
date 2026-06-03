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
        val elite = MilestoneUnlocker.apply(profile, VigorlyRepository.defaultMilestones())
            .first { it.id == "elite" }
        assertTrue(elite.unlocked)
    }

    @Test
    fun elite_lockedWithoutPro() {
        val profile = VigorlyRepository.defaultProfile().copy(totalWorkouts = 100, isProMember = false)
        val elite = MilestoneUnlocker.apply(profile, VigorlyRepository.defaultMilestones())
            .first { it.id == "elite" }
        assertFalse(elite.unlocked)
    }
}
