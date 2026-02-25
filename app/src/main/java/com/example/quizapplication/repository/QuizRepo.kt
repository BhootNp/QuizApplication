package com.example.quizapplication.repository

import com.example.quizapplication.model.QuizModel

interface QuizRepo {

    // Fetch all quizzes from Firebase
    fun getAllQuizzes
                (callback: (Boolean, List<QuizModel>?, String?) -> Unit)

    // Fetch quizzes by category (e.g., "Science")
    fun getQuizzesByCategory
                (category: String, callback: (Boolean, List<QuizModel>?, String?) -> Unit)

    // Add a new quiz question to the database
    fun addQuiz
                (quizId: String, model: QuizModel, callback: (Boolean, String) -> Unit)

    // Delete a specific quiz question
    fun deleteQuiz
                (quizId: String, callback: (Boolean, String) -> Unit)

    // Update an existing quiz question
    fun updateQuiz
                (quizId: String, model: QuizModel, callback: (Boolean, String) -> Unit)

}