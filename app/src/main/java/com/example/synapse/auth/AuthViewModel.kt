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

@Immutable
data class RankInfo(
    val rankName: String,
    val levelName: String,
    val level: Int,
    val rankIndex: Int // 1 to 10
)

@Immutable
data class UserProfileStats(
    val winLossRate: String = "70%",
    val level: Int = 22, // Current level source of truth
    val totalDays: Int = 76,
    val currentStreak: Int = 35,
    val friendsOnline: Int = 14
) {
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

    /**
     * Updates the user's level. This will automatically update the 
     * Rank, Title, and Trophy across the entire app.
     */
    fun updateLevel(newLevel: Int) {
        userStats = userStats.copy(level = newLevel.coerceIn(1, 100))
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
