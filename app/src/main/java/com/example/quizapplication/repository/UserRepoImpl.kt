package com.example.quizapplication.repository

import com.example.quizapplication.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl : UserRepo {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Users")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Login Success")
            } else {
                callback(false, it.exception?.message)
            }
        }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Register Success", auth.currentUser?.uid ?: "")
            } else {
                callback(false, it.exception?.message ?: "Registration failed", "")
            }
        }
    }

    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Registered successfully")
            } else {
                callback(false, it.exception?.message ?: "Database Error")
            }
        }
    }

    override fun getUserById(
        userId: String,
        callback: (Boolean, UserModel) -> Unit
    ) {
        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        callback(true, user)
                    } else {
                        callback(false, UserModel()) // User data is corrupt or null
                    }
                } else {
                    callback(false, UserModel()) // User not found
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, UserModel()) // Firebase error
            }
        })
    }

    override fun getAllUser(callback: (Boolean, List<UserModel>) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allUsers = mutableListOf<UserModel>()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(UserModel::class.java)
                        user?.let { allUsers.add(it) }
                    }
                }
                // Always return, even if the list is empty
                callback(true, allUsers)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, emptyList())
            }
        })
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun deleteUser(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User deleted successfully")
            } else {
                callback(false, it.exception?.message ?: "Deletion failed")
            }
        }
    }

    override fun updateProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        val updates = mapOf(
            "firstName" to model.firstName,
            "lastName" to model.lastName,
            "gender" to model.gender,
            "dob" to model.dob,
            "score" to model.score,
            "role" to model.role
        )

        ref.child(userId).updateChildren(updates).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Profile updated successfully!")
            } else {
                callback(false, it.exception?.message ?: "Database Error")
            }
        }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Email sent successfully")
                } else {
                    callback(false, it.exception?.message ?: "Failed to send email")
                }
            }
    }
    
    override fun getUserData(uid: String, callback: (Boolean, UserModel?, String?) -> Unit) {
        database.getReference("Users").child(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(UserModel::class.java)
                if (user != null) {
                    callback(true, user, "User data fetched")
                } else {
                    callback(false, null, "User not found")
                }
            }
            .addOnFailureListener {
                callback(false, null, it.message)
            }
    }

    override fun signOut() {
        auth.signOut()
    }
}
