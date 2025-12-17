package com.example.quizapplication.repository

import com.example.quizapplication.model.UserModel
import com.google.firebase.auth.FirebaseUser

class UserRepoImpl : UserRepo {
    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getUserById(
        userId: String,
        callback: (Boolean, UserModel) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getAllUser(callback: (Boolean, List<UserModel>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): FirebaseUser? {
        TODO("Not yet implemented")
    }

    override fun deleteUser(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}