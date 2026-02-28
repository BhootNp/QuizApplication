package com.example.quizapplication.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapplication.R
import com.example.quizapplication.model.QuizModel
import com.example.quizapplication.repository.QuizRepoImpl
import com.example.quizapplication.repository.UserRepoImpl
import com.example.quizapplication.ui.theme.LightBg
import com.example.quizapplication.ui.theme.NavyBlue
import com.example.quizapplication.ui.theme.ProfessionalBlue
import com.example.quizapplication.ui.theme.White
import com.example.quizapplication.viewmodel.QuizViewModel
import com.example.quizapplication.viewmodel.UserViewModel
import com.example.quizapplication.viewmodel.ViewModelFactory

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val activity = context as Activity

    val userRepo = UserRepoImpl()
    val quizRepo = QuizRepoImpl()
    val userViewModel: UserViewModel = viewModel(factory = ViewModelFactory(userRepo))
    val quizViewModel: QuizViewModel = viewModel(factory = ViewModelFactory(quizRepo))

    var isAdmin by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val currentUid = userViewModel.getCurrentUser()?.uid
        if (currentUid != null) {
            userViewModel.getUserData(currentUid) { user ->
                isAdmin = user?.role == "admin"
            }
        } else {
            // If no user is logged in, redirect to LoginActivity
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity.finish()
        }
    }

    if (showAddDialog) {
        AddQuizDialog(viewModel = quizViewModel, onDismiss = { showAddDialog = false })
    }

    data class NavItem(val label: String, val icon: Int)

    val listNav = listOf(
        NavItem("Home", R.drawable.baseline_home_24),
        NavItem("Leaderboards", R.drawable.baseline_leaderboard_24),
        NavItem("Profile", R.drawable.baseline_settings_24)
    )

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = ProfessionalBlue,
                    contentColor = White,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, "Add") },
                    text = { Text("New Quiz") }
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NavyBlue,
                    titleContentColor = White,
                    actionIconContentColor = White
                ),
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "NeuroQuiz Logo",
                        modifier = Modifier.height(40.dp)
                    )
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "No new notifications", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(painterResource(R.drawable.baseline_notifications_24), "Notifications")
                    }
                    IconButton(onClick = {
                        userViewModel.signOut()
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        activity.finish()
                    }) {
                        Icon(painterResource(R.drawable.baseline_logout_24), "Logout")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = White,
                tonalElevation = 8.dp
            ) {
                listNav.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedIndex = index },
                        label = {
                            Text(
                                item.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                painterResource(item.icon),
                                contentDescription = null,
                                tint = if (isSelected) ProfessionalBlue else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = ProfessionalBlue.copy(alpha = 0.1f),
                            selectedTextColor = NavyBlue,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Your background pattern
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(0.05f),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.fillMaxSize().background(LightBg.copy(alpha = 0.9f))) {
                when (selectedIndex) {
                    0 -> HomeScreen(quizViewModel, userViewModel, isAdmin)
                    1 -> RankingScreen(userViewModel)
                    2 -> ProfileScreen(userViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuizDialog(viewModel: QuizViewModel, onDismiss: () -> Unit) {
    var question by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    val options = remember { mutableStateListOf("", "", "", "") }
    var selectedCorrectIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Column {
                Text("Create New Question", fontWeight = FontWeight.ExtraBold, color = NavyBlue)
                Text("Assign 4 options and select the correct one", fontSize = 12.sp, color = Color.Gray)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question Text") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Science)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)

                Text("Options", fontWeight = FontWeight.Bold, color = NavyBlue)

                options.forEachIndexed { index, text ->
                    val isCorrect = selectedCorrectIndex == index

                    Surface(
                        color = if (isCorrect) ProfessionalBlue.copy(alpha = 0.08f) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isCorrect) ProfessionalBlue else Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = isCorrect,
                                onClick = { selectedCorrectIndex = index },
                                colors = RadioButtonDefaults.colors(selectedColor = ProfessionalBlue)
                            )
                            OutlinedTextField(
                                value = text,
                                onValueChange = { options[index] = it },
                                placeholder = { Text("Option ${index + 1}") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(0.5f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                onClick = {
                    if (question.isNotBlank() && options.all { it.isNotBlank() }) {
                        val quizId = System.currentTimeMillis().toString()
                        val newQuiz = QuizModel(
                            quizId = quizId,
                            question = question,
                            options = options.toList(),
                            correctAnswer = options[selectedCorrectIndex],
                            category = category.ifBlank { "General" },
                            difficulty = "Normal"
                        )
                        viewModel.addQuiz(quizId, newQuiz) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) onDismiss()
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                }
            ) { Text("Create", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}
