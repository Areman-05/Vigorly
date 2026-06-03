package com.example.vigorly.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.workout.WorkoutDetailSectionEnter
import com.example.vigorly.ui.workout.rememberWorkoutDetailVisible
import com.example.vigorly.util.HistorySummaryCalculator
import com.example.vigorly.util.LevelCalculator

@Composable
fun ProfileScreen(
    repository: VigorlyRepository,
    onViewAllMilestones: () -> Unit = {},
    onOpenInsights: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by repository.profile.collectAsState()
    val stats by repository.athleticStats.collectAsState()
    val milestones by repository.milestones.collectAsState()
    val showcaseSlots by repository.milestoneShowcase.collectAsState()
    val history by repository.history.collectAsState()
    val weeklyGoal by repository.weeklyGoal.collectAsState()
    val summary = remember(history) { HistorySummaryCalculator.from(history) }
    val level = remember(profile.totalWorkouts) {
        LevelCalculator.levelFromWorkouts(profile.totalWorkouts)
    }
    val unlockedMilestones = remember(milestones) { milestones.filter { it.unlocked } }
    val contentVisible = rememberWorkoutDetailVisible()

    var pickerVisible by remember { mutableStateOf(false) }
    var editingSlotIndex by remember { mutableIntStateOf(0) }

    val pickerCandidates = remember(unlockedMilestones, showcaseSlots, editingSlotIndex) {
        val usedElsewhere = showcaseSlots.mapIndexedNotNull { index, id ->
            if (index != editingSlotIndex) id else null
        }.toSet()
        unlockedMilestones.filter { it.id !in usedElsewhere }
    }

    ProfileMilestonePickerSheet(
        visible = pickerVisible,
        unlockedMilestones = pickerCandidates,
        onDismiss = { pickerVisible = false },
        onSelect = { milestone ->
            repository.setMilestoneShowcaseSlot(editingSlotIndex, milestone.id)
            pickerVisible = false
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg)
    ) {
        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 0) {
            androidx.compose.material3.Text(
                stringResource(R.string.profile_title),
                style = HeadlineLgMobile.copy(fontSize = 28.sp),
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 80) {
            ProfileHeroCard(
                displayName = profile.displayName,
                avatarUrl = profile.avatarUrl,
                level = level,
                levelProgress = LevelCalculator.progressToNextLevel(profile.totalWorkouts),
                isProMember = profile.isProMember,
                totalWorkouts = profile.totalWorkouts,
                modifier = Modifier.padding(top = Dimens.Md)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 160) {
            ProfileSummaryCard(
                sessions = profile.totalWorkouts,
                totalMinutes = summary.totalMinutes,
                totalCalories = summary.totalCalories,
                streakDays = profile.activeStreakDays,
                modifier = Modifier.padding(top = Dimens.Md)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 240) {
            ProfileLevelCard(
                level = level,
                progress = LevelCalculator.progressToNextLevel(profile.totalWorkouts),
                workoutsUntilNext = LevelCalculator.workoutsUntilNextLevel(profile.totalWorkouts),
                modifier = Modifier.padding(top = Dimens.Md)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 320) {
            ProfileStatsSection(
                summary = summary,
                weeklyGoal = weeklyGoal,
                onOpenInsights = onOpenInsights,
                modifier = Modifier.padding(top = Dimens.Lg)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 400) {
            ProfileAthleticSection(
                stats = stats,
                modifier = Modifier.padding(top = Dimens.Lg)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 480) {
            ProfileMilestoneShowcase(
                slots = showcaseSlots,
                milestones = milestones,
                onSlotClick = { index ->
                    editingSlotIndex = index
                    pickerVisible = true
                },
                onClearSlot = { index -> repository.setMilestoneShowcaseSlot(index, null) },
                onViewAll = onViewAllMilestones,
                modifier = Modifier.padding(top = Dimens.Lg)
            )
        }

        Spacer(Modifier.height(Dimens.Md))
    }
}
