package com.example.quizapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizapplication.repository.QuizRepo
import com.example.quizapplication.repository.UserRepo

class ViewModelFactory(private val repo: Any) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(repo as UserRepo) as T
        }
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(repo as QuizRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}