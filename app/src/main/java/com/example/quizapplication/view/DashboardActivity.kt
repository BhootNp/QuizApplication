package com.example.quizapplication.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    // 1. Setup ViewModels
    val userRepo = UserRepoImpl()
    val quizRepo = QuizRepoImpl()
    val userViewModel: UserViewModel = viewModel(factory = ViewModelFactory(userRepo))
    val quizViewModel: QuizViewModel = viewModel(factory = ViewModelFactory(quizRepo))

    // Role state: Default to false (User)
    var isAdmin by remember { mutableStateOf(false) }

    // Fetch User Role on launch
    LaunchedEffect(Unit) {
        val currentUid = userViewModel.getCurrentUser()?.uid
        if (currentUid != null) {
            userViewModel.getUserData(currentUid) { user ->
                // Make sure your UserModel has a 'role' field (either "admin" or "user")
                isAdmin = user?.role == "admin"
            }
        }
    }

    var selectedIndex by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddQuizDialog(viewModel = quizViewModel, onDismiss = { showAddDialog = false })
    }

    data class NavItem(val label: String, val icon: Int)
    val listNav = listOf(
        NavItem("Home", R.drawable.baseline_home_24),
        NavItem("Rankings", R.drawable.baseline_notifications_24),
        NavItem("Profile", R.drawable.baseline_settings_24)
    )

    Scaffold(
        floatingActionButton = {
            // ADMIN ONLY: Show the + button
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = ProfessionalBlue,
                    contentColor = White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Quiz")
                }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NavyBlue,
                    titleContentColor = White,
                    navigationIconContentColor = White
                ),
                title = { Text("NeuroQuiz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(painterResource(R.drawable.baseline_arrow_back_24), contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = White) {
                listNav.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        label = { Text(item.label) },
                        icon = { Icon(painterResource(item.icon), contentDescription = null) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(LightBg)) {
            when (selectedIndex) {
                // Corrected the order of parameters
                0 -> HomeScreen(quizViewModel, userViewModel, isAdmin)
                1 -> RankingScreen(userViewModel)
                2 -> ProfileScreen(userViewModel)
                else -> HomeScreen(quizViewModel, userViewModel, isAdmin)
            }
        }
    }
}

@Composable
fun AddQuizDialog(viewModel: QuizViewModel, onDismiss: () -> Unit) {
    var question by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var selectedCorrectIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Quiz", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = question, onValueChange = { question = it }, label = { Text("Question") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                Text("Options (Select Correct):", fontWeight = FontWeight.SemiBold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedCorrectIndex == 0, onClick = { selectedCorrectIndex = 0 })
                    OutlinedTextField(value = optionA, onValueChange = { optionA = it }, label = { Text("Option A") }, modifier = Modifier.weight(1f))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedCorrectIndex == 1, onClick = { selectedCorrectIndex = 1 })
                    OutlinedTextField(value = optionB, onValueChange = { optionB = it }, label = { Text("Option B") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (question.isNotBlank() && optionA.isNotBlank() && optionB.isNotBlank()) {
                    val optionsList = listOf(optionA, optionB)
                    val quizId = System.currentTimeMillis().toString()
                    val newQuiz = QuizModel(
                        quizId = quizId,
                        question = question,
                        options = optionsList,
                        correctAnswer = optionsList[selectedCorrectIndex],
                        category = category,
                        difficulty = "Normal"
                    )
                    viewModel.addQuiz(quizId, newQuiz) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (success) onDismiss()
                    }
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}