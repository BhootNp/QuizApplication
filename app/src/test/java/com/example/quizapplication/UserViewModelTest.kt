package com.example.quizapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.quizapplication.model.UserModel
import com.example.quizapplication.repository.UserRepo
import com.example.quizapplication.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repo: UserRepo

    @Mock
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var userViewModel: UserViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userViewModel = UserViewModel(repo)
    }

    @Test
    fun login_success() {
        val email = "test@example.com"
        val password = "password"
        
        userViewModel.login(email, password) { success, _ ->
            assertEquals(true, success)
        }

        verify(repo).login(eq(email), eq(password), any())
    }

    @Test
    fun getCurrentUser_returnsUser() {
        `when`(repo.getCurrentUser()).thenReturn(firebaseUser)

        val result = userViewModel.getCurrentUser()

        assertEquals(firebaseUser, result)
        verify(repo).getCurrentUser()
    }

    @Test
    fun getUserData_success() {
        val uid = "user123"
        val userModel = UserModel(userId = uid, email = "test@example.com", firstName = "Test", lastName = "User")
        
        `when`(repo.getUserData(eq(uid), any())).thenAnswer {
            val invocation = it
            val callback = invocation.arguments[1] as (Boolean, UserModel?, String?) -> Unit
            callback(true, userModel, "Success")
            null
        }

        userViewModel.getUserData(uid)

        assertEquals(userModel, userViewModel.userData.value)
    }

    @Test
    fun register_success() {
        val email = "new@example.com"
        val password = "password123"
        
        userViewModel.register(email, password) { success, message, uid ->
            assertEquals(true, success)
            assertEquals("uid123", uid)
        }

        verify(repo).register(eq(email), eq(password), any())
    }

    @Test
    fun signOut_callsRepo() {
        userViewModel.signOut()
        verify(repo).signOut()
    }
}
