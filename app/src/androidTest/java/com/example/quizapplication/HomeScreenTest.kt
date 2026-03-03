package com.example.quizapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.quizapplication.model.QuizModel
import com.example.quizapplication.repository.QuizRepo
import com.example.quizapplication.repository.UserRepo
import com.example.quizapplication.view.HomeScreen
import com.example.quizapplication.viewmodel.QuizViewModel
import com.example.quizapplication.viewmodel.UserViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockQuizRepo: QuizRepo
    private lateinit var mockUserRepo: UserRepo
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var userViewModel: UserViewModel

    private val testQuizzes = listOf(
        QuizModel(quizId = "1", question = "What is 2+2?", category = "Math", options = listOf("3", "4", "5"), correctAnswer = "4"),
        QuizModel(quizId = "2", question = "Capital of France?", category = "Geography", options = listOf("Paris", "London"), correctAnswer = "Paris")
    )

    @Before
    fun setUp() {
        mockQuizRepo = mock(QuizRepo::class.java)
        mockUserRepo = mock(UserRepo::class.java)
        
        // Stub the initial fetch to return our test data
        doAnswer { invocation ->
            val callback = invocation.arguments[0] as (Boolean, List<QuizModel>?, String?) -> Unit
            callback(true, testQuizzes, "Success")
            null
        }.whenever(mockQuizRepo).getAllQuizzes(any())

        quizViewModel = QuizViewModel(mockQuizRepo)
        userViewModel = UserViewModel(mockUserRepo)
    }

    @Test
    fun test1_initialDisplay_showsAllQuizzes() {
        composeTestRule.setContent {
            HomeScreen(quizViewModel = quizViewModel, userViewModel = userViewModel, isAdmin = false)
        }

        composeTestRule.onAllNodesWithTag("quiz_card").assertCountEquals(2)
        composeTestRule.onNodeWithText("What is 2+2?").assertIsDisplayed()
        composeTestRule.onNodeWithText("CAPITAL OF FRANCE?").assertIsDisplayed()
    }

    @Test
    fun test2_searchFiltering_worksCorrectly() {
        composeTestRule.setContent {
            HomeScreen(quizViewModel = quizViewModel, userViewModel = userViewModel, isAdmin = false)
        }

        composeTestRule.onNodeWithTag("search_bar").performTextInput("Math")
        composeTestRule.onAllNodesWithTag("quiz_card").assertCountEquals(1)
        composeTestRule.onNodeWithText("What is 2+2?").assertIsDisplayed()
    }

    @Test
    fun test3_clickQuiz_opensPlayDialog() {
        composeTestRule.setContent {
            HomeScreen(quizViewModel = quizViewModel, userViewModel = userViewModel, isAdmin = false)
        }

        composeTestRule.onNodeWithText("What is 2+2?").performClick()
        
        // Check if dialog is shown
        composeTestRule.onNodeWithText("Quiz Challenge").assertIsDisplayed()
        composeTestRule.onNodeWithText("4").assertIsDisplayed()
    }

    @Test
    fun test4_submitCorrectAnswer_showsSuccessMessage() {
        composeTestRule.setContent {
            HomeScreen(quizViewModel = quizViewModel, userViewModel = userViewModel, isAdmin = false)
        }

        composeTestRule.onNodeWithText("What is 2+2?").performClick()
        composeTestRule.onNodeWithText("4").performClick()
        composeTestRule.onNodeWithText("Submit Answer").performClick()

        composeTestRule.onNodeWithText("BOOM! +10 Points 🎉").assertIsDisplayed()
    }

    @Test
    fun test5_submitWrongAnswer_showsErrorMessage() {
        composeTestRule.setContent {
            HomeScreen(quizViewModel = quizViewModel, userViewModel = userViewModel, isAdmin = false)
        }

        composeTestRule.onNodeWithText("What is 2+2?").performClick()
        composeTestRule.onNodeWithText("3").performClick()
        composeTestRule.onNodeWithText("Submit Answer").performClick()

        composeTestRule.onNodeWithText("Not quite right! Correct answer: 4").assertIsDisplayed()
    }

    @Test
    fun test6_adminMode_showsDeleteButton() {
        composeTestRule.setContent {
            HomeScreen(quizViewModel = quizViewModel, userViewModel = userViewModel, isAdmin = true)
        }

        // Check if delete icon exists (based on the content description or tag)
        composeTestRule.onAllNodesWithContentDescription("Delete").assertCountEquals(2)
    }

    @Test
    fun test7_emptySearchResults_showsMessage() {
        composeTestRule.setContent {
            HomeScreen(quizViewModel = quizViewModel, userViewModel = userViewModel, isAdmin = false)
        }

        composeTestRule.onNodeWithTag("search_bar").performTextInput("History")
        composeTestRule.onNodeWithText("No quizzes found for this category!").assertIsDisplayed()
    }
}
