package com.example.synapse.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class UserStats(
    val xp: Int,
    val streak: Int,
    val coins: Int,
    val level: Int,
    val levelProgress: Double,
    val xpToNextLevel: Int
) {
    companion object {
        fun initial() = UserStats(
            xp = 1250,
            streak = 5,
            coins = 450,
            level = 2,
            levelProgress = 25.0,
            xpToNextLevel = 750
        )
    }
}

data class DailyQuest(
    val id: String,
    val task: String,
    val current: Int,
    val target: Int,
    val reward: Int,
    val isClaimed: Boolean = false
)

data class RankInfo(
    val name: String,
    val levelRange: String,
    val icon: ImageVector,
    val color: Color,
    val bgColor: Color,
    val tier: Int,
    val desc: String
)
