package com.example.quizapplication.model

data class QuizModel(
    val quizId: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val category: String = "",       // e.g., "Science", "History"
    val difficulty: String = ""      // e.g., "Easy", "Hard"
) {
    // This helps when sending data to Firebase or a Database
    fun toMap(): Map<String, Any> {
        return mapOf(
            "quizId" to quizId,
            "question" to question,
            "options" to options,
            "correctAnswer" to correctAnswer,
            "category" to category,
            "difficulty" to difficulty
        )
    }
}