package com.example.quizapplication.repository

import com.example.quizapplication.model.QuizModel
import com.google.firebase.database.*

class QuizRepoImpl : QuizRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Quizzes")

    override fun getAllQuizzes(callback: (Boolean, List<QuizModel>?, String?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val quizList = mutableListOf<QuizModel>()
                for (data in snapshot.children) {
                    val model = data.getValue(QuizModel::class.java)
                    if (model != null) {
                        quizList.add(model)
                    }
                }
                callback(true, quizList, "Quizzes fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, null, error.message)
            }
        })
    }

    override fun getQuizzesByCategory(category: String, callback: (Boolean, List<QuizModel>?, String?) -> Unit) {
        // We use a query to filter by the 'category' field
        ref.orderByChild("category").equalTo(category)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val filteredList = mutableListOf<QuizModel>()
                    for (data in snapshot.children) {
                        val model = data.getValue(QuizModel::class.java)
                        if (model != null) {
                            filteredList.add(model)
                        }
                    }
                    callback(true, filteredList, "Filtered successfully")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, null, error.message)
                }
            })
    }

    override fun addQuiz(quizId: String, model: QuizModel, callback: (Boolean, String) -> Unit) {
        ref.child(quizId).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Question added successfully")
            } else {
                callback(false, it.exception?.message ?: "Failed to add question")
            }
        }
    }

    override fun deleteQuiz(quizId: String, callback: (Boolean, String) -> Unit) {
        ref.child(quizId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Question deleted")
            } else {
                callback(false, it.exception?.message ?: "Delete failed")
            }
        }
    }

    override fun updateQuiz(quizId: String, model: QuizModel, callback: (Boolean, String) -> Unit) {
        ref.child(quizId).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Question updated")
            } else {
                callback(false, it.exception?.message ?: "Update failed")
            }
        }
    }


}