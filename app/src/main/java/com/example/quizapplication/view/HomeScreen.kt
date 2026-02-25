package com.example.quizapplication.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizapplication.model.QuizModel
import com.example.quizapplication.viewmodel.QuizViewModel

@Composable
fun HomeScreen(viewModel: QuizViewModel, isAdmin: Boolean) { // 1. Added isAdmin parameter
    val quizzes by viewModel.allQuizzes.observeAsState(initial = emptyList())
    val isLoading by viewModel.loading.observeAsState(initial = false)

    var selectedQuizForPlay by remember { mutableStateOf<QuizModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllQuizzes()
    }

    selectedQuizForPlay?.let { quiz ->
        QuizPlayDialog(
            quiz = quiz,
            onDismiss = { selectedQuizForPlay = null }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Explore Quizzes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(quizzes) { quiz ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedQuizForPlay = quiz },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        // 2. Used a Row to place Question and Delete button side-by-side
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = quiz.category, style = MaterialTheme.typography.labelLarge)
                                Text(text = quiz.question, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text(text = "Difficulty: ${quiz.difficulty}", style = MaterialTheme.typography.bodySmall)
                            }

                            // 3. ADMIN ONLY: Delete Icon appears only for Admins
                            if (isAdmin) {
                                IconButton(onClick = {
                                    // Add the curly braces at the end for the callback
                                    viewModel.deleteQuiz(quiz.quizId) { success, message ->
                                        // Optional: Show a toast or log the result
                                    }
                                }) {
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.ic_menu_delete),
                                        contentDescription = "Delete",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizPlayDialog(quiz: QuizModel, onDismiss: () -> Unit) {
    var selectedOption by remember { mutableStateOf("") }
    var hasSubmitted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Question", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = quiz.question, style = MaterialTheme.typography.titleMedium)

                quiz.options.forEach { option ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (selectedOption == option) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (!hasSubmitted) selectedOption = option }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            RadioButton(
                                selected = (selectedOption == option),
                                onClick = { if (!hasSubmitted) selectedOption = option }
                            )
                            Text(text = option, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                if (hasSubmitted) {
                    val isCorrect = selectedOption == quiz.correctAnswer
                    Text(
                        text = if (isCorrect) "Correct! 🎉" else "Wrong! Correct: ${quiz.correctAnswer}",
                        color = if (isCorrect) Color(0xFF4CAF50) else Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (!hasSubmitted) hasSubmitted = true else onDismiss() }) {
                Text(if (!hasSubmitted) "Submit" else "Close")
            }
        }
    )
}