package com.example.vigorly.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.di.AppViewModelFactory
import com.example.vigorly.presentation.feature.profile.ProfileViewModel
import com.example.vigorly.ui.components.WeeklyGoalCard
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
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
    val milestones by viewModel.milestones.collectAsState()
    val showcaseSlots by viewModel.milestoneShowcase.collectAsState()
    val history by viewModel.history.collectAsState()
    val athleticStats by viewModel.athleticStats.collectAsState()
    val weeklyGoal by viewModel.weeklyGoal.collectAsState()
    val summary = remember(history) { HistorySummaryCalculator.from(history) }
    val level = remember(profile.totalWorkouts) {
        LevelCalculator.levelFromWorkouts(profile.totalWorkouts)
    }
    val nextLocked = remember(milestones) { milestones.firstOrNull { !it.unlocked } }
    val recentSessions = remember(history) { history.take(4) }
    val contentVisible = rememberWorkoutDetailVisible()
    val selectedAvatarId = remember(profile.avatarUrl) {
        ProfileAvatarCatalog.presetId(profile.avatarUrl)
            ?: ProfileAvatarCatalog.DEFAULT_ID
    }

    var pickerVisible by remember { mutableStateOf(false) }
    var avatarPickerVisible by remember { mutableStateOf(false) }
    var editingSlotIndex by remember { mutableIntStateOf(0) }

    val pickerCandidates = remember(milestones, showcaseSlots, editingSlotIndex) {
        val unlocked = milestones.filter { it.unlocked }
        val usedElsewhere = showcaseSlots.mapIndexedNotNull { index, id ->
            if (index != editingSlotIndex) id else null
        }.toSet()
        unlocked.filter { it.id !in usedElsewhere }
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
            Text(
                stringResource(R.string.profile_title),
                style = HeadlineLgMobile,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.profile_subtitle),
                style = BodyMd,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.Xs)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 60) {
            ProfileSummaryHeader(
                displayName = profile.displayName,
                avatarUrl = profile.avatarUrl,
                level = level,
                streakDays = profile.activeStreakDays,
                isProMember = profile.isProMember,
                onAvatarClick = { avatarPickerVisible = true },
                modifier = Modifier.padding(top = Dimens.Lg)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 120) {
            ProfileSummaryCard(
                sessions = summary.totalSessions,
                totalMinutes = summary.totalMinutes,
                totalCalories = summary.totalCalories,
                streakDays = profile.activeStreakDays,
                modifier = Modifier.padding(top = Dimens.Lg)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 160) {
            WeeklyGoalCard(
                goal = weeklyGoal,
                modifier = Modifier.padding(top = Dimens.Md)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 200) {
            ProfileLevelCard(
                level = level,
                progress = LevelCalculator.progressToNextLevel(profile.totalWorkouts),
                workoutsUntilNext = LevelCalculator.workoutsUntilNextLevel(profile.totalWorkouts),
                modifier = Modifier.padding(top = Dimens.Md)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 240) {
            ProfileAthleticSection(
                stats = athleticStats,
                totalSessions = summary.totalSessions,
                modifier = Modifier.padding(top = Dimens.Lg)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 300) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Lg),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Md)
            ) {
                ProfileShortcutTile(
                    label = stringResource(R.string.profile_insights_action),
                    icon = Icons.Default.Insights,
                    onClick = onOpenInsights,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(VigorlyTestTags.PROFILE_OPEN_INSIGHTS)
                )
                ProfileShortcutTile(
                    label = stringResource(R.string.profile_all_milestones_action),
                    icon = Icons.Default.EmojiEvents,
                    onClick = onViewAllMilestones,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(VigorlyTestTags.PROFILE_OPEN_MILESTONES)
                )
            }
        }

        nextLocked?.let { next ->
            WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 340) {
                Text(
                    stringResource(R.string.profile_next_milestone, next.title),
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 15.sp,
                        color = OnSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = Dimens.Sm)
                )
            }
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 380) {
            ProfileMilestoneShowcase(
                slots = showcaseSlots,
                milestones = milestones,
                onSlotClick = { index ->
                    editingSlotIndex = index
                    pickerVisible = true
                },
                onClearSlot = { index -> repository.setMilestoneShowcaseSlot(index, null) },
                modifier = Modifier.padding(top = Dimens.Lg)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 420) {
            ProfileRecentActivitySection(
                sessions = recentSessions,
                modifier = Modifier.padding(top = Dimens.Lg)
            )
        }

        Spacer(Modifier.height(Dimens.Md))
    }
}
