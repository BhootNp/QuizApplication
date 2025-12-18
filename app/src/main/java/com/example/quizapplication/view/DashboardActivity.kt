package com.example.quizapplication.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.quizapplication.R
import com.example.quizapplication.ui.theme.LightBg
import com.example.quizapplication.ui.theme.NavyBlue
import com.example.quizapplication.ui.theme.ProfessionalBlue
import com.example.quizapplication.ui.theme.White

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

    // Retrieving email from intent (passed from Login)
    val email = activity.intent.getStringExtra("email") ?: "Explorer"

    // Navigation state logic from your reference
    var selectedIndex by remember { mutableStateOf(0) }

    data class NavItem(val label: String, val icon: Int)

    // Simplified list for a Quiz App: Home, Leaderboard, Profile
    val listNav = listOf(
        NavItem("Home", R.drawable.baseline_home_24),
        NavItem("Rankings", R.drawable.baseline_notifications_24),
        NavItem("Profile", R.drawable.baseline_settings_24)
    )

    Scaffold(
        floatingActionButton = {
            // Keep FAB as in reference, perhaps for "Create Quiz" in the future
            FloatingActionButton(
                onClick = { /* Action for FAB */ },
                containerColor = ProfessionalBlue,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NavyBlue,
                    titleContentColor = White,
                    navigationIconContentColor = White,
                    actionIconContentColor = White
                ),
                title = { Text("NeuroQuiz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(painterResource(R.drawable.baseline_arrow_back_24), contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(painterResource(R.drawable.baseline_attach_file_24), contentDescription = null)
                    }
                    IconButton(onClick = { }) {
                        Icon(painterResource(R.drawable.baseline_more_vert_24), contentDescription = null)
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
                        icon = { Icon(painterResource(item.icon), contentDescription = null) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ProfessionalBlue,
                            selectedTextColor = ProfessionalBlue,
                            unselectedIconColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LightBg)
        ) {
            // Screen switching logic based on your reference
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> RankingScreen()
                2 -> ProfileScreen()
                else -> HomeScreen()
            }
        }
    }
}
