package com.example.quizapplication.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizapplication.viewmodel.UserViewModel

@Composable
fun RankingScreen(viewModel: UserViewModel) {
    val allUsers by viewModel.allUsers.observeAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.getAllUser()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Leaderboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(allUsers) { user ->
                ListItem(
                    headlineContent = { Text("${user.firstName} ${user.lastName}") },
                    supportingContent = { Text("Score: --") }, // You can add a score field to UserModel later
                    leadingContent = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                HorizontalDivider()
            }
        }
    }
}