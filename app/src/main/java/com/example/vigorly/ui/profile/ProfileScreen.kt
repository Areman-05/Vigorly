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
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.di.AppViewModelFactory
import com.example.vigorly.presentation.feature.profile.ProfileViewModel
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.workout.WorkoutDetailSectionEnter
import com.example.vigorly.ui.workout.rememberWorkoutDetailVisible
import com.example.vigorly.util.HistorySummaryCalculator
import com.example.vigorly.util.LevelCalculator

@Composable
fun ProfileScreen(
    repository: VigorlyRepository,
    onViewAllMilestones: () -> Unit = {},
    onOpenInsights: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelFactory(repository))
) {
    val profile by viewModel.profile.collectAsState()
    val stats by viewModel.athleticStats.collectAsState()
    val milestones by viewModel.milestones.collectAsState()
    val showcaseSlots by viewModel.milestoneShowcase.collectAsState()
    val history by viewModel.history.collectAsState()
    val summary = remember(history) { HistorySummaryCalculator.from(history) }
    val level = remember(profile.totalWorkouts) {
        LevelCalculator.levelFromWorkouts(profile.totalWorkouts)
    }
    val unlockedMilestones = remember(milestones) { milestones.filter { it.unlocked } }
    val contentVisible = rememberWorkoutDetailVisible()
    val selectedAvatarId = remember(profile.avatarUrl) {
        ProfileAvatarCatalog.presetId(profile.avatarUrl)
            ?: ProfileAvatarCatalog.DEFAULT_ID
    }

    var pickerVisible by remember { mutableStateOf(false) }
    var avatarPickerVisible by remember { mutableStateOf(false) }
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
            viewModel.setMilestoneShowcaseSlot(editingSlotIndex, milestone.id)
            pickerVisible = false
        }
    )

    ProfileAvatarPickerSheet(
        visible = avatarPickerVisible,
        selectedId = selectedAvatarId,
        onDismiss = { avatarPickerVisible = false },
        onSelect = { id ->
            viewModel.setAvatarPreset(id)
            avatarPickerVisible = false
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(VigorlyTestTags.PROFILE)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg)
    ) {
        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 0) {
            androidx.compose.material3.Text(
                stringResource(R.string.profile_title),
                style = HeadlineLgMobile.copy(fontSize = 30.sp, lineHeight = 36.sp),
                color = PrimaryAccent,
                fontWeight = FontWeight.Bold
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 60) {
            ProfileHeroCard(
                displayName = profile.displayName,
                avatarUrl = profile.avatarUrl,
                level = level,
                levelProgress = LevelCalculator.progressToNextLevel(profile.totalWorkouts),
                isProMember = profile.isProMember,
                workoutsUntilNext = LevelCalculator.workoutsUntilNextLevel(profile.totalWorkouts),
                onAvatarClick = { avatarPickerVisible = true },
                modifier = Modifier.padding(top = Dimens.Lg)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 120) {
            ProfileQuickMetrics(
                sessions = profile.totalWorkouts,
                totalMinutes = summary.totalMinutes,
                totalCalories = summary.totalCalories,
                streakDays = profile.activeStreakDays,
                modifier = Modifier.padding(top = Dimens.Lg)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 180) {
            ProfileInsightsLink(
                onOpenInsights = onOpenInsights,
                modifier = Modifier.padding(top = Dimens.Md)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 260) {
            ProfileAthleticSection(
                stats = stats,
                modifier = Modifier.padding(top = Dimens.Lg)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 340) {
            ProfileMilestoneShowcase(
                slots = showcaseSlots,
                milestones = milestones,
                onSlotClick = { index ->
                    editingSlotIndex = index
                    pickerVisible = true
                },
                onClearSlot = { index -> repository.setMilestoneShowcaseSlot(index, null) },
                onViewAll = onViewAllMilestones,
                modifier = Modifier.padding(top = Dimens.Xl)
            )
        }

        Spacer(Modifier.height(Dimens.Md))
    }
}
