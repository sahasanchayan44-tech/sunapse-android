package com.example.synapse.auth

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
    val correctAnswers: Int = 352,
    val wrongAnswers: Int = 48,
    val level: Int = 22,
    val totalDays: Int = 76,
    val currentStreak: Int = 35,
    val friendsOnline: Int = 14,
    val syncCoins: Int = 1250,
    val totalPoints: Int = 450,
    val subjectsStudiedToday: List<String> = listOf("Physics", "Mathematics", "Chemistry"),
    val yearlyStats: List<MonthlyStat> = listOf(
        MonthlyStat("Jan", 40, 10),
        MonthlyStat("Feb", 45, 12),
        MonthlyStat("Mar", 50, 8),
        MonthlyStat("Apr", 38, 15),
        MonthlyStat("May", 55, 5),
        MonthlyStat("Jun", 60, 10),
        MonthlyStat("Jul", 64, 8)
    )
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
    
    var currentUser by mutableStateOf<FirebaseUser?>(auth.currentUser)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var userStats by mutableStateOf(UserProfileStats())
        private set

    init {
        auth.addAuthStateListener { firebaseAuth: FirebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
    }

    fun updateLevel(newLevel: Int) {
        userStats = userStats.copy(level = newLevel.coerceIn(1, 100))
    }

    /**
     * Adds points to the user and converts them to Sync Coins.
     * Conversion rule: Every 10 points added also adds 1 Sync Coin.
     */
    fun addPoints(points: Int) {
        val newPoints = userStats.totalPoints + points
        val coinsToAdd = points / 10 // Example conversion: 10 points = 1 Sync Coin
        userStats = userStats.copy(
            totalPoints = newPoints,
            syncCoins = userStats.syncCoins + coinsToAdd
        )
    }

    fun recordStudySession(subject: String) {
        if (!userStats.subjectsStudiedToday.contains(subject)) {
            userStats = userStats.copy(
                subjectsStudiedToday = userStats.subjectsStudiedToday + subject
            )
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
