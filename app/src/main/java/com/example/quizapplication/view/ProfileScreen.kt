package com.example.quizapplication.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizapplication.viewmodel.UserViewModel

@Composable
fun ProfileScreen(viewModel: UserViewModel) {
    val currentUser = viewModel.getCurrentUser()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "User Profile", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Email: ${currentUser?.email ?: "Guest"}")
                Text(text = "User ID: ${currentUser?.uid ?: "N/A"}")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* Add sign out logic here */ },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Sign Out")
        }
    }
}