package com.example.synapse.data

import com.example.synapse.models.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // --- Subjects ---
    suspend fun getSubjects(): List<SubjectModel> {
        return try {
            firestore.collection("subjects")
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects<SubjectModel>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Topics (Top-level, filtered by subjectId) ---
    suspend fun getTopicsForSubject(subjectId: String): List<TopicModel> {
        return try {
            firestore.collection("topics")
                .whereArrayContains("subjectIds", subjectId)
                .get()
                .await()
                .toObjects<TopicModel>()
                .sortedBy { it.order }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Lessons (Sub-collection of topics) ---
    suspend fun getLessonsForTopic(topicId: String): List<LessonModel> {
        return try {
            firestore.collection("topics")
                .document(topicId)
                .collection("lessons")
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects<LessonModel>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- User Stats ---
    suspend fun getUserStats(userId: String): UserStatsModel? {
        return try {
            firestore.collection("users")
                .document(userId)
                .get()
                .await()
                .toObject<UserStatsModel>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserStats(userId: String, stats: UserStatsModel) {
        try {
            firestore.collection("users")
                .document(userId)
                .set(stats, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    // --- Seeding Data ---
    suspend fun seedDatabase(currentUserId: String?) {
        try {
            // 1. Seed Subjects
            val subjects = listOf(
                SubjectModel("physics_subject", "QUANTUM", "PHYSICS", "CORE", "Bolt", "#8E2DE2", "#4A00E0", 1),
                SubjectModel("chemistry_subject", "ORGANIC", "CHEMISTRY", "ELEMENT", "Science", "#11998E", "#38EF7D", 2),
                SubjectModel("math_subject", "ADVANCED", "MATHEMATICS", "LOGIC", "Functions", "#F43F5E", "#881337", 3),
                SubjectModel("biology_subject", "MOLECULAR", "BIOLOGY", "LIFE", "Spa", "#FFA000", "#FF5722", 4)
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
        } catch (e: Exception) {
            throw e
        }
    }
}
