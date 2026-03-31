package com.example.synapse.models

import com.google.firebase.firestore.DocumentId

data class SubjectModel(
    @DocumentId val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val bottomText: String = "CORE",
    val iconName: String = "Bolt",
    val startColor: String = "#8E2DE2",
    val endColor: String = "#4A00E0",
    val category: String = "General",
    val order: Int = 0
)

data class TopicModel(
    @DocumentId val id: String = "",
    val name: String = "",
    val subjectIds: List<String> = emptyList(), // Can belong to multiple subjects
    val order: Int = 0
)

data class LessonModel(
    @DocumentId val id: String = "",
    val topicId: String = "", // Links to a top-level topic
    val name: String = "",
    val order: Int = 0,
    val studyPoints: List<String> = emptyList()
)

data class UserStatsModel(
    val level: Int = 1,
    val totalPoints: Int = 0,
    val syncCoins: Int = 0,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val currentStreak: Int = 0,
    val totalDays: Int = 0,
    val subjectsStudiedToday: List<String> = emptyList(),
    val yearlyStats: List<MonthlyStatModel> = emptyList()
)

data class MonthlyStatModel(
    val month: String = "",
    val correct: Int = 0,
    val wrong: Int = 0
)
