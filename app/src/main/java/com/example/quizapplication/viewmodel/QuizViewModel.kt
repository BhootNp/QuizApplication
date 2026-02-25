package com.example.quizapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizapplication.model.QuizModel
import com.example.quizapplication.repository.QuizRepo

class QuizViewModel(val repo: QuizRepo) : ViewModel() {
    private val _allQuizzes = MutableLiveData<List<QuizModel>>()
    val allQuizzes: MutableLiveData<List<QuizModel>> get() = _allQuizzes

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    fun getAllQuizzes() {
        _loading.postValue(true)
        repo.getAllQuizzes { success, data, message ->
            _loading.postValue(false)
            if (success && data != null) {
                _allQuizzes.postValue(data)
            }
        }
    }

    fun getQuizzesByCategory(category: String) {
        _loading.postValue(true)
        repo.getQuizzesByCategory(category) { success, data, _ ->
            _loading.postValue(false)
            if (success && data != null) {
                _allQuizzes.postValue(data)
            }
        }
    }

    fun addQuiz(quizId: String, model: QuizModel, callback: (Boolean, String) -> Unit) {
        repo.addQuiz(quizId, model, callback)
    }

    fun deleteQuiz(quizId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteQuiz(quizId, callback)
    }

    fun updateQuiz(quizId: String, model: QuizModel, callback: (Boolean, String) -> Unit) {
        repo.updateQuiz(quizId, model, callback)
    }
}