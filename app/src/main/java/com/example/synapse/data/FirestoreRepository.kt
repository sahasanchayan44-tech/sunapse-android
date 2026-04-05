package com.example.synapse.data

import android.util.Log
import com.example.synapse.models.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val tag = "FirestoreRepository"

    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    }

    // --- Subjects ---
    suspend fun getSubjects(): Result<List<SubjectModel>> {
        return try {
            val subjects = firestore.collection("subjects")
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects<SubjectModel>()
            Result.Success(subjects)
        } catch (e: Exception) {
            Log.e(tag, "Error fetching subjects", e)
            Result.Error("Failed to load subjects: ${e.message}", e)
        }
    }

    // --- Topics (Top-level, filtered by subjectId) ---
    suspend fun getTopicsForSubject(subjectId: String): Result<List<TopicModel>> {
        return try {
            val topics = firestore.collection("topics")
                .whereArrayContains("subjectIds", subjectId)
                .get()
                .await()
                .toObjects<TopicModel>()
                .sortedBy { it.order }
            Result.Success(topics)
        } catch (e: Exception) {
            Log.e(tag, "Error fetching topics for subject $subjectId", e)
            Result.Error("Failed to load topics: ${e.message}", e)
        }
    }

    // --- Lessons (Sub-collection of topics) ---
    suspend fun getLessonsForTopic(topicId: String): Result<List<LessonModel>> {
        return try {
            val lessons = firestore.collection("topics")
                .document(topicId)
                .collection("lessons")
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects<LessonModel>()
            Result.Success(lessons)
        } catch (e: Exception) {
            Log.e(tag, "Error fetching lessons for topic $topicId", e)
            Result.Error("Failed to load lessons: ${e.message}", e)
        }
    }

    // --- User Stats ---
    suspend fun getUserStats(userId: String): Result<UserStatsModel?> {
        return try {
            val stats = firestore.collection("users")
                .document(userId)
                .get()
                .await()
                .toObject<UserStatsModel>()
            Result.Success(stats)
        } catch (e: Exception) {
            Log.e(tag, "Error fetching user stats for $userId", e)
            Result.Error("Failed to load user stats: ${e.message}", e)
        }
    }

    suspend fun getAllUsersStats(): Result<List<UserStatsModel>> {
        return try {
            val stats = firestore.collection("users")
                .orderBy("totalPoints", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects<UserStatsModel>()
            Result.Success(stats)
        } catch (e: Exception) {
            Log.e(tag, "Error fetching all users stats", e)
            Result.Error("Failed to load leaderboard: ${e.message}", e)
        }
    }

    suspend fun saveUserStats(userId: String, stats: UserStatsModel): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .set(stats, SetOptions.merge())
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error saving user stats for $userId", e)
            Result.Error("Failed to save stats: ${e.message}", e)
        }
    }

    // --- Seeding Data ---
    suspend fun seedDatabase(currentUserId: String?) {
        try {
            // 1. Seed Subjects
            val subjects = listOf(
                SubjectModel("physics_subject", "QUANTUM", "PHYSICS", "CORE", "Bolt", "#8E2DE2", "#4A00E0", "Science", 1),
                SubjectModel("chemistry_subject", "ORGANIC", "CHEMISTRY", "ELEMENT", "Science", "#11998E", "#38EF7D", "Science", 2),
                SubjectModel("math_subject", "ADVANCED", "MATHEMATICS", "LOGIC", "Functions", "#F43F5E", "#881337", "Maths", 3),
                SubjectModel("biology_subject", "MOLECULAR", "BIOLOGY", "LIFE", "Spa", "#FFA000", "#FF5722", "Science", 4),
                SubjectModel("economics_subject", "MACRO", "ECONOMICS", "TRADE", "Business", "#00C9FF", "#92FE9D", "Business", 5),
                SubjectModel("history_subject", "ANCIENT", "HISTORY", "PAST", "History", "#f953c6", "#b91d73", "Humanities", 6),
                SubjectModel("cs_subject", "DATA", "STRUCTURES", "CODE", "Computer", "#434343", "#000000", "CS", 7),
                SubjectModel("sample_subject", "SAMPLE", "TESTING", "DEMO", "Bolt", "#FF6B6B", "#EE5A5A", "Testing", 8)
            )

            for (subject in subjects) {
                firestore.collection("subjects").document(subject.id).set(subject).await()
            }

            // 2. Seed Topics
            val topics = listOf(
                TopicModel("mechanics_topic", "Classical Mechanics", listOf("physics_subject"), 1),
                TopicModel("thermo_topic", "Thermodynamics", listOf("physics_subject", "chemistry_subject"), 2),
                TopicModel("calculus_topic", "Calculus", listOf("math_subject"), 1),
                TopicModel("cell_bio_topic", "Cell Biology", listOf("biology_subject"), 1)
            )

            for (topic in topics) {
                firestore.collection("topics").document(topic.id).set(topic).await()

                // Seed some sample lessons for each topic
                val lessons = listOf(
                    LessonModel("", topic.id, "Introduction", 1, listOf("Basic concepts", "History", "Applications")),
                    LessonModel("", topic.id, "Core Principles", 2, listOf("Key theories", "Mathematical proofs", "Examples"))
                )

                for (lesson in lessons) {
                    firestore.collection("topics").document(topic.id).collection("lessons").add(lesson).await()
                }
            }

            // 3. Seed User Stats if user is logged in
            currentUserId?.let { uid ->
                val userStats = UserStatsModel(
                    userId = uid,
                    username = "Synapse Seeker",
                    level = 25,
                    totalPoints = 1200,
                    syncCoins = 50,
                    correctAnswers = 412,
                    wrongAnswers = 32,
                    currentStreak = 12,
                    totalDays = 45,
                    subjectsStudiedToday = listOf("PHYSICS", "MATHEMATICS", "BIOLOGY"),
                    yearlyStats = listOf(
                        MonthlyStatModel("Oct", 120, 15),
                        MonthlyStatModel("Nov", 150, 10),
                        MonthlyStatModel("Dec", 142, 7)
                    )
                )
                saveUserStats(uid, userStats)
            }
            
            // Seed some fake leaderboard users
            val dummyUsers = listOf(
                UserStatsModel("user1", "Alex Quantum", 30, 2500, 100),
                UserStatsModel("user2", "Bio Hazard", 28, 2100, 80),
                UserStatsModel("user3", "Math Wizard", 22, 1850, 60),
                UserStatsModel("user4", "Code Ninja", 15, 900, 30)
            )
            for (user in dummyUsers) {
                firestore.collection("users").document(user.userId).set(user).await()
            }

        } catch (e: Exception) {
            Log.e(tag, "Error seeding database", e)
            throw e
        }
    }
}
