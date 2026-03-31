package com.example.synapse.auth

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synapse.data.FirestoreRepository
import com.example.synapse.models.*
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Immutable
data class RankInfo(
    val rankName: String,
    val levelName: String,
    val level: Int,
    val rankIndex: Int // 1 to 10
)

@Immutable
data class MonthlyStat(val month: String, val correct: Int, val wrong: Int) {
    val accuracy: Int get() {
        val total = correct + wrong
        if (total == 0) return 0
        return ((correct.toFloat() / total) * 100).roundToInt()
    }
}

@Immutable
data class UserProfileStats(
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val level: Int = 1,
    val totalDays: Int = 0,
    val currentStreak: Int = 0,
    val friendsOnline: Int = 0,
    val syncCoins: Int = 0,
    val totalPoints: Int = 0,
    val subjectsStudiedToday: List<String> = emptyList(),
    val yearlyStats: List<MonthlyStat> = emptyList()
) {
    val accuracy: Int get() {
        val total = correctAnswers + wrongAnswers
        if (total == 0) return 0
        return ((correctAnswers.toFloat() / total) * 100).roundToInt()
    }

    val rankInfo: RankInfo get() {
        val rankIdx = ((level - 1) / 10).coerceIn(0, 9) + 1
        val levelIdx = (level - 1) % 10
        
        val rankNames = listOf(
            "Novice", "Seeker", "Scholar", "Adept", "Sage", 
            "Expert", "Master", "Grandmaster", "Legend", "Transcendent"
        )
        
        val levelTitles = listOf(
            "Initiate", "Apprentice", "Student", "Practitioner", "Specialist",
            "Veteran", "Elite", "Prime", "Superior", "Champion"
        )
        
        val currentRank = rankNames[rankIdx - 1]
        val currentTitle = levelTitles[levelIdx]
        
        return RankInfo(
            rankName = currentRank,
            levelName = "$currentRank $currentTitle", // 100 Unique combinations
            level = level,
            rankIndex = rankIdx
        )
    }
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val repository = FirestoreRepository()
    
    var currentUser by mutableStateOf<FirebaseUser?>(auth.currentUser)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var userStats by mutableStateOf(UserProfileStats())
        private set

    var subjects by mutableStateOf<List<SubjectModel>>(emptyList())
        private set

    init {
        auth.addAuthStateListener { firebaseAuth: FirebaseAuth ->
            currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                loadUserData()
            }
        }
    }

    fun loadUserData() {
        currentUser?.let { user ->
            viewModelScope.launch {
                val stats = repository.getUserStats(user.uid)
                stats?.let {
                    userStats = UserProfileStats(
                        correctAnswers = it.correctAnswers,
                        wrongAnswers = it.wrongAnswers,
                        level = it.level,
                        totalDays = it.totalDays,
                        currentStreak = it.currentStreak,
                        syncCoins = it.syncCoins,
                        totalPoints = it.totalPoints,
                        subjectsStudiedToday = it.subjectsStudiedToday,
                        yearlyStats = it.yearlyStats.map { s -> MonthlyStat(s.month, s.correct, s.wrong) }
                    )
                }
                subjects = repository.getSubjects()
            }
        }
    }

    fun seedData() {
        viewModelScope.launch {
            isLoading = true
            try {
                repository.seedDatabase(currentUser?.uid)
                loadUserData()
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    suspend fun getTopics(subjectId: String) = repository.getTopicsForSubject(subjectId)
    suspend fun getLessons(topicId: String) = repository.getLessonsForTopic(topicId)

    fun updateLevel(newLevel: Int) {
        userStats = userStats.copy(level = newLevel.coerceIn(1, 100))
        saveStats()
    }

    fun addPoints(points: Int) {
        val newPoints = userStats.totalPoints + points
        val coinsToAdd = points / 10
        userStats = userStats.copy(
            totalPoints = newPoints,
            syncCoins = userStats.syncCoins + coinsToAdd
        )
        saveStats()
    }

    private fun saveStats() {
        currentUser?.let { user ->
            viewModelScope.launch {
                val model = UserStatsModel(
                    level = userStats.level,
                    totalPoints = userStats.totalPoints,
                    syncCoins = userStats.syncCoins,
                    correctAnswers = userStats.correctAnswers,
                    wrongAnswers = userStats.wrongAnswers,
                    currentStreak = userStats.currentStreak,
                    totalDays = userStats.totalDays,
                    subjectsStudiedToday = userStats.subjectsStudiedToday,
                    yearlyStats = userStats.yearlyStats.map { MonthlyStatModel(it.month, it.correct, it.wrong) }
                )
                repository.saveUserStats(user.uid, model)
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) return
        isLoading = true
        error = null
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (!task.isSuccessful) {
                    error = task.exception?.message
                }
            }
    }

    fun signUpWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) return
        isLoading = true
        error = null
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (!task.isSuccessful) {
                    error = task.exception?.message
                }
            }
    }

    fun signInWithCredential(credential: AuthCredential) {
        isLoading = true
        error = null
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                isLoading = false
                if (!task.isSuccessful) {
                    error = task.exception?.message
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun setErrorMessage(message: String) {
        error = message
    }

    fun clearError() {
        error = null
    }
}
