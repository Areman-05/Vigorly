package com.example.vigorly.util

import com.example.vigorly.data.model.CoachingTip

object DailyTipSelector {
    fun pick(tips: List<CoachingTip>, index: Int): CoachingTip {
        if (tips.isEmpty()) return CoachingTip("tip-001", "")
        return tips[index % tips.size]
    }
}
