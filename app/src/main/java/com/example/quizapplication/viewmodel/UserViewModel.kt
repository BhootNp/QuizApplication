package com.example.quizapplication.viewmodel

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

    fun deleteUser(userId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteUser(userId, callback)
    }

    fun updateProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.updateProfile(userId, model, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgetPassword(email, callback)
    }
}