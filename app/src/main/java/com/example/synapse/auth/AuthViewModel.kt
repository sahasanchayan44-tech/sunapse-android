package com.example.synapse.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    
    var currentUser by mutableStateOf<FirebaseUser?>(auth.currentUser)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        auth.addAuthStateListener { firebaseAuth: FirebaseAuth ->
            currentUser = firebaseAuth.currentUser
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

    fun clearError() {
        error = null
    }
}
