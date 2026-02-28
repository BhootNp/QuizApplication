package com.example.quizapplication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizapplication.model.QuizModel
import com.example.quizapplication.ui.theme.NavyBlue
import com.example.quizapplication.ui.theme.ProfessionalBlue
import com.example.quizapplication.ui.theme.White
import com.example.quizapplication.viewmodel.QuizViewModel
import com.example.quizapplication.viewmodel.UserViewModel

@Composable
fun HomeScreen(quizViewModel: QuizViewModel, userViewModel: UserViewModel, isAdmin: Boolean) {
    val quizzes by quizViewModel.allQuizzes.observeAsState(initial = emptyList())
    val isLoading by quizViewModel.loading.observeAsState(initial = false)
    var searchQuery by remember { mutableStateOf("") }

    var selectedQuizForPlay by remember { mutableStateOf<QuizModel?>(null) }

    LaunchedEffect(Unit) {
        quizViewModel.getAllQuizzes()
    }

    if (selectedQuizForPlay != null) {
        QuizPlayDialog(
            quiz = selectedQuizForPlay!!,
            userViewModel = userViewModel,
            onDismiss = { selectedQuizForPlay = null }
        )
    }

    val filteredQuizzes = quizzes.filter {
        it.category.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(
            text = "Pick Your Challenge",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = NavyBlue
        )

        Text(
            text = "Test your knowledge and climb the ranks",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by category...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = White,
                focusedContainerColor = White,
                focusedIndicatorColor = ProfessionalBlue
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ProfessionalBlue)
            }
        } else if (filteredQuizzes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No quizzes found for this category!", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredQuizzes) { quiz ->
                    QuizCard(
                        quiz = quiz,
                        isAdmin = isAdmin,
                        onClick = { selectedQuizForPlay = quiz },
                        onDelete = {
                            quizViewModel.deleteQuiz(quiz.quizId) { _, _ -> }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuizCard(quiz: QuizModel, isAdmin: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = ProfessionalBlue.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = quiz.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = ProfessionalBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = quiz.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            }

            if (isAdmin) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.background(Color(0xFFFFEBEE), CircleShape)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun QuizPlayDialog(quiz: QuizModel, userViewModel: UserViewModel, onDismiss: () -> Unit) {
    var selectedOption by remember { mutableStateOf("") }
    var hasSubmitted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(text = "Quiz Challenge", fontWeight = FontWeight.ExtraBold, color = NavyBlue)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = quiz.question,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )

                quiz.options.forEach { option ->
                    val isSelected = selectedOption == option
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) ProfessionalBlue.copy(alpha = 0.1f) else Color(0xFFF5F5F5),
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isSelected) Modifier.border(2.dp, ProfessionalBlue, RoundedCornerShape(16.dp))
                                else Modifier
                            )
                            .clickable { if (!hasSubmitted) selectedOption = option }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { if (!hasSubmitted) selectedOption = option },
                                colors = RadioButtonDefaults.colors(selectedColor = ProfessionalBlue)
                            )
                            Text(
                                text = option,
                                modifier = Modifier.padding(start = 12.dp),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                if (hasSubmitted) {
                    val isCorrect = selectedOption == quiz.correctAnswer
                    Text(
                        text = if (isCorrect) "BOOM! +10 Points 🎉" else "Not quite right! Correct answer: ${quiz.correctAnswer}",
                        color = if (isCorrect) Color(0xFF4CAF50) else Color.Red,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!hasSubmitted) {
                        hasSubmitted = true
                        if (selectedOption == quiz.correctAnswer) {
                            userViewModel.getCurrentUser()?.uid?.let { uid ->
                                userViewModel.incrementScore(uid)
                            }
                        }
                    } else {
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text(if (!hasSubmitted) "Submit Answer" else "Finish")
            }
        }
    )
}
