package com.example.quizapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.quizapplication.model.UserModel
import com.example.quizapplication.repository.UserRepo
import com.google.firebase.auth.FirebaseUser

class UserViewModel(val repo: UserRepo) : ViewModel() {
    fun login(email: String, password: String,
              callback: (Boolean, String?) -> Unit)
    {
        repo.login(email, password, callback)
    }

    fun register(email: String, password: String,
                 callback: (Boolean, String, String) -> Unit)
    {
        repo.register(email, password, callback)
    }

    fun addUserToDatabase(userId : String, model: UserModel,
                          callback: (Boolean , String) -> Unit)
    {
        repo.addUserToDatabase(userId, model, callback)
    }

    fun getUserById(userId: String,
                    callback: (Boolean, UserModel) -> Unit)
    {
        repo.getUserById(userId, callback)
    }

    fun getAllUser(callback: (Boolean, List<UserModel>) -> Unit)
    {
        repo.getAllUser(callback)
    }

    fun getCurrentUser() : FirebaseUser ?
    {
        return repo.getCurrentUser()
    }

    fun deleteUser(userId: String,
                   callback: (Boolean, String) -> Unit)
    {
        repo.deleteUser(userId, callback)
    }

    fun updateProfile(userId: String, model: UserModel,
                      callback: (Boolean, String) -> Unit)
    {
        repo.updateProfile(userId, model, callback)
    }

    fun forgetPassword(email: String,
                       callback: (Boolean, String) -> Unit)
    {
        repo.forgetPassword(email, callback)

    }
}