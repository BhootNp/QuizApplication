package com.example.quizapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizapplication.model.UserModel
import com.example.quizapplication.repository.UserRepo
import com.google.firebase.auth.FirebaseUser

class UserViewModel(val repo: UserRepo) : ViewModel() {

    // LiveData to hold a single user's data (for profiles or specific lookups)
    private val _users = MutableLiveData<UserModel>()
    val users: MutableLiveData<UserModel>
        get() = _users

    // LiveData to hold the list of all users (for leaderboards or admin views)
    private val _allUsers = MutableLiveData<List<UserModel>>()
    val allUsers: MutableLiveData<List<UserModel>>
        get() = _allUsers

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        repo.login(email, password, callback)
    }

    // In UserViewModel.kt
    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> = _userData

    // 2. Updated function to accept an optional callback
    fun getUserData(uid: String, onResult: ((UserModel?) -> Unit)? = null) {
        repo.getUserData(uid) { success, user, _ ->
            if (success) {
                _userData.value = user // Updates LiveData for observers
                onResult?.invoke(user) // Executes the lambda if provided
            } else {
                onResult?.invoke(null)
            }
        }
    }

    // Ensure this matches your repo exactly to avoid "Conflicting overloads"
    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.register(email, password, callback)
    }

    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.addUserToDatabase(userId, model, callback)
    }

    fun getUserById(userId: String) {
        repo.getUserById(userId) { success, user ->
            if (success) {
                _users.postValue(user)
            }
        }
    }

    fun getAllUser() {
        repo.getAllUser { success, data ->
            if (success) {
                _allUsers.postValue(data)
            }
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    // In UserViewModel.kt
    fun deleteUser(userId: String, onComplete: (Boolean, String) -> Unit) {
        repo.deleteUser(userId, onComplete)
    }

    fun updateProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.updateProfile(userId, model, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgetPassword(email, callback)
    }

    fun signOut() {
        repo.signOut()
    }

    // In UserViewModel.kt
    fun incrementScore(userId: String) {
        // Pass the userId and handle the 3-parameter callback from UserRepoImpl
        repo.getUserData(userId) { success: Boolean, user: UserModel?, message: String? ->
            if (success && user != null) {
                val newScore = user.score + 10
                val updatedUser = user.copy(score = newScore)

                // Push update to Firebase
                repo.updateProfile(userId, updatedUser) { isSuccessful, error ->
                    if (isSuccessful) {
                        // Refresh data to update UI
                        getAllUser()
                    }
                }
            }
        }
    }
}