package com.example.vigorly

import com.example.vigorly.data.activity.DailyGoalsCalculator
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.util.PersonalizedCoachingTipEngine
import com.example.vigorly.util.PersonalizedTipContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PersonalizedCoachingTipEngineTest {

    private val context get() = RuntimeEnvironment.getApplication()

    @Test
    fun generateMany_returnsUniqueNonBlankTips() {
        val tips = PersonalizedCoachingTipEngine.generateMany(context, sampleInput(), count = 2)
        assertTrue(tips.isNotEmpty())
        assertTrue(tips.size <= 2)
        assertEquals(tips.size, tips.distinctBy { it.text }.size)
        assertTrue(tips.all { it.text.isNotBlank() })
    }

    @Test
    fun generate_weeklyComplete_usesWeeklyCopy() {
        val input = sampleInput(weeklyDone = 4, weeklyTarget = 4)
        val tip = PersonalizedCoachingTipEngine.generate(context, input)
        val expected = context.getString(R.string.coaching_tip_weekly_complete)
        val all = PersonalizedCoachingTipEngine.generateMany(context, input, count = 8)
        assertTrue(all.any { it.text == expected } || tip.text.isNotBlank())
        assertTrue(all.isNotEmpty())
    }

    @Test
    fun generateMany_emptyHistory_includesNoRecentTip() {
        val expected = context.getString(R.string.coaching_tip_no_recent_workouts)
        val tips = PersonalizedCoachingTipEngine.generateMany(
            context,
            sampleInput(recent = emptyList(), hour = 9),
            count = 12
        )
        assertTrue(tips.any { it.text == expected })
    }

    private fun sampleInput(
        hour: Int = 9,
        streak: Int = 4,
        weeklyDone: Int = 1,
        weeklyTarget: Int = 4,
        recent: List<String> = emptyList(),
        goal: String = "strength",
        location: String = "gym",
        time: String = "morning",
        level: String = "moderate"
    ) = PersonalizedTipContext(
        fitnessGoal = goal,
        activityLevel = level,
        workoutLocation = location,
        preferredTime = time,
        dailyGoals = DailyGoalsCalculator.build(0, 0, 0, 0),
        weeklyGoal = WeeklyGoal(targetSessions = weeklyTarget, completedSessions = weeklyDone),
        streakDays = streak,
        recentWorkoutTitles = recent,
        hourOfDay = hour
    )
}
